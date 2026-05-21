package com.pepitobuscaerror.dto;

import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.model.Company;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.Recommendation;
import com.pepitobuscaerror.model.RiskLevel;
import com.pepitobuscaerror.model.ScanRun;
import com.pepitobuscaerror.model.Severity;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DashboardStats {

	private static final DateTimeFormatter TREND_LABEL_FORMAT = DateTimeFormatter.ofPattern("dd MMM");

	private final long totalCompanies;
	private final long totalAnalyses;
	private final int averageRiskScore;
	private final long criticalAnalyses;
	private final long criticalIndicators;
	private final long criticalFindings;
	private final long highFindings;
	private final long totalRecommendations;
	private final long totalOsintChecks;
	private final List<Company> latestCompanies;
	private final List<Analysis> latestAnalyses;
	private final List<ScanRun> latestOsintScans;
	private final List<Recommendation> topRecommendations;
	private final Analysis latestHighRiskAnalysis;
	private final List<Analysis> latestHighRiskAnalyses;
	private final Map<RiskLevel, Long> riskDistribution;
	private final Map<Severity, Long> indicatorSeverityDistribution;
	private final Map<FindingCategory, Long> findingCategoryDistribution;

	public DashboardStats(long totalCompanies, long totalAnalyses, int averageRiskScore, long criticalAnalyses,
			long criticalIndicators, long criticalFindings, long highFindings, long totalRecommendations,
			long totalOsintChecks,
			List<Company> latestCompanies, List<Analysis> latestAnalyses, List<ScanRun> latestOsintScans,
			List<Recommendation> topRecommendations, Analysis latestHighRiskAnalysis,
			List<Analysis> latestHighRiskAnalyses, Map<RiskLevel, Long> riskDistribution,
			Map<Severity, Long> indicatorSeverityDistribution,
			Map<FindingCategory, Long> findingCategoryDistribution) {
		this.totalCompanies = totalCompanies;
		this.totalAnalyses = totalAnalyses;
		this.averageRiskScore = averageRiskScore;
		this.criticalAnalyses = criticalAnalyses;
		this.criticalIndicators = criticalIndicators;
		this.criticalFindings = criticalFindings;
		this.highFindings = highFindings;
		this.totalRecommendations = totalRecommendations;
		this.totalOsintChecks = totalOsintChecks;
		this.latestCompanies = latestCompanies;
		this.latestAnalyses = latestAnalyses;
		this.latestOsintScans = latestOsintScans;
		this.topRecommendations = topRecommendations;
		this.latestHighRiskAnalysis = latestHighRiskAnalysis;
		this.latestHighRiskAnalyses = latestHighRiskAnalyses;
		this.riskDistribution = riskDistribution;
		this.indicatorSeverityDistribution = indicatorSeverityDistribution;
		this.findingCategoryDistribution = findingCategoryDistribution;
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

	public long getCriticalFindings() {
		return criticalFindings;
	}

	public long getHighFindings() {
		return highFindings;
	}

	public long getTotalRecommendations() {
		return totalRecommendations;
	}

	public long getOpenRecommendations() {
		return totalRecommendations;
	}

	public long getTotalOsintChecks() {
		return totalOsintChecks;
	}

	public List<Company> getLatestCompanies() {
		return latestCompanies;
	}

	public List<Analysis> getLatestAnalyses() {
		return latestAnalyses;
	}

	public List<ScanRun> getLatestOsintScans() {
		return latestOsintScans;
	}

	public List<Recommendation> getTopRecommendations() {
		return topRecommendations;
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

	public Map<Severity, Long> getIndicatorSeverityDistribution() {
		return indicatorSeverityDistribution;
	}

	public Map<FindingCategory, Long> getFindingCategoryDistribution() {
		return findingCategoryDistribution;
	}

	public long getTotalIndicators() {
		return indicatorSeverityDistribution.values().stream()
				.mapToLong(Long::longValue)
				.sum();
	}

	public long getTotalOsintFindings() {
		return findingCategoryDistribution.values().stream()
				.mapToLong(Long::longValue)
				.sum();
	}

	public int getRiskDistributionPercentage(RiskLevel riskLevel) {
		return percentage(riskDistribution.getOrDefault(riskLevel, 0L), totalAnalyses);
	}

	public int riskDistributionPercentage(RiskLevel riskLevel) {
		return getRiskDistributionPercentage(riskLevel);
	}

	public int getIndicatorSeverityPercentage(Severity severity) {
		return percentage(indicatorSeverityDistribution.getOrDefault(severity, 0L), getTotalIndicators());
	}

	public int indicatorSeverityPercentage(Severity severity) {
		return getIndicatorSeverityPercentage(severity);
	}

	public int getFindingCategoryPercentage(FindingCategory category) {
		return percentage(findingCategoryDistribution.getOrDefault(category, 0L), getTotalOsintFindings());
	}

	public int findingCategoryPercentage(FindingCategory category) {
		return getFindingCategoryPercentage(category);
	}

	public boolean isHasRiskDistributionData() {
		return totalAnalyses > 0 && riskDistribution.values().stream().anyMatch(value -> value > 0);
	}

	public boolean hasRiskDistributionData() {
		return isHasRiskDistributionData();
	}

	public boolean isHasIndicatorSeverityData() {
		return getTotalIndicators() > 0;
	}

	public boolean hasIndicatorSeverityData() {
		return isHasIndicatorSeverityData();
	}

	public boolean isHasFindingCategoryData() {
		return getTotalOsintFindings() > 0;
	}

	public boolean hasFindingCategoryData() {
		return isHasFindingCategoryData();
	}

	public boolean isHasRecentRiskTrendData() {
		return latestAnalyses.size() > 1;
	}

	public boolean hasRecentRiskTrendData() {
		return isHasRecentRiskTrendData();
	}

	public List<String> getRiskDistributionLabels() {
		return List.of(RiskLevel.LOW.getLabel(), RiskLevel.MEDIUM.getLabel(), RiskLevel.HIGH.getLabel(),
				RiskLevel.CRITICAL.getLabel());
	}

	public List<Long> getRiskDistributionData() {
		return List.of(
				riskDistribution.getOrDefault(RiskLevel.LOW, 0L),
				riskDistribution.getOrDefault(RiskLevel.MEDIUM, 0L),
				riskDistribution.getOrDefault(RiskLevel.HIGH, 0L),
				riskDistribution.getOrDefault(RiskLevel.CRITICAL, 0L)
		);
	}

	public List<String> getRiskDistributionColors() {
		return List.of("#16a34a", "#f59e0b", "#dc2626", "#111827");
	}

	public List<String> getIndicatorSeverityLabels() {
		return List.of(Severity.LOW.getLabel(), Severity.MEDIUM.getLabel(), Severity.HIGH.getLabel(),
				Severity.CRITICAL.getLabel());
	}

	public List<Long> getIndicatorSeverityData() {
		return List.of(
				indicatorSeverityDistribution.getOrDefault(Severity.LOW, 0L),
				indicatorSeverityDistribution.getOrDefault(Severity.MEDIUM, 0L),
				indicatorSeverityDistribution.getOrDefault(Severity.HIGH, 0L),
				indicatorSeverityDistribution.getOrDefault(Severity.CRITICAL, 0L)
		);
	}

	public List<String> getIndicatorSeverityColors() {
		return List.of("#16a34a", "#f59e0b", "#dc2626", "#111827");
	}

	public List<String> getFindingCategoryLabels() {
		return List.of(FindingCategory.OSINT.getLabel(), FindingCategory.DNS.getLabel(), FindingCategory.MAIL.getLabel(),
				FindingCategory.WEB.getLabel(), FindingCategory.DATA.getLabel(), FindingCategory.AVAILABILITY.getLabel());
	}

	public List<Long> getFindingCategoryData() {
		return List.of(
				findingCategoryDistribution.getOrDefault(FindingCategory.OSINT, 0L),
				findingCategoryDistribution.getOrDefault(FindingCategory.DNS, 0L),
				findingCategoryDistribution.getOrDefault(FindingCategory.MAIL, 0L),
				findingCategoryDistribution.getOrDefault(FindingCategory.WEB, 0L),
				findingCategoryDistribution.getOrDefault(FindingCategory.DATA, 0L),
				findingCategoryDistribution.getOrDefault(FindingCategory.AVAILABILITY, 0L)
		);
	}

	public List<String> getFindingCategoryColors() {
		return List.of("#2563eb", "#0891b2", "#7c3aed", "#16a34a", "#f59e0b", "#64748b");
	}

	public List<String> getRecentRiskTrendLabels() {
		return latestAnalyses.stream()
				.sorted(Comparator.comparing(Analysis::getAnalysisDate))
				.map(analysis -> analysis.getAnalysisDate().format(TREND_LABEL_FORMAT))
				.toList();
	}

	public List<Integer> getRecentRiskTrendData() {
		return latestAnalyses.stream()
				.sorted(Comparator.comparing(Analysis::getAnalysisDate))
				.map(Analysis::getRiskScore)
				.toList();
	}

	private int percentage(long value, long total) {
		if (total == 0) {
			return 0;
		}
		return (int) Math.round((value * 100.0) / total);
	}
}
