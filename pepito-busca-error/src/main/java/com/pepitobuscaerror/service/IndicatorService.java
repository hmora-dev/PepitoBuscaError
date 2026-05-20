package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.IndicatorOption;
import com.pepitobuscaerror.model.Indicator;
import com.pepitobuscaerror.model.Severity;
import com.pepitobuscaerror.repository.IndicatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class IndicatorService {

	private final IndicatorRepository indicatorRepository;
	private final AnalysisService analysisService;

	public IndicatorService(IndicatorRepository indicatorRepository, AnalysisService analysisService) {
		this.indicatorRepository = indicatorRepository;
		this.analysisService = analysisService;
	}

	@Transactional(readOnly = true)
	public List<Indicator> findAllIndicators() {
		return indicatorRepository.findAllByOrderBySeverityDescTypeAsc();
	}

	@Transactional(readOnly = true)
	public List<IndicatorOption> findIndicatorCatalog() {
		return analysisService.availableIndicatorOptions();
	}

	@Transactional(readOnly = true)
	public Map<Severity, Long> countBySeverity() {
		Map<Severity, Long> counts = new EnumMap<>(Severity.class);
		for (Severity severity : Severity.values()) {
			counts.put(severity, indicatorRepository.countBySeverity(severity));
		}
		return counts;
	}
}
