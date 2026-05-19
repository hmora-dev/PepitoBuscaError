package com.pepitobuscaerror.model;

public enum RiskLevel {
	LOW("Low"),
	MEDIUM("Medium"),
	HIGH("High"),
	CRITICAL("Critical");

	private final String label;

	RiskLevel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getCssClass() {
		return name().toLowerCase();
	}
}
