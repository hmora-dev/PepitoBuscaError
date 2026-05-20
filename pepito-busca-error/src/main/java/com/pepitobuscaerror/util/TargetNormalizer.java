package com.pepitobuscaerror.util;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TargetNormalizer {

	private static final Pattern DOMAIN_PATTERN = Pattern.compile(
			"^(?=.{1,253}$)(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])$");

	private TargetNormalizer() {
	}

	public static NormalizedTarget normalize(String rawInput, String rawName) {
		String input = rawInput == null ? "" : rawInput.trim();
		if (input.isBlank()) {
			throw new IllegalArgumentException("Target cannot be empty");
		}

		if (input.matches("(?i)^[a-z][a-z0-9+.-]*://.*") && !input.matches("(?i)^https?://.*")) {
			throw new IllegalArgumentException("Only HTTP and HTTPS targets are supported");
		}

		String urlInput = input.matches("(?i)^https?://.*") ? input : "https://" + input;
		URI uri = parse(urlInput);
		String host = uri.getHost();
		if (host == null || host.isBlank()) {
			throw new IllegalArgumentException("Enter a valid domain or URL");
		}

		String domain = normalizeDomain(host);
		String scheme = uri.getScheme() == null ? "https" : uri.getScheme().toLowerCase(Locale.ROOT);
		if (!scheme.equals("http") && !scheme.equals("https")) {
			throw new IllegalArgumentException("Only HTTP and HTTPS targets are supported");
		}

		String normalizedUrl = scheme + "://" + domain;
		if (uri.getPort() > 0) {
			normalizedUrl += ":" + uri.getPort();
		}

		String name = rawName == null || rawName.isBlank() ? domain : rawName.trim();
		return new NormalizedTarget(name, domain, normalizedUrl);
	}

	private static String normalizeDomain(String host) {
		String domain = IDN.toASCII(host.toLowerCase(Locale.ROOT));
		if (domain.endsWith(".")) {
			domain = domain.substring(0, domain.length() - 1);
		}
		if (domain.matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
			throw new IllegalArgumentException("Enter a valid public domain name, for example example.com");
		}
		if (!DOMAIN_PATTERN.matcher(domain).matches()) {
			throw new IllegalArgumentException("Enter a valid public domain name, for example example.com");
		}
		if (domain.contains("..") || domain.endsWith(".local") || domain.endsWith(".localhost")) {
			throw new IllegalArgumentException("Only public internet domains can be analyzed with OSINT");
		}
		return domain;
	}

	private static URI parse(String value) {
		try {
			return new URI(value);
		} catch (URISyntaxException exception) {
			throw new IllegalArgumentException("Enter a valid domain or URL");
		}
	}

	public record NormalizedTarget(String name, String domain, String url) {
	}
}
