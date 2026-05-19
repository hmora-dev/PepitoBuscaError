package com.pepitobuscaerror.model;

public enum Severity {
	LOW("Low"),
	MEDIUM("Medium"),
	HIGH("High"),
	CRITICAL("Critical");

	private final String label;

	Severity(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getCssClass() {
		return name().toLowerCase();
	}
}
