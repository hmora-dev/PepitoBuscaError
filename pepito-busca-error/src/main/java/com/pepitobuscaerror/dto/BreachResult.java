package com.pepitobuscaerror.dto;

import java.util.List;

public class BreachResult {

	private final String name;
	private final String breachDate;
	private final List<String> dataClasses;

	public BreachResult(String name, String breachDate, List<String> dataClasses) {
		this.name = valueOrEmpty(name);
		this.breachDate = valueOrEmpty(breachDate);
		this.dataClasses = dataClasses == null ? List.of() : List.copyOf(dataClasses);
	}

	public String getName() {
		return name;
	}

	public String getBreachDate() {
		return breachDate;
	}

	public List<String> getDataClasses() {
		return dataClasses;
	}

	public String getDataClassesSummary() {
		if (dataClasses.isEmpty()) {
			return "Not specified";
		}
		return String.join(", ", dataClasses);
	}

	private String valueOrEmpty(String value) {
		return value == null ? "" : value;
	}
}
