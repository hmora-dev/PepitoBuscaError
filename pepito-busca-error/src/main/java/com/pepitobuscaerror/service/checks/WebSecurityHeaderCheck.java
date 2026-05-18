package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class WebSecurityHeaderCheck implements SecurityCheck {

	private final HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(6))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();

	@Override
	public List<Finding> analyze(AuditTarget target) {
		List<Finding> findings = new ArrayList<>();
		URI uri = URI.create(target.getUrl());

		if (!"https".equalsIgnoreCase(uri.getScheme())) {
			findings.add(new Finding(
					FindingCategory.WEB,
					FindingSeverity.HIGH,
					"Target URL does not use HTTPS",
					"Configured URL: " + target.getUrl(),
					"Serve the site over HTTPS and redirect all HTTP traffic to HTTPS."
			));
		}

		HttpResponse<Void> response;
		try {
			HttpRequest request = HttpRequest.newBuilder(uri)
					.timeout(Duration.ofSeconds(8))
					.GET()
					.header("User-Agent", "PepitoBuscaError/0.1 defensive-audit")
					.build();
			response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
		} catch (IOException | InterruptedException | IllegalArgumentException exception) {
			if (exception instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			findings.add(new Finding(
					FindingCategory.AVAILABILITY,
					FindingSeverity.HIGH,
					"Web target could not be reached",
					"Request to " + target.getUrl() + " failed: " + exception.getMessage(),
					"Confirm DNS, firewall rules, TLS configuration, hosting availability, and that the URL is correct."
			));
			return findings;
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

		Map<String, List<String>> headers = lowerCaseHeaders(response.headers().map());
		requireHeader(findings, headers, "strict-transport-security", FindingSeverity.HIGH,
				"HTTP Strict Transport Security is missing",
				"HSTS tells browsers to use HTTPS only for future visits.");
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

	private Map<String, List<String>> lowerCaseHeaders(Map<String, List<String>> source) {
		Map<String, List<String>> normalized = new HashMap<>();
		source.forEach((name, values) -> normalized.put(name.toLowerCase(Locale.ROOT), values));
		return normalized;
	}
}
