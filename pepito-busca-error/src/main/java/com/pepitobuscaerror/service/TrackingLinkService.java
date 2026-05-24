package com.pepitobuscaerror.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.List;
import java.util.Locale;

@Service
public class TrackingLinkService {

	private final String publicBaseUrl;

	public TrackingLinkService(Environment environment) {
		this.publicBaseUrl = firstConfiguredBaseUrl(environment);
	}

	public TrackingLinks buildLinks(HttpServletRequest request, String trackingToken) {
		String path = UriComponentsBuilder.fromPath("/geolocation/live/{token}")
				.buildAndExpand(trackingToken)
				.toUriString();
		String currentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(path)
				.build()
				.toUriString();
		String configuredUrl = buildConfiguredUrl(path);

		return new TrackingLinks(currentUrl, "", List.of(), configuredUrl);
	}

	private String buildConfiguredUrl(String path) {
		if (publicBaseUrl.isBlank()) {
			return "";
		}
		String baseUrl = publicBaseUrl.endsWith("/")
				? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
				: publicBaseUrl;
		try {
			return UriComponentsBuilder.fromUriString(baseUrl)
					.path(path)
					.build()
					.toUriString();
		} catch (IllegalArgumentException exception) {
			return "";
		}
	}

	private String firstConfiguredBaseUrl(Environment environment) {
		String configuredBaseUrl = environment.getProperty("app.public-base-url");
		if (configuredBaseUrl == null || configuredBaseUrl.isBlank()) {
			configuredBaseUrl = environment.getProperty("APP_PUBLIC_BASE_URL");
		}
		return configuredBaseUrl == null ? "" : configuredBaseUrl.trim();
	}

	public record TrackingLinks(String currentUrl, String networkUrl, List<String> networkUrls, String configuredUrl) {

		public String getRecommendedUrl() {
			return getPublicUrl();
		}

		public String getPublicUrl() {
			if (configuredUrl != null && !configuredUrl.isBlank()) {
				return configuredUrl;
			}
			if (isPublicClientUrl(currentUrl)) {
				return currentUrl;
			}
			return "";
		}

		public String getRecommendedLabel() {
			return hasPublicUrl() ? "Public client link" : "Public URL required";
		}

		public boolean hasPublicUrl() {
			return !getPublicUrl().isBlank();
		}

		private boolean isPublicClientUrl(String url) {
			if (url == null || url.isBlank()) {
				return false;
			}
			try {
				URI uri = URI.create(url);
				String scheme = uri.getScheme();
				String host = uri.getHost();
				if (scheme == null || host == null) {
					return false;
				}
				if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
					return false;
				}
				return !isLocalOrPrivateHost(host);
			} catch (IllegalArgumentException exception) {
				return false;
			}
		}

		private boolean isLocalOrPrivateHost(String host) {
			String normalizedHost = host.toLowerCase(Locale.ROOT);
			if ("localhost".equals(normalizedHost) || normalizedHost.endsWith(".localhost")
					|| normalizedHost.endsWith(".local")) {
				return true;
			}
			if (!isIpLiteral(normalizedHost)) {
				return false;
			}
			try {
				InetAddress address = InetAddress.getByName(normalizedHost);
				return address.isAnyLocalAddress()
						|| address.isLoopbackAddress()
						|| address.isLinkLocalAddress()
						|| address.isSiteLocalAddress()
						|| isCarrierGradeNat(address);
			} catch (Exception exception) {
				return true;
			}
		}

		private boolean isIpLiteral(String host) {
			return host.matches("\\d{1,3}(\\.\\d{1,3}){3}") || host.contains(":");
		}

		private boolean isCarrierGradeNat(InetAddress address) {
			if (!(address instanceof Inet4Address)) {
				return false;
			}
			byte[] bytes = address.getAddress();
			int firstOctet = bytes[0] & 0xff;
			int secondOctet = bytes[1] & 0xff;
			return firstOctet == 100 && secondOctet >= 64 && secondOctet <= 127;
		}
	}
}
