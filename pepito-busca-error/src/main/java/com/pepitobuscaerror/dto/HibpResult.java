package com.pepitobuscaerror.dto;

import java.time.Instant;
import java.util.List;

public class HibpResult {

	private final String email;
	private final boolean demoMode;
	private final String message;
	private final boolean breachesFound;
	private final int breachCount;
	private final List<BreachResult> breaches;
	private final String riskSeverity;
	private final String riskRecommendation;
	private final Instant checkedAt;

	public HibpResult(String email, boolean demoMode, String message, boolean breachesFound, int breachCount,
			List<BreachResult> breaches, String riskSeverity, String riskRecommendation, Instant checkedAt) {
		this.email = valueOrEmpty(email);
		this.demoMode = demoMode;
		this.message = valueOrEmpty(message);
		this.breachesFound = breachesFound;
		this.breachCount = breachCount;
		this.breaches = breaches == null ? List.of() : List.copyOf(breaches);
		this.riskSeverity = valueOrEmpty(riskSeverity);
		this.riskRecommendation = valueOrEmpty(riskRecommendation);
		this.checkedAt = checkedAt == null ? Instant.now() : checkedAt;
	}

	public String getEmail() {
		return email;
	}

	public boolean isDemoMode() {
		return demoMode;
	}

	public String getMessage() {
		return message;
	}

	public boolean isBreachesFound() {
		return breachesFound;
	}

	public int getBreachCount() {
		return breachCount;
	}

	public List<BreachResult> getBreaches() {
		return breaches;
	}

	public String getRiskSeverity() {
		return riskSeverity;
	}

	public String getRiskRecommendation() {
		return riskRecommendation;
	}

	public Instant getCheckedAt() {
		return checkedAt;
	}

	private String valueOrEmpty(String value) {
		return value == null ? "" : value;
	}
}
