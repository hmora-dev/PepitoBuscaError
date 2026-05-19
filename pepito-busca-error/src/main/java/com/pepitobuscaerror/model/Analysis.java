package com.pepitobuscaerror.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analyses")
public class Analysis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_analysis")
	private Long idAnalysis;

	@Column(name = "analysis_date", nullable = false)
	private LocalDateTime analysisDate;

	@Column(name = "risk_score", nullable = false)
	private int riskScore;

	@Enumerated(EnumType.STRING)
	@Column(name = "risk_level", nullable = false, length = 20)
	private RiskLevel riskLevel = RiskLevel.LOW;

	@Column(nullable = false, length = 40)
	private String status = "COMPLETED";

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_company", nullable = false)
	private Company company;

	@OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("idIndicator ASC")
	private List<Indicator> indicators = new ArrayList<>();

	@OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("idRecommendation ASC")
	private List<Recommendation> recommendations = new ArrayList<>();

	protected Analysis() {
	}

	public Analysis(Company company) {
		this.company = company;
	}

	@PrePersist
	void onCreate() {
		if (analysisDate == null) {
			analysisDate = LocalDateTime.now();
		}
	}

	public void complete(int riskScore, RiskLevel riskLevel) {
		this.riskScore = Math.min(100, Math.max(0, riskScore));
		this.riskLevel = riskLevel;
		this.status = "COMPLETED";
	}

	public void addIndicator(Indicator indicator) {
		indicator.attachTo(this);
		indicators.add(indicator);
	}

	public void addRecommendation(Recommendation recommendation) {
		recommendation.attachTo(this);
		recommendations.add(recommendation);
	}

	public Long getIdAnalysis() {
		return idAnalysis;
	}

	public LocalDateTime getAnalysisDate() {
		return analysisDate;
	}

	public int getRiskScore() {
		return riskScore;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public String getStatus() {
		return status;
	}

	public Company getCompany() {
		return company;
	}

	public List<Indicator> getIndicators() {
		return indicators;
	}

	public List<Recommendation> getRecommendations() {
		return recommendations;
	}
}
