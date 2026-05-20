package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class WebSecurityHeaderCheck implements SecurityCheck {

	private final HttpProbeClient httpProbeClient;

	public WebSecurityHeaderCheck(HttpProbeClient httpProbeClient) {
		this.httpProbeClient = httpProbeClient;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		List<Finding> findings = new ArrayList<>();
		HttpProbeClient.ProbeResult response = httpProbeClient.get(target, "", 0);

		if (!response.successful()) {
			findings.add(new Finding(
					FindingCategory.AVAILABILITY,
					FindingSeverity.HIGH,
					"Web target could not be reached",
					"Request to " + target.getUrl() + " failed: " + response.failureMessage(),
					"Confirm DNS, firewall rules, TLS configuration, hosting availability, and that the URL is correct."
			));
			return findings;
		}

		if (!"https".equalsIgnoreCase(response.uri().getScheme())) {
			String evidence = "Final URL: " + response.uri();
			if (response.usedFallback()) {
				evidence += "\nHTTPS attempt failed first: " + response.fallbackReason();
			}
			findings.add(new Finding(
					FindingCategory.WEB,
					FindingSeverity.HIGH,
					"Target is reachable without confirmed HTTPS",
					evidence,
					"Serve the site over HTTPS, redirect all HTTP traffic to HTTPS, and keep the TLS certificate valid."
			));
		}

		if (response.statusCode() >= 500) {
			findings.add(new Finding(
					FindingCategory.AVAILABILITY,
					FindingSeverity.HIGH,
					"Server returned an error response",
					"HTTP status code: " + response.statusCode(),
					"Review server logs and application health for the analyzed web target."
			));
		} else if (response.statusCode() >= 400) {
			findings.add(new Finding(
					FindingCategory.WEB,
					FindingSeverity.MEDIUM,
					"Web target returned a client error",
					"HTTP status code: " + response.statusCode(),
					"Confirm the public root page is expected to return this status."
			));
		}

		Map<String, List<String>> headers = response.headers();
		if ("https".equalsIgnoreCase(response.uri().getScheme())) {
			requireHeader(findings, headers, "strict-transport-security", FindingSeverity.HIGH,
					"HTTP Strict Transport Security is missing",
					"HSTS tells browsers to use HTTPS only for future visits.");
		}
		requireHeader(findings, headers, "content-security-policy", FindingSeverity.MEDIUM,
				"Content Security Policy is missing",
				"CSP reduces the impact of cross-site scripting and content injection flaws.");
		requireHeader(findings, headers, "x-content-type-options", FindingSeverity.LOW,
				"X-Content-Type-Options is missing",
				"Set X-Content-Type-Options: nosniff to reduce MIME confusion risks.");
		requireHeader(findings, headers, "x-frame-options", FindingSeverity.LOW,
				"Clickjacking protection header is missing",
				"Set X-Frame-Options or a CSP frame-ancestors directive.");
		requireHeader(findings, headers, "referrer-policy", FindingSeverity.LOW,
				"Referrer-Policy is missing",
				"Set a policy that limits leaking sensitive paths to third-party sites.");
		requireHeader(findings, headers, "permissions-policy", FindingSeverity.LOW,
				"Permissions-Policy is missing",
				"Disable browser features the site does not need.");

		checkServerDisclosure(findings, headers);
		checkCookieFlags(findings, headers);

		return findings;
	}

	private void requireHeader(List<Finding> findings, Map<String, List<String>> headers, String headerName,
			FindingSeverity severity, String title, String reason) {
		if (!headers.containsKey(headerName)) {
			findings.add(new Finding(
					FindingCategory.WEB,
					severity,
					title,
					headerName + " was not present in the root page response.",
					reason
			));
		}
	}

	private void checkServerDisclosure(List<Finding> findings, Map<String, List<String>> headers) {
		Optional<String> server = firstHeader(headers, "server");
		if (server.isPresent() && server.get().matches(".*\\d+.*")) {
			findings.add(new Finding(
					FindingCategory.DATA,
					FindingSeverity.LOW,
					"Server header exposes version details",
					"Server: " + server.get(),
					"Reduce banner details where possible and keep server components patched."
			));
		}

		Optional<String> poweredBy = firstHeader(headers, "x-powered-by");
		if (poweredBy.isPresent()) {
			findings.add(new Finding(
					FindingCategory.DATA,
					FindingSeverity.LOW,
					"Technology disclosure header is present",
					"X-Powered-By: " + poweredBy.get(),
					"Remove framework disclosure headers from production responses."
			));
		}
	}

	private void checkCookieFlags(List<Finding> findings, Map<String, List<String>> headers) {
		List<String> cookies = headers.getOrDefault("set-cookie", List.of());
		for (String cookie : cookies) {
			String lower = cookie.toLowerCase(Locale.ROOT);
			String cookieName = cookie.split("=", 2)[0];
			if (!lower.contains("httponly")) {
				findings.add(new Finding(
						FindingCategory.DATA,
						FindingSeverity.MEDIUM,
						"Cookie is missing HttpOnly",
						"Cookie " + cookieName + " does not include HttpOnly.",
						"Add HttpOnly to session or sensitive cookies to reduce client-side script access."
				));
			}
			if (!lower.contains("secure")) {
				findings.add(new Finding(
						FindingCategory.DATA,
						FindingSeverity.MEDIUM,
						"Cookie is missing Secure",
						"Cookie " + cookieName + " does not include Secure.",
						"Add Secure so browsers send sensitive cookies only over HTTPS."
				));
			}
			if (!lower.contains("samesite")) {
				findings.add(new Finding(
						FindingCategory.DATA,
						FindingSeverity.LOW,
						"Cookie is missing SameSite",
						"Cookie " + cookieName + " does not include SameSite.",
						"Set SameSite=Lax or SameSite=Strict unless the application specifically requires cross-site cookies."
				));
			}
		}
	}

	private Optional<String> firstHeader(Map<String, List<String>> headers, String headerName) {
		List<String> values = headers.get(headerName);
		if (values == null || values.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(values.get(0));
	}
}
