package com.pepitobuscaerror.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AuditTarget {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 140)
	private String name;

	@NotBlank
	@Column(nullable = false, length = 255)
	private String domain;

	@NotBlank
	@Column(nullable = false, length = 500)
	private String url;

	@Column(nullable = false)
	private Instant createdAt = Instant.now();

	@OneToMany(mappedBy = "target")
	private List<ScanRun> scanRuns = new ArrayList<>();

	protected AuditTarget() {
	}

	public AuditTarget(String name, String domain, String url) {
		this.name = name;
		this.domain = domain;
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}

	public String getUrl() {
		return url;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public List<ScanRun> getScanRuns() {
		return scanRuns;
	}
}
