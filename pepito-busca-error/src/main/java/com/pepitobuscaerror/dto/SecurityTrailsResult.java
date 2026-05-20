package com.pepitobuscaerror.dto;

import java.util.List;

public class SecurityTrailsResult {

	private final String domain;
	private final boolean demoMode;
	private final String message;
	private final List<SecurityTrailsRecord> currentRecords;
	private final List<String> historicalNotes;
	private final List<String> subdomains;
	private final List<String> associatedIps;
	private final List<String> nameservers;
	private final List<String> mailServers;
	private final List<String> riskNotes;

	public SecurityTrailsResult(String domain, boolean demoMode, String message,
			List<SecurityTrailsRecord> currentRecords, List<String> historicalNotes, List<String> subdomains,
			List<String> associatedIps, List<String> nameservers, List<String> mailServers, List<String> riskNotes) {
		this.domain = valueOrEmpty(domain);
		this.demoMode = demoMode;
		this.message = valueOrEmpty(message);
		this.currentRecords = safeList(currentRecords);
		this.historicalNotes = safeList(historicalNotes);
		this.subdomains = safeList(subdomains);
		this.associatedIps = safeList(associatedIps);
		this.nameservers = safeList(nameservers);
		this.mailServers = safeList(mailServers);
		this.riskNotes = safeList(riskNotes);
	}

	public String getDomain() {
		return domain;
	}

	public boolean isDemoMode() {
		return demoMode;
	}

	public String getMessage() {
		return message;
	}

	public List<SecurityTrailsRecord> getCurrentRecords() {
		return currentRecords;
	}

	public List<String> getHistoricalNotes() {
		return historicalNotes;
	}

	public List<String> getSubdomains() {
		return subdomains;
	}

	public int getSubdomainCount() {
		return subdomains.size();
	}

	public List<String> getAssociatedIps() {
		return associatedIps;
	}

	public String getAssociatedIpsSummary() {
		return summarize(associatedIps);
	}

	public List<String> getNameservers() {
		return nameservers;
	}

	public String getNameserversSummary() {
		return summarize(nameservers);
	}

	public List<String> getMailServers() {
		return mailServers;
	}

	public String getMailServersSummary() {
		return summarize(mailServers);
	}

	public List<String> getRiskNotes() {
		return riskNotes;
	}

	private <T> List<T> safeList(List<T> values) {
		return values == null ? List.of() : List.copyOf(values);
	}

	private String valueOrEmpty(String value) {
		return value == null ? "" : value;
	}

	private String summarize(List<String> values) {
		if (values.isEmpty()) {
			return "None observed";
		}
		return String.join(", ", values);
	}
}
