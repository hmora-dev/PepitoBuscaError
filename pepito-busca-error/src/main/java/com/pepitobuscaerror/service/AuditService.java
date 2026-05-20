package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.TargetForm;
import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import com.pepitobuscaerror.model.ScanRun;
import com.pepitobuscaerror.repository.AuditTargetRepository;
import com.pepitobuscaerror.repository.ScanRunRepository;
import com.pepitobuscaerror.service.checks.SecurityCheck;
import com.pepitobuscaerror.util.TargetNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {

	private final AuditTargetRepository targetRepository;
	private final ScanRunRepository scanRunRepository;
	private final List<SecurityCheck> securityChecks;

	public AuditService(AuditTargetRepository targetRepository, ScanRunRepository scanRunRepository,
			List<SecurityCheck> securityChecks) {
		this.targetRepository = targetRepository;
		this.scanRunRepository = scanRunRepository;
		this.securityChecks = securityChecks.stream()
				.sorted(Comparator.comparing(check -> check.getClass().getSimpleName()))
				.toList();
	}

	@Transactional
	public ScanRun createAndRun(TargetForm form) {
		TargetNormalizer.NormalizedTarget normalized = TargetNormalizer.normalize(form.getDomainOrUrl(), form.getName());
		AuditTarget target = targetRepository.findFirstByDomainIgnoreCase(normalized.domain())
				.orElseGet(() -> targetRepository.save(new AuditTarget(
						normalized.name(),
						normalized.domain(),
						normalized.url()
				)));

		ScanRun scanRun = new ScanRun(target);
		try {
			for (SecurityCheck securityCheck : securityChecks) {
				runCheck(securityCheck, target, scanRun);
			}
			if (scanRun.getFindings().isEmpty()) {
				scanRun.addFinding(new Finding(
						FindingCategory.OSINT,
						FindingSeverity.INFO,
						"No immediate issues detected",
						"The first-pass checks completed without producing findings.",
						"Keep monitoring the target and add deeper authorized checks as the platform evolves."
				));
			}
			scanRun.complete(calculateRiskScore(scanRun.getFindings()));
		} catch (RuntimeException exception) {
			scanRun.addFinding(new Finding(
					FindingCategory.AVAILABILITY,
					FindingSeverity.HIGH,
					"Scan failed before completion",
					exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage(),
					"Review the target input and application logs, then run the scan again."
			));
			scanRun.fail(calculateRiskScore(scanRun.getFindings()));
		}
		return scanRunRepository.save(scanRun);
	}

	private void runCheck(SecurityCheck securityCheck, AuditTarget target, ScanRun scanRun) {
		try {
			securityCheck.analyze(target).forEach(scanRun::addFinding);
		} catch (RuntimeException exception) {
			scanRun.addFinding(new Finding(
					FindingCategory.AVAILABILITY,
					FindingSeverity.LOW,
					"Passive check could not complete",
					securityCheck.getClass().getSimpleName() + " failed for " + target.getDomain() + ": "
							+ describe(exception),
					"Keep the rest of the report and rerun the OSINT scan later. If this repeats, confirm DNS, outbound HTTP, and TLS connectivity from the application host."
			));
		}
	}

	@Transactional(readOnly = true)
	public List<ScanRun> recentScans() {
		return scanRunRepository.findTop12ByOrderByStartedAtDesc();
	}

	@Transactional(readOnly = true)
	public List<AuditTarget> recentTargets() {
		return targetRepository.findTop8ByOrderByCreatedAtDesc();
	}

	@Transactional(readOnly = true)
	public ScanRun getScan(Long id) {
		return scanRunRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Scan not found"));
	}

	public Map<FindingSeverity, Long> countBySeverity(List<Finding> findings) {
		Map<FindingSeverity, Long> counts = new EnumMap<>(FindingSeverity.class);
		for (FindingSeverity severity : FindingSeverity.values()) {
			counts.put(severity, findings.stream().filter(finding -> finding.getSeverity() == severity).count());
		}
		return counts;
	}

	public Map<FindingCategory, Long> countByCategory(List<Finding> findings) {
		Map<FindingCategory, Long> counts = new EnumMap<>(FindingCategory.class);
		for (FindingCategory category : FindingCategory.values()) {
			counts.put(category, findings.stream().filter(finding -> finding.getCategory() == category).count());
		}
		return counts;
	}

	public List<Finding> priorityFindings(List<Finding> findings) {
		return sortFindings(findings).stream()
				.filter(finding -> finding.getSeverity() != FindingSeverity.INFO)
				.limit(5)
				.toList();
	}

	public List<Finding> sortFindings(List<Finding> findings) {
		return findings.stream()
				.sorted(Comparator.comparing((Finding finding) -> finding.getSeverity().getScoreWeight()).reversed()
						.thenComparing(finding -> finding.getCategory().getLabel())
						.thenComparing(Finding::getTitle))
				.toList();
	}

	private int calculateRiskScore(List<Finding> findings) {
		return findings.stream()
				.mapToInt(finding -> finding.getSeverity().getScoreWeight())
				.sum();
	}

	private String describe(RuntimeException exception) {
		if (exception.getMessage() == null || exception.getMessage().isBlank()) {
			return exception.getClass().getSimpleName();
		}
		return exception.getMessage();
	}
}
