package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.DashboardStats;
import com.pepitobuscaerror.model.RiskLevel;
import com.pepitobuscaerror.repository.AnalysisRepository;
import com.pepitobuscaerror.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
public class DashboardService {

	private final CompanyRepository companyRepository;
	private final AnalysisRepository analysisRepository;

	public DashboardService(CompanyRepository companyRepository, AnalysisRepository analysisRepository) {
		this.companyRepository = companyRepository;
		this.analysisRepository = analysisRepository;
	}

	@Transactional(readOnly = true)
	public DashboardStats getDashboardStats() {
		Map<RiskLevel, Long> distribution = new EnumMap<>(RiskLevel.class);
		for (RiskLevel riskLevel : RiskLevel.values()) {
			distribution.put(riskLevel, analysisRepository.countByRiskLevel(riskLevel));
		}

		return new DashboardStats(
				companyRepository.count(),
				analysisRepository.count(),
				(int) Math.round(analysisRepository.averageRiskScore()),
				distribution.get(RiskLevel.CRITICAL),
				companyRepository.findTop5ByOrderByRegistrationDateDesc(),
				analysisRepository.findTop8ByOrderByAnalysisDateDesc(),
				distribution
		);
	}
}
