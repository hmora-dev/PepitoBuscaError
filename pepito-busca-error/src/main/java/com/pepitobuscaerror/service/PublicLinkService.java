package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.PublicLinkInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.Locale;

@Service
public class PublicLinkService {

	private static final String LOCAL_WARNING = "This link only works on this computer. To use it from another phone or from another Wi-Fi network, expose the app through a public HTTPS tunnel and set APP_PUBLIC_BASE_URL.";
	private static final String LAN_WARNING = "This link may work only for devices connected to the same Wi-Fi. To use it from another Wi-Fi network, use a public HTTPS tunnel.";
	private static final String HTTPS_WARNING = "HTTPS required. Mobile browser geolocation usually requires HTTPS, so configure APP_PUBLIC_BASE_URL with a public HTTPS URL.";
	private static final String NOT_CONFIGURED_WARNING = "No usable public URL could be derived. Set APP_PUBLIC_BASE_URL to a public HTTPS URL, or open this dashboard through a public HTTPS reverse proxy/tunnel.";
	private static final String INVALID_CONFIG_WARNING = "APP_PUBLIC_BASE_URL is not a valid HTTP/HTTPS URL. Using the current request URL instead.";

	private final Environment environment;

	public PublicLinkService(Environment environment) {
		this.environment = environment;
	}

	public PublicLinkInfo buildGpsLink(String token, HttpServletRequest request) {
		String path = UriComponentsBuilder.fromPath("/geolocation/live/{token}")
				.buildAndExpand(token)
				.toUriString();
		String configuredBaseUrl = firstConfiguredBaseUrl();
		boolean hasConfiguredBaseUrl = !configuredBaseUrl.isBlank();
		String normalizedConfiguredBaseUrl = normalizeBaseUrl(configuredBaseUrl);
		if (!normalizedConfiguredBaseUrl.isBlank()) {
			return classify(buildUrl(normalizedConfiguredBaseUrl, path), true, false, "");
		}

		String baseUrl = deriveBaseUrlFromRequest(request);
		String link = buildUrl(baseUrl, path);
		String extraWarning = hasConfiguredBaseUrl ? INVALID_CONFIG_WARNING : "";
		return classify(link, false, !baseUrl.isBlank(), extraWarning);
	}

	public PublicLinkInfo buildGpsClientLink(String token, HttpServletRequest request) {
		return buildGpsLink(token, request);
	}

	public String normalizeBaseUrl(String baseUrl) {
		if (baseUrl == null || baseUrl.isBlank()) {
			return "";
		}
		try {
			URI uri = URI.create(baseUrl.trim());
			String scheme = uri.getScheme();
			String host = uri.getHost();
			if (scheme == null || host == null) {
				return "";
			}
			String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
			if (!"http".equals(normalizedScheme) && !"https".equals(normalizedScheme)) {
				return "";
			}
			UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
					.scheme(normalizedScheme)
					.host(host);
			if (uri.getPort() != -1 && !isDefaultPort(normalizedScheme, uri.getPort())) {
				builder.port(uri.getPort());
			}
			String path = trimTrailingSlash(uri.getRawPath());
			if (path != null && !path.isBlank() && !"/".equals(path)) {
				builder.path(path);
			}
			return builder.build(true).toUriString();
		} catch (IllegalArgumentException exception) {
			return "";
		}
	}

	public String deriveBaseUrlFromRequest(HttpServletRequest request) {
		if (request == null) {
			return "";
		}
		String scheme = firstHeaderValue(request.getHeader("X-Forwarded-Proto"));
		String host = firstHeaderValue(request.getHeader("X-Forwarded-Host"));
		String forwardedPort = firstHeaderValue(request.getHeader("X-Forwarded-Port"));
		ForwardedValues forwardedValues = parseForwarded(request.getHeader("Forwarded"));
		if (scheme == null && forwardedValues.proto() != null) {
			scheme = forwardedValues.proto();
		}
		if (host == null && forwardedValues.host() != null) {
			host = forwardedValues.host();
		}
		if (scheme == null) {
			scheme = request.getScheme();
		}
		if (host == null) {
			host = requestHost(request);
		}
		host = applyForwardedPort(host, scheme, forwardedPort);
		String prefix = firstHeaderValue(request.getHeader("X-Forwarded-Prefix"));
		if (prefix == null) {
			prefix = request.getContextPath();
		}
		return normalizeBaseUrl(scheme + "://" + host + cleanContextPath(prefix));
	}

	public boolean isHttps(String url) {
		URI uri = parseUri(url);
		return uri != null && "https".equalsIgnoreCase(uri.getScheme());
	}

	public boolean isLocalhost(String url) {
		URI uri = parseUri(url);
		if (uri == null || uri.getHost() == null) {
			return false;
		}
		String host = uri.getHost().toLowerCase(Locale.ROOT);
		return "localhost".equals(host)
				|| host.endsWith(".localhost")
				|| "127.0.0.1".equals(host)
				|| "0.0.0.0".equals(host)
				|| "::1".equals(host)
				|| isLoopbackAddress(host);
	}

	public boolean isPrivateLanUrl(String url) {
		URI uri = parseUri(url);
		if (uri == null || uri.getHost() == null || isLocalhost(url)) {
			return false;
		}
		return isPrivateOrLanHost(uri.getHost());
	}

	public boolean isPublicHostname(String url) {
		URI uri = parseUri(url);
		return uri != null
				&& uri.getHost() != null
				&& !isLocalhost(url)
				&& !isPrivateLanUrl(url);
	}

	public boolean isPublicHttps(String url) {
		return isHttps(url) && isPublicHostname(url);
	}

	private PublicLinkInfo classify(String url, boolean configuredFromEnvironment, boolean derivedFromRequest,
			String extraWarning) {
		if (url == null || url.isBlank()) {
			return new PublicLinkInfo("", "Not properly configured", appendWarning(extraWarning, NOT_CONFIGURED_WARNING),
					false, false, false, false, configuredFromEnvironment, derivedFromRequest);
		}
		boolean https = isHttps(url);
		boolean publicHttpsReady = isPublicHttps(url);
		boolean localOnly = isLocalhost(url);
		boolean sameWifiOnly = isPrivateLanUrl(url);
		boolean publicHostname = isPublicHostname(url);
		boolean requiresHttpsWarning = !https && publicHostname;
		String statusLabel = statusLabel(publicHttpsReady, localOnly, sameWifiOnly, requiresHttpsWarning);
		String warningMessage = warningMessage(publicHttpsReady, localOnly, sameWifiOnly, requiresHttpsWarning, https);

		return new PublicLinkInfo(url, statusLabel, appendWarning(extraWarning, warningMessage),
				publicHttpsReady, localOnly, sameWifiOnly, publicHttpsReady, configuredFromEnvironment,
				derivedFromRequest);
	}

	private String statusLabel(boolean publicHttpsReady, boolean localOnly, boolean sameWifiOnly,
			boolean requiresHttpsWarning) {
		if (publicHttpsReady) {
			return "Public HTTPS ready";
		}
		if (localOnly) {
			return "Local test link";
		}
		if (sameWifiOnly) {
			return "Same Wi-Fi only";
		}
		if (requiresHttpsWarning) {
			return "HTTPS required";
		}
		return "Not properly configured";
	}

	private String warningMessage(boolean publicHttpsReady, boolean localOnly, boolean sameWifiOnly,
			boolean requiresHttpsWarning, boolean https) {
		if (publicHttpsReady) {
			return "";
		}
		if (localOnly) {
			return LOCAL_WARNING;
		}
		if (sameWifiOnly && !https) {
			return LAN_WARNING + " " + HTTPS_WARNING;
		}
		if (sameWifiOnly) {
			return LAN_WARNING;
		}
		if (requiresHttpsWarning) {
			return HTTPS_WARNING;
		}
		return NOT_CONFIGURED_WARNING;
	}

	private String appendWarning(String first, String second) {
		String cleanFirst = clean(first);
		String cleanSecond = clean(second);
		if (cleanFirst == null) {
			return cleanSecond == null ? "" : cleanSecond;
		}
		if (cleanSecond == null) {
			return cleanFirst;
		}
		return cleanFirst + " " + cleanSecond;
	}

	private String firstConfiguredBaseUrl() {
		String configuredBaseUrl = clean(environment.getProperty("app.public-base-url"));
		if (configuredBaseUrl == null) {
			configuredBaseUrl = clean(environment.getProperty("public.base-url"));
		}
		if (configuredBaseUrl == null) {
			configuredBaseUrl = clean(environment.getProperty("APP_PUBLIC_BASE_URL"));
		}
		if (configuredBaseUrl == null) {
			configuredBaseUrl = clean(environment.getProperty("PUBLIC_BASE_URL"));
		}
		return configuredBaseUrl == null ? "" : configuredBaseUrl;
	}

	private String buildUrl(String baseUrl, String path) {
		if (baseUrl == null || baseUrl.isBlank()) {
			return "";
		}
		try {
			return UriComponentsBuilder.fromUriString(baseUrl)
					.path(path)
					.build()
					.toUriString();
		} catch (IllegalArgumentException exception) {
			return "";
		}
	}

	private String requestHost(HttpServletRequest request) {
		String host = request.getServerName();
		int port = request.getServerPort();
		if (port < 0 || isDefaultPort(request.getScheme(), port)) {
			return bracketIpv6Host(host);
		}
		return bracketIpv6Host(host) + ":" + port;
	}

	private String applyForwardedPort(String host, String scheme, String forwardedPort) {
		if (host == null || forwardedPort == null || hostContainsPort(host)) {
			return host;
		}
		try {
			int port = Integer.parseInt(forwardedPort);
			if (port < 1 || isDefaultPort(scheme, port)) {
				return host;
			}
			return host + ":" + port;
		} catch (NumberFormatException exception) {
			return host;
		}
	}

	private boolean hostContainsPort(String host) {
		return host.startsWith("[") && host.contains("]:")
				|| !host.startsWith("[") && host.indexOf(':') != host.lastIndexOf(':')
				|| !host.startsWith("[") && host.lastIndexOf(':') > 0 && host.substring(host.lastIndexOf(':') + 1).matches("\\d+");
	}

	private String bracketIpv6Host(String host) {
		if (host != null && host.contains(":") && !host.startsWith("[")) {
			return "[" + host + "]";
		}
		return host;
	}

	private ForwardedValues parseForwarded(String headerValue) {
		String firstEntry = firstHeaderValue(headerValue);
		if (firstEntry == null) {
			return new ForwardedValues(null, null);
		}
		String proto = null;
		String host = null;
		for (String part : firstEntry.split(";")) {
			String cleanPart = part.trim();
			if (cleanPart.regionMatches(true, 0, "proto=", 0, 6)) {
				proto = stripQuotes(cleanPart.substring(6));
			} else if (cleanPart.regionMatches(true, 0, "host=", 0, 5)) {
				host = stripQuotes(cleanPart.substring(5));
			}
		}
		return new ForwardedValues(clean(proto), clean(host));
	}

	private String firstHeaderValue(String headerValue) {
		if (headerValue == null || headerValue.isBlank()) {
			return null;
		}
		return clean(headerValue.split(",", 2)[0]);
	}

	private String cleanContextPath(String contextPath) {
		String cleanPath = clean(contextPath);
		if (cleanPath == null || "/".equals(cleanPath)) {
			return "";
		}
		return cleanPath.startsWith("/") ? trimTrailingSlash(cleanPath) : "/" + trimTrailingSlash(cleanPath);
	}

	private String trimTrailingSlash(String value) {
		if (value == null) {
			return null;
		}
		String cleanValue = value;
		while (cleanValue.length() > 1 && cleanValue.endsWith("/")) {
			cleanValue = cleanValue.substring(0, cleanValue.length() - 1);
		}
		return cleanValue;
	}

	private String clean(String value) {
		if (value == null) {
			return null;
		}
		String cleanValue = stripQuotes(value.trim());
		return cleanValue.isBlank() ? null : cleanValue;
	}

	private String stripQuotes(String value) {
		if (value == null) {
			return null;
		}
		String cleanValue = value.trim();
		if (cleanValue.length() > 1 && cleanValue.startsWith("\"") && cleanValue.endsWith("\"")) {
			return cleanValue.substring(1, cleanValue.length() - 1);
		}
		return cleanValue;
	}

	private boolean isDefaultPort(String scheme, int port) {
		return ("http".equalsIgnoreCase(scheme) && port == 80)
				|| ("https".equalsIgnoreCase(scheme) && port == 443);
	}

	private URI parseUri(String url) {
		if (url == null || url.isBlank()) {
			return null;
		}
		try {
			return URI.create(url);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	private boolean isLoopbackAddress(String host) {
		if (!isIpLiteral(host)) {
			return false;
		}
		try {
			return InetAddress.getByName(host).isLoopbackAddress();
		} catch (Exception exception) {
			return false;
		}
	}

	private boolean isPrivateOrLanHost(String host) {
		String normalizedHost = host.toLowerCase(Locale.ROOT);
		if (normalizedHost.endsWith(".local")) {
			return true;
		}
		if (!isIpLiteral(normalizedHost)) {
			return false;
		}
		try {
			InetAddress address = InetAddress.getByName(normalizedHost);
			return address.isAnyLocalAddress()
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

	private record ForwardedValues(String proto, String host) {
	}
}
