package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.AnalysisForm;
import com.pepitobuscaerror.dto.IndicatorOption;
import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.model.Company;
import com.pepitobuscaerror.model.Indicator;
import com.pepitobuscaerror.model.Priority;
import com.pepitobuscaerror.model.Recommendation;
import com.pepitobuscaerror.model.Severity;
import com.pepitobuscaerror.repository.AnalysisRepository;
import com.pepitobuscaerror.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

	private final CompanyRepository companyRepository;
	private final AnalysisRepository analysisRepository;
	private final BasicRiskCalculator riskCalculator;

	public AnalysisService(CompanyRepository companyRepository, AnalysisRepository analysisRepository,
			BasicRiskCalculator riskCalculator) {
		this.companyRepository = companyRepository;
		this.analysisRepository = analysisRepository;
		this.riskCalculator = riskCalculator;
	}

	@Transactional(readOnly = true)
	public List<IndicatorOption> availableIndicatorOptions() {
		return List.of(
				new IndicatorOption(BasicRiskCalculator.SUSPICIOUS_IP_ADDRESS, "Suspicious hosting signal",
						"The company infrastructure uses an IP address that should be reviewed.", Severity.CRITICAL),
				new IndicatorOption(BasicRiskCalculator.SUSPICIOUS_CORPORATE_EMAIL, "Corporate email risk",
						"The corporate email shows signs of exposure, spoofing risk, or weak configuration.", Severity.HIGH),
				new IndicatorOption(BasicRiskCalculator.DOMAIN_WITHOUT_HTTPS, "HTTP service detected",
						"The company domain is not forcing encrypted HTTPS access.", Severity.MEDIUM),
				new IndicatorOption(BasicRiskCalculator.MISSING_SECURITY_HEADERS, "Header hardening gap",
						"Important browser security headers are missing from the public web response.", Severity.MEDIUM),
				new IndicatorOption(BasicRiskCalculator.INVALID_SSL_CERTIFICATE, "Certificate problem",
						"The SSL certificate is expired, invalid, or not trusted by browsers.", Severity.HIGH)
		);
	}

	@Transactional(readOnly = true)
	public List<Analysis> findAllAnalyses() {
		return analysisRepository.findAllByOrderByAnalysisDateDesc();
	}

	@Transactional
	public Analysis createAnalysis(Long companyId, AnalysisForm form) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new ResourceNotFoundException("The requested company does not exist."));
		Analysis analysis = new Analysis(company);

		List<Indicator> indicators = buildIndicators(form.getSelectedIndicators());
		indicators.forEach(analysis::addIndicator);

		int riskScore = riskCalculator.calculateRisk(indicators);
		analysis.complete(riskScore, riskCalculator.determineRiskLevel(riskScore));
		generateRecommendations(indicators).forEach(analysis::addRecommendation);

		return analysisRepository.save(analysis);
	}

	@Transactional(readOnly = true)
	public Analysis getAnalysis(Long id) {
		Analysis analysis = analysisRepository.findByIdAnalysis(id)
				.orElseThrow(() -> new ResourceNotFoundException("The requested analysis does not exist."));
		analysis.getIndicators().size();
		analysis.getRecommendations().size();
		return analysis;
	}

	public Map<Severity, List<Indicator>> groupIndicatorsBySeverity(Analysis analysis) {
		Map<Severity, List<Indicator>> groups = new EnumMap<>(Severity.class);
		for (Severity severity : Severity.values()) {
			groups.put(severity, new ArrayList<>());
		}
		analysis.getIndicators().stream()
				.sorted(Comparator.comparing(Indicator::getSeverity).reversed().thenComparing(Indicator::getType))
				.forEach(indicator -> groups.get(indicator.getSeverity()).add(indicator));
		return groups;
	}

	public Map<Priority, List<Recommendation>> groupRecommendationsByPriority(Analysis analysis) {
		Map<Priority, List<Recommendation>> groups = new EnumMap<>(Priority.class);
		for (Priority priority : Priority.values()) {
			groups.put(priority, new ArrayList<>());
		}
		analysis.getRecommendations().stream()
				.sorted(Comparator.comparing(Recommendation::getPriority).reversed()
						.thenComparing(Recommendation::getDescription))
				.forEach(recommendation -> groups.get(recommendation.getPriority()).add(recommendation));
		return groups;
	}

	private List<Indicator> buildIndicators(List<String> selectedTypes) {
		Map<String, IndicatorOption> options = availableIndicatorOptions().stream()
				.collect(Collectors.toMap(IndicatorOption::getType, Function.identity()));
		return selectedTypes.stream()
				.distinct()
				.map(type -> {
					IndicatorOption option = options.get(type);
					if (option == null) {
						throw new IllegalArgumentException("One of the selected indicators is not valid.");
					}
					return new Indicator(option.getType(), option.getValue(), option.getDescription(), option.getSeverity());
				})
				.toList();
	}

	private List<Recommendation> generateRecommendations(List<Indicator> indicators) {
		Map<String, Recommendation> recommendations = new LinkedHashMap<>();
		for (Indicator indicator : indicators) {
			switch (indicator.getType()) {
				case BasicRiskCalculator.SUSPICIOUS_IP_ADDRESS -> addRecommendation(recommendations, Priority.HIGH,
						"Review IP reputation.",
						"Check the affected IP address, hosting provider, DNS records, and firewall exposure.");
				case BasicRiskCalculator.SUSPICIOUS_CORPORATE_EMAIL -> addRecommendation(recommendations, Priority.HIGH,
						"Change exposed credentials.",
						"Reset suspicious mailbox credentials and review SPF, DKIM, and DMARC configuration.");
				case BasicRiskCalculator.DOMAIN_WITHOUT_HTTPS -> addRecommendation(recommendations, Priority.HIGH,
						"Enable HTTPS.",
						"Install a valid TLS certificate and redirect all HTTP traffic to HTTPS.");
				case BasicRiskCalculator.MISSING_SECURITY_HEADERS -> addRecommendation(recommendations, Priority.MEDIUM,
						"Review security headers.",
						"Add HSTS, Content-Security-Policy, X-Content-Type-Options, Referrer-Policy, and related headers.");
				case BasicRiskCalculator.INVALID_SSL_CERTIFICATE -> addRecommendation(recommendations, Priority.HIGH,
						"Renew or install SSL certificate.",
						"Replace expired or invalid certificates and verify the complete certificate chain.");
				default -> {
				}
			}
		}
		return new ArrayList<>(recommendations.values());
	}

	private void addRecommendation(Map<String, Recommendation> recommendations, Priority priority,
			String description, String action) {
		recommendations.putIfAbsent(description, new Recommendation(priority, description, action));
	}
}
