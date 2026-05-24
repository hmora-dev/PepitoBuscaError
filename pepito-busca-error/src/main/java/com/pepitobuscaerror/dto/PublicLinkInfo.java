package com.pepitobuscaerror.dto;

public class PublicLinkInfo {

	private final String url;
	private final String statusLabel;
	private final String warningMessage;
	private final boolean publicHttpsReady;
	private final boolean localOnly;
	private final boolean sameWifiOnly;
	private final boolean usableFromAnotherNetwork;
	private final boolean configuredFromEnvironment;
	private final boolean derivedFromRequest;

	public PublicLinkInfo(String url, String statusLabel, String warningMessage,
			boolean publicHttpsReady, boolean localOnly, boolean sameWifiOnly, boolean usableFromAnotherNetwork,
			boolean configuredFromEnvironment, boolean derivedFromRequest) {
		this.url = url;
		this.statusLabel = statusLabel;
		this.warningMessage = warningMessage;
		this.publicHttpsReady = publicHttpsReady;
		this.localOnly = localOnly;
		this.sameWifiOnly = sameWifiOnly;
		this.usableFromAnotherNetwork = usableFromAnotherNetwork;
		this.configuredFromEnvironment = configuredFromEnvironment;
		this.derivedFromRequest = derivedFromRequest;
	}

	public String getUrl() {
		return url;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public boolean isPublicHttpsReady() {
		return publicHttpsReady;
	}

	public boolean isPublicHttps() {
		return publicHttpsReady;
	}

	public boolean isLocalOnly() {
		return localOnly;
	}

	public boolean isSameWifiOnly() {
		return sameWifiOnly;
	}

	public boolean isLanOnly() {
		return sameWifiOnly;
	}

	public boolean isUsableFromAnotherNetwork() {
		return usableFromAnotherNetwork;
	}

	public boolean isUsableFromDifferentNetwork() {
		return usableFromAnotherNetwork;
	}

	public boolean isConfiguredFromEnvironment() {
		return configuredFromEnvironment;
	}

	public boolean isDerivedFromRequest() {
		return derivedFromRequest;
	}

	public boolean isRequiresHttpsWarning() {
		return url != null && url.toLowerCase(java.util.Locale.ROOT).startsWith("http://") && !localOnly;
	}
}
