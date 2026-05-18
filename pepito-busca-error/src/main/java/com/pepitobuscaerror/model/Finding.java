package com.pepitobuscaerror.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Finding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private ScanRun scanRun;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private FindingCategory category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private FindingSeverity severity;

	@Column(nullable = false, length = 180)
	private String title;

	@Lob
	@Column(nullable = false)
	private String evidence;

	@Lob
	@Column(nullable = false)
	private String recommendation;

	protected Finding() {
	}

	public Finding(FindingCategory category, FindingSeverity severity, String title, String evidence, String recommendation) {
		this.category = category;
		this.severity = severity;
		this.title = title;
		this.evidence = evidence;
		this.recommendation = recommendation;
	}

	public void attachTo(ScanRun scanRun) {
		this.scanRun = scanRun;
	}

	public Long getId() {
		return id;
	}

	public FindingCategory getCategory() {
		return category;
	}

	public FindingSeverity getSeverity() {
		return severity;
	}

	public String getTitle() {
		return title;
	}

	public String getEvidence() {
		return evidence;
	}

	public String getRecommendation() {
		return recommendation;
	}
}
