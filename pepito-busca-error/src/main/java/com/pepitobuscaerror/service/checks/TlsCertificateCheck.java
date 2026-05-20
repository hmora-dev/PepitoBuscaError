package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import org.springframework.stereotype.Component;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TlsCertificateCheck implements SecurityCheck {

	@Override
	public List<Finding> analyze(AuditTarget target) {
		String host = target.getDomain();
		try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket()) {
			socket.connect(new InetSocketAddress(host, 443), 7000);
			socket.setSoTimeout(7000);
			SSLParameters parameters = socket.getSSLParameters();
			parameters.setEndpointIdentificationAlgorithm("HTTPS");
			parameters.setServerNames(List.of(new SNIHostName(host)));
			socket.setSSLParameters(parameters);
			socket.startHandshake();

			Certificate[] certificates = socket.getSession().getPeerCertificates();
			if (certificates.length == 0 || !(certificates[0] instanceof X509Certificate certificate)) {
				return List.of(new Finding(
						FindingCategory.WEB,
						FindingSeverity.MEDIUM,
						"TLS certificate could not be inspected",
						"The TLS handshake completed, but no X.509 leaf certificate was available.",
						"Review the public TLS configuration with the hosting provider."
				));
			}

			return certificateFindings(target, certificate);
		} catch (SSLHandshakeException exception) {
			return List.of(new Finding(
					FindingCategory.WEB,
					FindingSeverity.HIGH,
					"TLS handshake failed",
					"TLS validation failed for " + host + ": " + exception.getMessage(),
					"Renew or replace the certificate, verify the hostname, and confirm the full certificate chain is trusted."
			));
		} catch (SocketTimeoutException exception) {
			return List.of(new Finding(
					FindingCategory.AVAILABILITY,
					FindingSeverity.MEDIUM,
					"TLS service timed out",
					"Port 443 did not complete the TLS handshake within the timeout.",
					"Confirm that HTTPS is expected, reachable, and not blocked by firewall or hosting rules."
			));
		} catch (IOException | RuntimeException exception) {
			return List.of(new Finding(
					FindingCategory.WEB,
					FindingSeverity.LOW,
					"TLS certificate was not observed",
					"Could not collect a TLS certificate from " + host + ": " + exception.getMessage(),
					"If the site should be public, confirm HTTPS availability and certificate deployment."
			));
		}
	}

	private List<Finding> certificateFindings(AuditTarget target, X509Certificate certificate) {
		Instant expiresAt = certificate.getNotAfter().toInstant();
		long daysRemaining = ChronoUnit.DAYS.between(Instant.now(), expiresAt);
		FindingSeverity expirySeverity = expirySeverity(daysRemaining);
		Finding inventory = new Finding(
				FindingCategory.OSINT,
				FindingSeverity.INFO,
				"TLS certificate metadata collected",
				String.join("\n",
						"Subject: " + certificate.getSubjectX500Principal().getName(),
						"Issuer: " + certificate.getIssuerX500Principal().getName(),
						"Valid until: " + certificate.getNotAfter(),
						"Days remaining: " + daysRemaining,
						"Target: " + target.getDomain()),
				"Keep certificate ownership, renewal contacts, and hosting provider access documented."
		);

		if (expirySeverity == FindingSeverity.INFO) {
			return List.of(inventory);
		}

		Finding expiry = new Finding(
				FindingCategory.WEB,
				expirySeverity,
				"TLS certificate expires soon",
				"The certificate for " + target.getDomain() + " expires in " + daysRemaining + " day(s).",
				"Renew the certificate and validate automatic renewal before the expiry window becomes critical."
		);
		return List.of(inventory, expiry);
	}

	private FindingSeverity expirySeverity(long daysRemaining) {
		if (daysRemaining < 0) {
			return FindingSeverity.CRITICAL;
		}
		if (daysRemaining <= 14) {
			return FindingSeverity.HIGH;
		}
		if (daysRemaining <= 30) {
			return FindingSeverity.MEDIUM;
		}
		if (daysRemaining <= 60) {
			return FindingSeverity.LOW;
		}
		return FindingSeverity.INFO;
	}
}
