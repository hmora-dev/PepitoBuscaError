package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.DashboardStats;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import com.pepitobuscaerror.model.Recommendation;
import com.pepitobuscaerror.model.RiskLevel;
import com.pepitobuscaerror.model.Severity;
import com.pepitobuscaerror.repository.AnalysisRepository;
import com.pepitobuscaerror.repository.CompanyRepository;
import com.pepitobuscaerror.repository.RecommendationRepository;
import com.pepitobuscaerror.repository.ScanRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

	private final CompanyRepository companyRepository;
	private final AnalysisRepository analysisRepository;
	private final RecommendationRepository recommendationRepository;
	private final ScanRunRepository scanRunRepository;

	public DashboardService(CompanyRepository companyRepository, AnalysisRepository analysisRepository,
			RecommendationRepository recommendationRepository, ScanRunRepository scanRunRepository) {
		this.companyRepository = companyRepository;
		this.analysisRepository = analysisRepository;
		this.recommendationRepository = recommendationRepository;
		this.scanRunRepository = scanRunRepository;
	}

	@Transactional(readOnly = true)
	public DashboardStats getDashboardStats() {
		Map<RiskLevel, Long> distribution = new EnumMap<>(RiskLevel.class);
		for (RiskLevel riskLevel : RiskLevel.values()) {
			distribution.put(riskLevel, analysisRepository.countByRiskLevel(riskLevel));
		}

		Map<Severity, Long> indicatorDistribution = new EnumMap<>(Severity.class);
		for (Severity severity : Severity.values()) {
			indicatorDistribution.put(severity, analysisRepository.countIndicatorsBySeverity(severity));
		}

		Map<FindingCategory, Long> findingCategoryDistribution = new EnumMap<>(FindingCategory.class);
		for (FindingCategory category : FindingCategory.values()) {
			findingCategoryDistribution.put(category, 0L);
		}
		scanRunRepository.countFindingsByCategory()
				.forEach(row -> findingCategoryDistribution.put((FindingCategory) row[0], (Long) row[1]));

		long criticalFindings = analysisRepository.countIndicatorsBySeverity(Severity.CRITICAL)
				+ scanRunRepository.countFindingsBySeverity(FindingSeverity.CRITICAL);
		long highFindings = analysisRepository.countIndicatorsBySeverity(Severity.HIGH)
				+ scanRunRepository.countFindingsBySeverity(FindingSeverity.HIGH);

		return new DashboardStats(
				companyRepository.count(),
				analysisRepository.count(),
				(int) Math.round(analysisRepository.averageRiskScore()),
				distribution.get(RiskLevel.CRITICAL),
				analysisRepository.countIndicatorsBySeverity(Severity.CRITICAL),
				criticalFindings,
				highFindings,
				recommendationRepository.count(),
				scanRunRepository.count(),
				companyRepository.findTop5ByOrderByRegistrationDateDesc(),
				analysisRepository.findTop8ByOrderByAnalysisDateDesc(),
				scanRunRepository.findTop12ByOrderByStartedAtDesc(),
				recommendationRepository.findAllByOrderByPriorityDescDescriptionAsc().stream()
						.sorted(Comparator.comparing((Recommendation recommendation) ->
								recommendation.getPriority().ordinal()).reversed()
								.thenComparing(Recommendation::getDescription))
						.limit(5)
						.toList(),
				analysisRepository.findFirstByRiskLevelInOrderByAnalysisDateDesc(
						List.of(RiskLevel.CRITICAL, RiskLevel.HIGH)).orElse(null),
				analysisRepository.findTop5ByRiskLevelInOrderByAnalysisDateDesc(
						List.of(RiskLevel.CRITICAL, RiskLevel.HIGH)),
				distribution,
				indicatorDistribution,
				findingCategoryDistribution
		);
	}
}
