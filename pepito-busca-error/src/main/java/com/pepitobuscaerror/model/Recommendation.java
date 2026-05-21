package com.pepitobuscaerror.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendations")
public class Recommendation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_recommendation")
	private Long idRecommendation;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Priority priority;

	@Lob
	@Column(nullable = false)
	private String description;

	@Lob
	@Column(nullable = false)
	private String action;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_analysis", nullable = false)
	private Analysis analysis;

	protected Recommendation() {
	}

	public Recommendation(Priority priority, String description, String action) {
		this.priority = priority;
		this.description = description;
		this.action = action;
	}

	void attachTo(Analysis analysis) {
		this.analysis = analysis;
	}

	public Long getIdRecommendation() {
		return idRecommendation;
	}

	public Priority getPriority() {
		return priority;
	}

	public String getDescription() {
		return description;
	}

	public String getAction() {
		return action;
	}

	public String getResponsibleRole() {
		return switch (priority) {
			case HIGH -> "IT / Security owner";
			case MEDIUM -> "Technical owner";
			case LOW -> "Operations owner";
		};
	}

	public String getSuggestedDeadline() {
		return switch (priority) {
			case HIGH -> "7 days";
			case MEDIUM -> "30 days";
			case LOW -> "90 days";
		};
	}

	public String getTrackingStatus() {
		return "Planned";
	}

	public Analysis getAnalysis() {
		return analysis;
	}
}
