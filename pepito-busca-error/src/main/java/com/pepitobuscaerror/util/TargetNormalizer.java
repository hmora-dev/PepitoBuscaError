package com.pepitobuscaerror.util;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public final class TargetNormalizer {

	private TargetNormalizer() {
	}

	public static NormalizedTarget normalize(String rawInput, String rawName) {
		String input = rawInput == null ? "" : rawInput.trim();
		if (input.isBlank()) {
			throw new IllegalArgumentException("Target cannot be empty");
		}

		String urlInput = input.matches("(?i)^https?://.*") ? input : "https://" + input;
		URI uri = parse(urlInput);
		String host = uri.getHost();
		if (host == null || host.isBlank()) {
			throw new IllegalArgumentException("Enter a valid domain or URL");
		}

		String domain = IDN.toASCII(host.toLowerCase(Locale.ROOT));
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
