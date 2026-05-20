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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScanRun {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private AuditTarget target;

	@Column(nullable = false)
	private Instant startedAt = Instant.now();

	private Instant completedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private ScanStatus status = ScanStatus.RUNNING;

	@Column(nullable = false)
	private int riskScore;

	@OneToMany(mappedBy = "scanRun", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("severity DESC, category ASC, id ASC")
	private List<Finding> findings = new ArrayList<>();

	protected ScanRun() {
	}

	public ScanRun(AuditTarget target) {
		this.target = target;
	}

	public void complete(int riskScore) {
		this.status = ScanStatus.COMPLETED;
		this.completedAt = Instant.now();
		this.riskScore = Math.min(100, Math.max(0, riskScore));
	}

	public void fail() {
		fail(riskScore);
	}

	public void fail(int riskScore) {
		this.status = ScanStatus.FAILED;
		this.completedAt = Instant.now();
		this.riskScore = Math.min(100, Math.max(0, riskScore));
	}

	public void addFinding(Finding finding) {
		finding.attachTo(this);
		findings.add(finding);
	}

	public Long getId() {
		return id;
	}

	public AuditTarget getTarget() {
		return target;
	}

	public Instant getStartedAt() {
		return startedAt;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public ScanStatus getStatus() {
		return status;
	}

	public int getRiskScore() {
		return riskScore;
	}

	public String getRiskLabel() {
		if (riskScore >= 60) {
			return "Critical";
		}
		if (riskScore >= 25) {
			return "High";
		}
		if (riskScore >= 10) {
			return "Medium";
		}
		return "Low";
	}

	public String getRiskClass() {
		return getRiskLabel().toLowerCase();
	}

	public List<Finding> getFindings() {
		return findings;
	}
}
