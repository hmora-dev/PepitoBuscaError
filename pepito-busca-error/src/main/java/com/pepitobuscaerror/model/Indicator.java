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
@Table(name = "indicators")
public class Indicator {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_indicator")
	private Long idIndicator;

	@Column(nullable = false, length = 100)
	private String type;

	@Column(name = "indicator_value", nullable = false, length = 180)
	private String value;

	@Lob
	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Severity severity;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_analysis", nullable = false)
	private Analysis analysis;

	protected Indicator() {
	}

	public Indicator(String type, String value, String description, Severity severity) {
		this.type = type;
		this.value = value;
		this.description = description;
		this.severity = severity;
	}

	void attachTo(Analysis analysis) {
		this.analysis = analysis;
	}

	public Long getIdIndicator() {
		return idIndicator;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public Severity getSeverity() {
		return severity;
	}

	public Analysis getAnalysis() {
		return analysis;
	}
}
