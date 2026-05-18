package com.pepitobuscaerror.model;

public enum FindingSeverity {
	CRITICAL("Critical", 25),
	HIGH("High", 15),
	MEDIUM("Medium", 8),
	LOW("Low", 3),
	INFO("Info", 0);

	private final String label;
	private final int scoreWeight;

	FindingSeverity(String label, int scoreWeight) {
		this.label = label;
		this.scoreWeight = scoreWeight;
	}

	public String getLabel() {
		return label;
	}

	public int getScoreWeight() {
		return scoreWeight;
	}
}
