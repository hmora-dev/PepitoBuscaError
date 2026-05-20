package com.pepitobuscaerror.dto;

import java.time.Instant;
import java.util.List;

public class OsintDomainResult {

	private final String domain;
	private final DnsDumpsterResult dnsDumpsterResult;
	private final SecurityTrailsResult securityTrailsResult;
	private final List<String> recommendations;
	private final Instant analyzedAt;

	public OsintDomainResult(String domain, DnsDumpsterResult dnsDumpsterResult,
			SecurityTrailsResult securityTrailsResult, List<String> recommendations, Instant analyzedAt) {
		this.domain = domain == null ? "" : domain;
		this.dnsDumpsterResult = dnsDumpsterResult;
		this.securityTrailsResult = securityTrailsResult;
		this.recommendations = recommendations == null ? List.of() : List.copyOf(recommendations);
		this.analyzedAt = analyzedAt == null ? Instant.now() : analyzedAt;
	}

	public String getDomain() {
		return domain;
	}

	public DnsDumpsterResult getDnsDumpsterResult() {
		return dnsDumpsterResult;
	}

	public SecurityTrailsResult getSecurityTrailsResult() {
		return securityTrailsResult;
	}

	public List<String> getRecommendations() {
		return recommendations;
	}

	public Instant getAnalyzedAt() {
		return analyzedAt;
	}

	public boolean isDemoMode() {
		return (dnsDumpsterResult != null && dnsDumpsterResult.isDemoMode())
				|| (securityTrailsResult != null && securityTrailsResult.isDemoMode());
	}
}
