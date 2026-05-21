package com.pepitobuscaerror.dto;

import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.model.Company;
import com.pepitobuscaerror.model.RiskLevel;

import java.util.List;
import java.util.Map;

public class DashboardStats {

	private final long totalCompanies;
	private final long totalAnalyses;
	private final int averageRiskScore;
	private final long criticalAnalyses;
	private final long criticalIndicators;
	private final List<Company> latestCompanies;
	private final List<Analysis> latestAnalyses;
	private final Analysis latestHighRiskAnalysis;
	private final List<Analysis> latestHighRiskAnalyses;
	private final Map<RiskLevel, Long> riskDistribution;

	public DashboardStats(long totalCompanies, long totalAnalyses, int averageRiskScore, long criticalAnalyses,
			long criticalIndicators, List<Company> latestCompanies, List<Analysis> latestAnalyses,
			Analysis latestHighRiskAnalysis, List<Analysis> latestHighRiskAnalyses,
			Map<RiskLevel, Long> riskDistribution) {
		this.totalCompanies = totalCompanies;
		this.totalAnalyses = totalAnalyses;
		this.averageRiskScore = averageRiskScore;
		this.criticalAnalyses = criticalAnalyses;
		this.criticalIndicators = criticalIndicators;
		this.latestCompanies = latestCompanies;
		this.latestAnalyses = latestAnalyses;
		this.latestHighRiskAnalysis = latestHighRiskAnalysis;
		this.latestHighRiskAnalyses = latestHighRiskAnalyses;
		this.riskDistribution = riskDistribution;
	}

	public long getTotalCompanies() {
		return totalCompanies;
	}

	public long getTotalAnalyses() {
		return totalAnalyses;
	}

	public int getAverageRiskScore() {
		return averageRiskScore;
	}

	public long getCriticalAnalyses() {
		return criticalAnalyses;
	}

	public long getCriticalIndicators() {
		return criticalIndicators;
	}

	public List<Company> getLatestCompanies() {
		return latestCompanies;
	}

	public List<Analysis> getLatestAnalyses() {
		return latestAnalyses;
	}

	public Analysis getLatestHighRiskAnalysis() {
		return latestHighRiskAnalysis;
	}

	public List<Analysis> getLatestHighRiskAnalyses() {
		return latestHighRiskAnalyses;
	}

	public Map<RiskLevel, Long> getRiskDistribution() {
		return riskDistribution;
	}
}
