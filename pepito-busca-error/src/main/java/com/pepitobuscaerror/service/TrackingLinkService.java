package com.pepitobuscaerror.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;

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
		String networkUrl = findPrivateIpv4Address()
				.map(ipAddress -> buildUrl(request.getScheme(), ipAddress, request.getServerPort(),
						request.getContextPath(), path))
				.orElse("");
		String configuredUrl = buildConfiguredUrl(path);

		return new TrackingLinks(currentUrl, networkUrl, configuredUrl);
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

	private String buildUrl(String scheme, String host, int port, String contextPath, String path) {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
				.scheme(scheme)
				.host(host);
		if (!isDefaultPort(scheme, port)) {
			builder.port(port);
		}
		if (contextPath != null && !contextPath.isBlank()) {
			builder.path(contextPath);
		}
		return builder.path(path).build().toUriString();
	}

	private boolean isDefaultPort(String scheme, int port) {
		return ("http".equalsIgnoreCase(scheme) && port == 80)
				|| ("https".equalsIgnoreCase(scheme) && port == 443);
	}

	private Optional<String> findPrivateIpv4Address() {
		Optional<String> preferredAddress = findIpv4Address(true);
		return preferredAddress.isPresent() ? preferredAddress : findIpv4Address(false);
	}

	private Optional<String> findIpv4Address(boolean skipLikelyVirtualAdapters) {
		InetAddress fallback = null;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
					continue;
				}
				if (skipLikelyVirtualAdapters && isLikelyVirtualAdapter(networkInterface)) {
					continue;
				}
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (!(address instanceof Inet4Address) || address.isLoopbackAddress() || address.isLinkLocalAddress()) {
						continue;
					}
					if (address.isSiteLocalAddress()) {
						return Optional.of(address.getHostAddress());
					}
					if (fallback == null) {
						fallback = address;
					}
				}
			}
		} catch (SocketException exception) {
			return Optional.empty();
		}
		return fallback == null ? Optional.empty() : Optional.of(fallback.getHostAddress());
	}

	private boolean isLikelyVirtualAdapter(NetworkInterface networkInterface) {
		String name = networkInterface.getName() == null ? "" : networkInterface.getName();
		String displayName = networkInterface.getDisplayName() == null ? "" : networkInterface.getDisplayName();
		String adapterName = (name + " " + displayName).toLowerCase();
		return adapterName.contains("docker")
				|| adapterName.contains("veth")
				|| adapterName.contains("virtual")
				|| adapterName.contains("vmware")
				|| adapterName.contains("vbox")
				|| adapterName.contains("hyper-v")
				|| adapterName.contains("wsl");
	}

	public record TrackingLinks(String currentUrl, String networkUrl, String configuredUrl) {
	}
}
