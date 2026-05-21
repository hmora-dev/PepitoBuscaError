package com.pepitobuscaerror.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_company")
	private Long idCompany;

	@NotBlank(message = "Company name is required")
	@Size(min = 2, max = 140, message = "Company name must be between 2 and 140 characters")
	@Column(nullable = false, length = 140)
	private String name;

	@NotBlank(message = "Domain is required")
	@Size(max = 255, message = "Domain is too long")
	@Column(nullable = false, length = 255)
	private String domain;

	@NotBlank(message = "Corporate email is required")
	@Email(message = "Enter a valid email address")
	@Size(max = 255, message = "Corporate email is too long")
	@Column(name = "corporate_email", nullable = false, length = 255)
	private String corporateEmail;

	@NotBlank(message = "Sector is required")
	@Size(max = 120, message = "Sector is too long")
	@Column(nullable = false, length = 120)
	private String sector;

	@Column(name = "registration_date", nullable = false)
	private LocalDateTime registrationDate;

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("analysisDate DESC")
	private List<Analysis> analyses = new ArrayList<>();

	public Company() {
	}

	@PrePersist
	void onCreate() {
		if (registrationDate == null) {
			registrationDate = LocalDateTime.now();
		}
	}

	public Long getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(Long idCompany) {
		this.idCompany = idCompany;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCorporateEmail() {
		return corporateEmail;
	}

	public void setCorporateEmail(String corporateEmail) {
		this.corporateEmail = corporateEmail;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public List<Analysis> getAnalyses() {
		return analyses;
	}

	public int getAnalysisCount() {
		return analyses.size();
	}

	public Analysis getLatestAnalysis() {
		return analyses.stream()
				.max(Comparator.comparing(Analysis::getAnalysisDate))
				.orElse(null);
	}

	public RiskLevel getLatestRiskLevel() {
		Analysis latestAnalysis = getLatestAnalysis();
		return latestAnalysis == null ? null : latestAnalysis.getRiskLevel();
	}

	public Integer getLatestRiskScore() {
		Analysis latestAnalysis = getLatestAnalysis();
		return latestAnalysis == null ? null : latestAnalysis.getRiskScore();
	}

	public int getLatestFindingCount() {
		Analysis latestAnalysis = getLatestAnalysis();
		return latestAnalysis == null ? 0 : latestAnalysis.getIndicators().size();
	}

	public int getLatestRecommendationCount() {
		Analysis latestAnalysis = getLatestAnalysis();
		return latestAnalysis == null ? 0 : latestAnalysis.getRecommendations().size();
	}

	public Analysis getPreviousAnalysis() {
		List<Analysis> sortedAnalyses = analyses.stream()
				.sorted(Comparator.comparing(Analysis::getAnalysisDate).reversed())
				.toList();
		return sortedAnalyses.size() < 2 ? null : sortedAnalyses.get(1);
	}

	public Integer getPreviousRiskScore() {
		Analysis previousAnalysis = getPreviousAnalysis();
		return previousAnalysis == null ? null : previousAnalysis.getRiskScore();
	}

	public Integer getAverageRiskScore() {
		if (analyses.isEmpty()) {
			return null;
		}
		return (int) Math.round(analyses.stream()
				.mapToInt(Analysis::getRiskScore)
				.average()
				.orElse(0));
	}

	public String getRiskTrendLabel() {
		Integer latestRiskScore = getLatestRiskScore();
		Integer previousRiskScore = getPreviousRiskScore();
		if (latestRiskScore == null || previousRiskScore == null) {
			return "No trend yet";
		}
		if (latestRiskScore < previousRiskScore) {
			return "Improving";
		}
		if (latestRiskScore > previousRiskScore) {
			return "Worsening";
		}
		return "Stable";
	}

	public String getRiskTrendClass() {
		return switch (getRiskTrendLabel()) {
			case "Improving" -> "low";
			case "Worsening" -> "high";
			case "Stable" -> "medium";
			default -> "neutral";
		};
	}
}
