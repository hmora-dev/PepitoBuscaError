package com.pepitobuscaerror.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pepitobuscaerror.dto.BreachResult;
import com.pepitobuscaerror.dto.HibpResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class HaveIBeenPwnedClient {

	private static final String API_URL = "https://haveibeenpwned.com/api/v3/breachedaccount/";
	private static final String USER_AGENT = "PepitoBuscaError-OSINT";

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final String apiKey;
	private final boolean demoMode;

	public HaveIBeenPwnedClient(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper,
			@Value("${osint.hibp.api-key:}") String apiKey,
			@Value("${osint.demo-mode:true}") boolean demoMode) {
		this.restTemplate = restTemplateBuilder
				.connectTimeout(Duration.ofSeconds(6))
				.readTimeout(Duration.ofSeconds(10))
				.build();
		this.objectMapper = objectMapper;
		this.apiKey = apiKey == null ? "" : apiKey.trim();
		this.demoMode = demoMode;
	}

	public HibpResult checkEmail(String email) {
		if (!StringUtils.hasText(apiKey)) {
			return demoResult(email, "HIBP API key not configured. Showing demo data.");
		}
		if (demoMode) {
			return demoResult(email, "Demo mode enabled. Showing Have I Been Pwned-style demo data.");
		}

		try {
			JsonNode root = fetchBreaches(email);
			List<BreachResult> breaches = parseBreaches(root);
			if (breaches.isEmpty()) {
				return noBreaches(email, false, "HIBP API returned no breach exposure for this email.");
			}
			return new HibpResult(email, false, "HIBP API data loaded.", true, breaches.size(), breaches,
					"HIGH", "Change passwords, enable MFA, review reused credentials.", Instant.now());
		} catch (HttpClientErrorException.NotFound exception) {
			return noBreaches(email, false, "HIBP API returned no breach exposure for this email.");
		} catch (IOException | RuntimeException exception) {
			return demoResult(email, "HIBP API request could not complete safely. Showing demo data.");
		}
	}

	private JsonNode fetchBreaches(String email) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("hibp-api-key", apiKey);
		headers.set("User-Agent", USER_AGENT);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		String url = API_URL + URLEncoder.encode(email, StandardCharsets.UTF_8) + "?truncateResponse=false";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),
				String.class);
		String body = response.getBody();
		if (!response.getStatusCode().is2xxSuccessful() || !StringUtils.hasText(body)) {
			throw new IOException("HIBP returned an empty or unsuccessful response");
		}
		return objectMapper.readTree(body);
	}

	private List<BreachResult> parseBreaches(JsonNode root) {
		if (!root.isArray()) {
			return List.of();
		}
		List<BreachResult> breaches = new ArrayList<>();
		for (JsonNode node : root) {
			if (breaches.size() >= 10) {
				break;
			}
			List<String> dataClasses = new ArrayList<>();
			JsonNode classes = node.path("DataClasses");
			if (classes.isArray()) {
				for (JsonNode dataClass : classes) {
					if (StringUtils.hasText(dataClass.asText())) {
						dataClasses.add(dataClass.asText());
					}
				}
			}
			breaches.add(new BreachResult(node.path("Name").asText("Unknown breach"),
					node.path("BreachDate").asText("Unknown date"), dataClasses));
		}
		return breaches;
	}

	private HibpResult noBreaches(String email, boolean demo, String message) {
		return new HibpResult(email, demo, message, false, 0, List.of(), "INFO",
				"No public breach exposure was returned. Keep MFA enabled and continue monitoring corporate accounts.",
				Instant.now());
	}

	private HibpResult demoResult(String email, String message) {
		List<BreachResult> breaches = List.of(
				new BreachResult("Pepito Demo CRM", "2022-04-18",
						List.of("Email addresses", "Passwords", "Names")),
				new BreachResult("Example Project Forum", "2020-11-03",
						List.of("Email addresses", "Usernames", "IP addresses"))
		);
		return new HibpResult(email, true, message, true, breaches.size(), breaches, "HIGH",
				"Corporate email exposure detected. Change passwords, enable MFA, review reused credentials.",
				Instant.now());
	}
}
