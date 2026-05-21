package com.pepitobuscaerror.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pepitobuscaerror.dto.SecurityTrailsRecord;
import com.pepitobuscaerror.dto.SecurityTrailsResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class SecurityTrailsClient {

	private static final String API_BASE_URL = "https://api.securitytrails.com/v1/domain/";
	private static final List<String> INTERESTING_LABELS = List.of(
			"admin", "backup", "dev", "internal", "jenkins", "old", "portal", "stage", "staging", "test", "vpn"
	);

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final String apiKey;
	private final boolean demoMode;

	public SecurityTrailsClient(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper,
			@Value("${osint.securitytrails.api-key:}") String apiKey,
			@Value("${osint.demo-mode:true}") boolean demoMode) {
		this.restTemplate = restTemplateBuilder
				.connectTimeout(Duration.ofSeconds(6))
				.readTimeout(Duration.ofSeconds(10))
				.build();
		this.objectMapper = objectMapper;
		this.apiKey = apiKey == null ? "" : apiKey.trim();
		this.demoMode = demoMode;
	}

	public SecurityTrailsResult analyzeDomain(String domain) {
		if (!StringUtils.hasText(apiKey)) {
			return demoResult(domain, "SecurityTrails API key not configured. Showing demo data.");
		}
		if (demoMode) {
			return demoResult(domain, "Demo mode enabled. Showing SecurityTrails-style demo data.");
		}

		try {
			JsonNode domainJson = fetchJson(API_BASE_URL + encode(domain));
			List<SecurityTrailsRecord> currentRecords = new ArrayList<>();
			Set<String> associatedIps = new LinkedHashSet<>();
			Set<String> nameservers = new LinkedHashSet<>();
			Set<String> mailServers = new LinkedHashSet<>();
			parseCurrentDns(domainJson.path("current_dns"), domain, currentRecords, associatedIps, nameservers, mailServers);

			List<String> subdomains = fetchSubdomains(domain);
			return new SecurityTrailsResult(domain, false, "SecurityTrails API data loaded.",
					currentRecords,
					List.of("SecurityTrails supports historical DNS review. This application stores only a current summary, not raw historical datasets."),
					subdomains, new ArrayList<>(associatedIps), new ArrayList<>(nameservers),
					new ArrayList<>(mailServers), buildRiskNotes(subdomains, currentRecords));
		} catch (IOException | RuntimeException exception) {
			return demoResult(domain, "SecurityTrails API request could not complete safely. Showing demo data.");
		}
	}

	private void parseCurrentDns(JsonNode currentDns, String domain, List<SecurityTrailsRecord> currentRecords,
			Set<String> associatedIps, Set<String> nameservers, Set<String> mailServers) {
		addRecordGroup(currentDns, "a", "A", domain, currentRecords, associatedIps, "ip", "value");
		addRecordGroup(currentDns, "aaaa", "AAAA", domain, currentRecords, associatedIps, "ipv6", "ip", "value");
		addRecordGroup(currentDns, "ns", "NS", domain, currentRecords, nameservers, "nameserver", "value", "host");
		addRecordGroup(currentDns, "mx", "MX", domain, currentRecords, mailServers, "value", "hostname", "host");
		addRecordGroup(currentDns, "txt", "TXT", domain, currentRecords, new LinkedHashSet<>(), "value", "text");
		addRecordGroup(currentDns, "cname", "CNAME", domain, currentRecords, new LinkedHashSet<>(), "value", "host");
	}

	private void addRecordGroup(JsonNode currentDns, String fieldName, String type, String domain,
			List<SecurityTrailsRecord> records, Set<String> collectedValues, String... valueFields) {
		JsonNode group = currentDns.path(fieldName);
		if (group.isMissingNode() || group.isNull()) {
			return;
		}
		String ttl = group.path("ttl").asText("");
		JsonNode values = group.path("values");
		if (!values.isArray()) {
			return;
		}
		for (JsonNode valueNode : values) {
			String value = firstText(valueNode, valueFields);
			if (StringUtils.hasText(value)) {
				records.add(new SecurityTrailsRecord(type, domain, value, ttl));
				collectedValues.add(value);
			}
		}
	}

	private List<String> fetchSubdomains(String domain) throws IOException {
		JsonNode root = fetchJson(API_BASE_URL + encode(domain) + "/subdomains?children_only=false");
		JsonNode values = root.path("subdomains");
		if (!values.isArray()) {
			return List.of();
		}
		List<String> subdomains = new ArrayList<>();
		for (JsonNode value : values) {
			String label = value.asText("").trim().toLowerCase(Locale.ROOT);
			if (StringUtils.hasText(label) && subdomains.size() < 30) {
				subdomains.add(label + "." + domain);
			}
		}
		return subdomains;
	}

	private JsonNode fetchJson(String url) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("APIKEY", apiKey);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),
				String.class);
		String body = response.getBody();
		if (!response.getStatusCode().is2xxSuccessful() || !StringUtils.hasText(body)) {
			throw new IOException("SecurityTrails returned an empty or unsuccessful response");
		}
		return objectMapper.readTree(body);
	}

	private String firstText(JsonNode node, String... keys) {
		for (String key : keys) {
			JsonNode value = node.path(key);
			if (value.isTextual() && StringUtils.hasText(value.asText())) {
				return value.asText();
			}
			if (value.isArray()) {
				List<String> parts = new ArrayList<>();
				for (JsonNode item : value) {
					if (StringUtils.hasText(item.asText())) {
						parts.add(item.asText());
					}
				}
				if (!parts.isEmpty()) {
					return String.join(" ", parts);
				}
			}
		}
		return node.isTextual() ? node.asText() : "";
	}

	private List<String> buildRiskNotes(List<String> subdomains, List<SecurityTrailsRecord> currentRecords) {
		List<String> notes = new ArrayList<>();
		List<String> interesting = subdomains.stream()
				.filter(this::hasInterestingLabel)
				.limit(8)
				.toList();
		if (!interesting.isEmpty()) {
			notes.add("Interesting hostnames need manual review: " + String.join(", ", interesting) + ".");
		}
		boolean hasVerificationToken = currentRecords.stream()
				.anyMatch(record -> "TXT".equals(record.getType())
						&& record.getValue().toLowerCase(Locale.ROOT).contains("verification"));
		if (hasVerificationToken) {
			notes.add("TXT records include verification-style values; remove stale ownership tokens.");
		}
		if (notes.isEmpty()) {
			notes.add("No elevated SecurityTrails-style risk notes were detected in the summarized data.");
		}
		return notes;
	}

	private boolean hasInterestingLabel(String hostname) {
		String value = hostname.toLowerCase(Locale.ROOT);
		return INTERESTING_LABELS.stream()
				.anyMatch(label -> value.equals(label) || value.startsWith(label + ".")
						|| value.contains("." + label + "."));
	}

	private SecurityTrailsResult demoResult(String domain, String message) {
		List<SecurityTrailsRecord> records = List.of(
				new SecurityTrailsRecord("A", domain, "203.0.113.15", "3600"),
				new SecurityTrailsRecord("MX", domain, "mail." + domain, "3600"),
				new SecurityTrailsRecord("NS", domain, "ns1.provider-example.net", "86400"),
				new SecurityTrailsRecord("TXT", domain, "v=spf1 include:_spf.example.net -all", "3600")
		);
		List<String> subdomains = List.of(
				"app." + domain,
				"portal." + domain,
				"vpn." + domain,
				"dev." + domain,
				"admin." + domain
		);
		return new SecurityTrailsResult(domain, true, message, records,
				List.of("Historical DNS note: demo data indicates previous provider changes should be reviewed."),
				subdomains, List.of("203.0.113.15", "198.51.100.42"),
				List.of("ns1.provider-example.net", "ns2.provider-example.net"), List.of("mail." + domain),
				List.of("Review vpn, dev, and admin hostnames before assuming they are approved public assets."));
	}

	private String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
