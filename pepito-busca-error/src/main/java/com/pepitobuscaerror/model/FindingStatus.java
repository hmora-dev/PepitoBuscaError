package com.pepitobuscaerror.model;

public enum FindingStatus {
	OPEN("Open"),
	IN_PROGRESS("In progress"),
	ACCEPTED_RISK("Accepted risk"),
	RESOLVED("Resolved"),
	FALSE_POSITIVE("False positive");

	private final String label;

	FindingStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getCssClass() {
		return name().toLowerCase().replace('_', '-');
	}
}
