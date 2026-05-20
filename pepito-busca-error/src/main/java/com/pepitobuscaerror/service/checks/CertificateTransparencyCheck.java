package com.pepitobuscaerror.service.checks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

@Component
public class CertificateTransparencyCheck implements SecurityCheck {

	private static final int MAX_CT_BYTES = 1_500_000;
	private static final List<String> SENSITIVE_NAME_HINTS = List.of(
			"admin", "backup", "dev", "grafana", "internal", "jira", "jenkins", "old", "private", "stage",
			"staging", "test", "vpn"
	);

	private final HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(6))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();
	private final ObjectMapper objectMapper;

	public CertificateTransparencyCheck(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		try {
			Set<String> names = collectCertificateNames(target.getDomain());
			if (names.isEmpty()) {
				return List.of(new Finding(
						FindingCategory.OSINT,
						FindingSeverity.INFO,
						"No Certificate Transparency names observed",
						"The passive CT lookup did not return public certificate names for " + target.getDomain() + ".",
						"Treat this as context only. Some domains have no matching public entries or the public source may be temporarily incomplete."
				));
			}

			List<Finding> findings = new ArrayList<>();
			findings.add(new Finding(
					FindingCategory.OSINT,
					FindingSeverity.INFO,
					"Certificate Transparency names collected",
					"Observed " + names.size() + " unique certificate name(s): " + summarize(names, 18),
					"Review public certificate names as an external attacker would see them. Retire stale hostnames and keep ownership of every exposed name documented."
			));

			List<String> sensitiveLookingNames = names.stream()
					.filter(this::looksSensitive)
					.limit(12)
					.toList();
			if (!sensitiveLookingNames.isEmpty()) {
				findings.add(new Finding(
						FindingCategory.DATA,
						FindingSeverity.LOW,
						"Certificate names contain sensitive-looking labels",
						"Names worth manual review: " + String.join("; ", sensitiveLookingNames),
						"Confirm these hosts are intentional, authenticated, patched, and not legacy staging or internal systems accidentally exposed through certificates."
				));
			}
			return findings;
		} catch (IOException | InterruptedException | RuntimeException exception) {
			if (exception instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return List.of(new Finding(
					FindingCategory.OSINT,
					FindingSeverity.INFO,
					"Certificate Transparency lookup unavailable",
					"Could not collect CT data for " + target.getDomain() + ": " + describe(exception),
					"Rerun the report later or confirm outbound HTTPS access from the application host. The rest of the passive OSINT report remains usable."
			));
		}
	}

	private Set<String> collectCertificateNames(String domain) throws IOException, InterruptedException {
		String query = URLEncoder.encode("%." + domain, StandardCharsets.UTF_8);
		URI uri = URI.create("https://crt.sh/?q=" + query + "&output=json");
		HttpRequest request = HttpRequest.newBuilder(uri)
				.timeout(Duration.ofSeconds(10))
				.GET()
				.header("User-Agent", "PepitoBuscaError/1.0 passive-osint")
				.build();
		HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
		if (response.statusCode() != 200) {
			return Set.of();
		}

		try (InputStream body = response.body()) {
			byte[] bytes = body.readNBytes(MAX_CT_BYTES + 1);
			if (bytes.length > MAX_CT_BYTES) {
				throw new IOException("Certificate Transparency response exceeded the safe parsing limit");
			}
			String json = new String(bytes, StandardCharsets.UTF_8);
			if (json.isBlank()) {
				return Set.of();
			}
			return parseNames(json, domain);
		}
	}

	private Set<String> parseNames(String json, String domain) throws IOException {
		JsonNode root = objectMapper.readTree(json);
		if (!root.isArray()) {
			return Set.of();
		}

		Set<String> names = new TreeSet<>();
		for (JsonNode entry : root) {
			String nameValue = entry.path("name_value").asText("");
			for (String candidate : nameValue.replace("\\n", "\n").split("\\R")) {
				String normalized = normalizeName(candidate, domain);
				if (normalized != null) {
					names.add(normalized);
				}
			}
		}
		return names;
	}

	private String normalizeName(String candidate, String domain) {
		String value;
		try {
			value = IDN.toASCII(candidate.trim().toLowerCase(Locale.ROOT));
		} catch (IllegalArgumentException exception) {
			return null;
		}
		if (value.startsWith("*.")) {
			value = value.substring(2);
		}
		if (value.endsWith(".")) {
			value = value.substring(0, value.length() - 1);
		}
		if (value.isBlank() || value.length() > 253) {
			return null;
		}
		if (!value.equals(domain) && !value.endsWith("." + domain)) {
			return null;
		}
		if (!value.matches("[a-z0-9.-]+")) {
			return null;
		}
		return value;
	}

	private boolean looksSensitive(String name) {
		String lowerName = name.toLowerCase(Locale.ROOT);
		return SENSITIVE_NAME_HINTS.stream()
				.anyMatch(hint -> lowerName.equals(hint) || lowerName.startsWith(hint + ".")
						|| lowerName.contains("." + hint + "."));
	}

	private String summarize(Set<String> names, int limit) {
		List<String> visible = names.stream().limit(limit).toList();
		String summary = String.join("; ", visible);
		if (names.size() > limit) {
			summary += "; +" + (names.size() - limit) + " more";
		}
		return summary;
	}

	private String describe(Exception exception) {
		if (exception.getMessage() == null || exception.getMessage().isBlank()) {
			return exception.getClass().getSimpleName();
		}
		return exception.getMessage();
	}
}
