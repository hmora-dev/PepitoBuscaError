package com.pepitobuscaerror.dto;

import java.util.List;

public class DnsDumpsterResult {

	private final String domain;
	private final boolean demoMode;
	private final String message;
	private final List<SubdomainResult> subdomains;
	private final List<String> dnsRecordsSummary;
	private final List<String> mxRecords;
	private final List<String> nsRecords;
	private final List<String> txtRecords;
	private final List<String> exposedServices;
	private final List<String> riskNotes;

	public DnsDumpsterResult(String domain, boolean demoMode, String message, List<SubdomainResult> subdomains,
			List<String> dnsRecordsSummary, List<String> mxRecords, List<String> nsRecords, List<String> txtRecords,
			List<String> exposedServices, List<String> riskNotes) {
		this.domain = valueOrEmpty(domain);
		this.demoMode = demoMode;
		this.message = valueOrEmpty(message);
		this.subdomains = safeList(subdomains);
		this.dnsRecordsSummary = safeList(dnsRecordsSummary);
		this.mxRecords = safeList(mxRecords);
		this.nsRecords = safeList(nsRecords);
		this.txtRecords = safeList(txtRecords);
		this.exposedServices = safeList(exposedServices);
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

	public List<SubdomainResult> getSubdomains() {
		return subdomains;
	}

	public int getSubdomainCount() {
		return subdomains.size();
	}

	public List<String> getDnsRecordsSummary() {
		return dnsRecordsSummary;
	}

	public List<String> getMxRecords() {
		return mxRecords;
	}

	public String getMxRecordsSummary() {
		return summarize(mxRecords);
	}

	public List<String> getNsRecords() {
		return nsRecords;
	}

	public String getNsRecordsSummary() {
		return summarize(nsRecords);
	}

	public List<String> getTxtRecords() {
		return txtRecords;
	}

	public String getTxtRecordsSummary() {
		return summarize(txtRecords);
	}

	public List<String> getExposedServices() {
		return exposedServices;
	}

	public String getExposedServicesSummary() {
		return summarize(exposedServices);
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
		return String.join("; ", values);
	}
}
