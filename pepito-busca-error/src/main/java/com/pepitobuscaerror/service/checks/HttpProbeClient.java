package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class HttpProbeClient {

	private static final String USER_AGENT = "PepitoBuscaError/1.0 passive-osint";

	private final HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(6))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();

	public ProbeResult get(AuditTarget target, String path, int maxBodyBytes) {
		URI primaryUri = buildUri(target.getUrl(), path);
		ProbeResult primary = request(primaryUri, maxBodyBytes);
		if (primary.successful() || !"https".equalsIgnoreCase(primaryUri.getScheme()) || primaryUri.getPort() != -1) {
			return primary;
		}

		URI fallbackUri = buildUri("http://" + target.getDomain(), path);
		ProbeResult fallback = request(fallbackUri, maxBodyBytes);
		if (fallback.successful()) {
			return fallback.withFallbackFrom(primary.failureMessage());
		}
		return primary;
	}

	private ProbeResult request(URI uri, int maxBodyBytes) {
		try {
			HttpRequest request = HttpRequest.newBuilder(uri)
					.timeout(Duration.ofSeconds(8))
					.GET()
					.header("User-Agent", USER_AGENT)
					.build();
			HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
			try (InputStream body = response.body()) {
				String bodyText = "";
				if (maxBodyBytes > 0) {
					bodyText = new String(body.readNBytes(maxBodyBytes), StandardCharsets.UTF_8);
				}
				return ProbeResult.success(response.uri(), response.statusCode(), lowerCaseHeaders(response.headers().map()),
						bodyText);
			}
		} catch (IOException | InterruptedException | IllegalArgumentException exception) {
			if (exception instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return ProbeResult.failure(uri, describe(exception));
		}
	}

	private URI buildUri(String baseUrl, String path) {
		String normalizedBase = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
		String normalizedPath = path == null ? "" : path.trim();
		if (normalizedPath.startsWith("/")) {
			normalizedPath = normalizedPath.substring(1);
		}
		return URI.create(normalizedBase).resolve(normalizedPath);
	}

	private Map<String, List<String>> lowerCaseHeaders(Map<String, List<String>> source) {
		Map<String, List<String>> normalized = new HashMap<>();
		source.forEach((name, values) -> normalized.put(name.toLowerCase(Locale.ROOT), values));
		return normalized;
	}

	private String describe(Exception exception) {
		if (exception.getMessage() == null || exception.getMessage().isBlank()) {
			return exception.getClass().getSimpleName();
		}
		return exception.getMessage();
	}

	public record ProbeResult(URI uri, int statusCode, Map<String, List<String>> headers, String body,
			String failureMessage, String fallbackReason) {

		static ProbeResult success(URI uri, int statusCode, Map<String, List<String>> headers, String body) {
			return new ProbeResult(uri, statusCode, headers, body, null, null);
		}

		static ProbeResult failure(URI uri, String failureMessage) {
			return new ProbeResult(uri, 0, Map.of(), "", failureMessage, null);
		}

		public boolean successful() {
			return statusCode > 0;
		}

		public boolean usedFallback() {
			return fallbackReason != null && !fallbackReason.isBlank();
		}

		ProbeResult withFallbackFrom(String reason) {
			return new ProbeResult(uri, statusCode, headers, body, failureMessage, reason);
		}
	}
}
