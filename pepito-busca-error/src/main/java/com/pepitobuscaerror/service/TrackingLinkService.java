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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
		List<String> networkUrls = findPrivateIpv4Addresses().stream()
				.map(ipAddress -> buildUrl(request.getScheme(), ipAddress, request.getServerPort(),
						request.getContextPath(), path))
				.toList();
		String networkUrl = networkUrls.isEmpty() ? "" : networkUrls.get(0);
		String configuredUrl = buildConfiguredUrl(path);

		return new TrackingLinks(currentUrl, networkUrl, networkUrls, configuredUrl);
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

	private List<String> findPrivateIpv4Addresses() {
		List<NetworkAddressCandidate> candidates = findIpv4AddressCandidates(true);
		if (candidates.isEmpty()) {
			candidates = findIpv4AddressCandidates(false);
		}
		Set<String> addresses = new LinkedHashSet<>();
		candidates.stream()
				.sorted(Comparator.comparingInt(NetworkAddressCandidate::score)
						.thenComparing(NetworkAddressCandidate::hostAddress))
				.map(NetworkAddressCandidate::hostAddress)
				.forEach(addresses::add);
		return List.copyOf(addresses);
	}

	private List<NetworkAddressCandidate> findIpv4AddressCandidates(boolean skipLikelyVirtualAdapters) {
		List<NetworkAddressCandidate> candidates = new ArrayList<>();
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
					if (!address.isSiteLocalAddress()) {
						continue;
					}
					String hostAddress = address.getHostAddress();
					if (isLikelyHostOnlyAddress(hostAddress)) {
						continue;
					}
					candidates.add(new NetworkAddressCandidate(hostAddress, adapterScore(networkInterface)));
				}
			}
		} catch (SocketException exception) {
			return List.of();
		}
		return candidates;
	}

	private boolean isLikelyVirtualAdapter(NetworkInterface networkInterface) {
		String name = networkInterface.getName() == null ? "" : networkInterface.getName();
		String displayName = networkInterface.getDisplayName() == null ? "" : networkInterface.getDisplayName();
		String adapterName = (name + " " + displayName).toLowerCase();
		return adapterName.contains("docker")
				|| adapterName.contains("container")
				|| adapterName.contains("veth")
				|| adapterName.contains("vethernet")
				|| adapterName.contains("virtual")
				|| adapterName.contains("virtualbox")
				|| adapterName.contains("vmware")
				|| adapterName.contains("vbox")
				|| adapterName.contains("hyper-v")
				|| adapterName.contains("host-only")
				|| adapterName.contains("wsl")
				|| adapterName.contains("loopback")
				|| adapterName.contains("bluetooth");
	}

	private int adapterScore(NetworkInterface networkInterface) {
		String name = networkInterface.getName() == null ? "" : networkInterface.getName();
		String displayName = networkInterface.getDisplayName() == null ? "" : networkInterface.getDisplayName();
		String adapterName = (name + " " + displayName).toLowerCase();
		if (adapterName.contains("wi-fi") || adapterName.contains("wifi") || adapterName.contains("wireless")
				|| adapterName.contains("wlan")) {
			return 0;
		}
		if (adapterName.contains("ethernet") || adapterName.startsWith("eth")) {
			return 10;
		}
		return 20;
	}

	private boolean isLikelyHostOnlyAddress(String hostAddress) {
		return hostAddress.startsWith("192.168.56.");
	}

	private record NetworkAddressCandidate(String hostAddress, int score) {
	}

	public record TrackingLinks(String currentUrl, String networkUrl, List<String> networkUrls, String configuredUrl) {
	}
}
