package com.pepitobuscaerror.dto;

import com.pepitobuscaerror.model.Severity;

public class IndicatorOption {

	private final String type;
	private final String value;
	private final String description;
	private final Severity severity;

	public IndicatorOption(String type, String value, String description, Severity severity) {
		this.type = type;
		this.value = value;
		this.description = description;
		this.severity = severity;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public Severity getSeverity() {
		return severity;
	}
}
