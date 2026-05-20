package com.pepitobuscaerror.model;

public enum FindingCategory {
	OSINT("OSINT"),
	WEB("Web"),
	MAIL("Mail"),
	DNS("DNS"),
	DATA("Data"),
	AVAILABILITY("Availability");

	private final String label;

	FindingCategory(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
