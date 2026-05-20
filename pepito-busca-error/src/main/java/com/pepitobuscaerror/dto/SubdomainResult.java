package com.pepitobuscaerror.dto;

public class SubdomainResult {

	private final String hostname;
	private final String source;
	private final String ipAddress;
	private final String note;
	private final boolean interesting;

	public SubdomainResult(String hostname, String source, String ipAddress, String note, boolean interesting) {
		this.hostname = valueOrEmpty(hostname);
		this.source = valueOrEmpty(source);
		this.ipAddress = valueOrEmpty(ipAddress);
		this.note = valueOrEmpty(note);
		this.interesting = interesting;
	}

	public String getHostname() {
		return hostname;
	}

	public String getSource() {
		return source;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getNote() {
		return note;
	}

	public boolean isInteresting() {
		return interesting;
	}

	public String getRiskLabel() {
		return interesting ? "Review" : "Info";
	}

	public String getRiskClass() {
		return interesting ? "medium" : "info";
	}

	private String valueOrEmpty(String value) {
		return value == null ? "" : value;
	}
}
