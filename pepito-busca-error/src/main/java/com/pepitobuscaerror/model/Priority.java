package com.pepitobuscaerror.model;

public enum Priority {
	LOW("Low"),
	MEDIUM("Medium"),
	HIGH("High");

	private final String label;

	Priority(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getCssClass() {
		return name().toLowerCase();
	}
}
