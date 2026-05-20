package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.DnsDumpsterResult;
import com.pepitobuscaerror.dto.HibpResult;
import com.pepitobuscaerror.dto.OsintDomainResult;
import com.pepitobuscaerror.dto.SecurityTrailsResult;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OsintService {

	private final DnsDumpsterClient dnsDumpsterClient;
	private final SecurityTrailsClient securityTrailsClient;
	private final HaveIBeenPwnedClient haveIBeenPwnedClient;

	public OsintService(DnsDumpsterClient dnsDumpsterClient, SecurityTrailsClient securityTrailsClient,
			HaveIBeenPwnedClient haveIBeenPwnedClient) {
		this.dnsDumpsterClient = dnsDumpsterClient;
		this.securityTrailsClient = securityTrailsClient;
		this.haveIBeenPwnedClient = haveIBeenPwnedClient;
	}

	public OsintDomainResult analyzeDomain(String domain) {
		String normalizedDomain = normalizeDomain(domain);
		DnsDumpsterResult dnsDumpsterResult = dnsDumpsterClient.analyzeDomain(normalizedDomain);
		SecurityTrailsResult securityTrailsResult = securityTrailsClient.analyzeDomain(normalizedDomain);
		return new OsintDomainResult(normalizedDomain, dnsDumpsterResult, securityTrailsResult,
				buildDomainRecommendations(dnsDumpsterResult, securityTrailsResult), Instant.now());
	}

	public HibpResult checkEmail(String email) {
		return haveIBeenPwnedClient.checkEmail(normalizeEmail(email));
	}

	public List<Finding> toFindings(OsintDomainResult result) {
		List<Finding> findings = new ArrayList<>();
		DnsDumpsterResult dnsDumpster = result.getDnsDumpsterResult();
		findings.add(new Finding(
				FindingCategory.DNS,
				FindingSeverity.INFO,
				"DNSDumpster-style domain intelligence collected",
				String.join("\n",
						"Domain: " + result.getDomain(),
						"Mode: " + modeLabel(dnsDumpster.isDemoMode()),
						"Subdomains: " + dnsDumpster.getSubdomainCount(),
						"DNS summary: " + String.join(" | ", dnsDumpster.getDnsRecordsSummary()),
						"Risk notes: " + summarize(dnsDumpster.getRiskNotes())),
				"Use the DNSDumpster-style results to confirm public hostnames, mail records, DNS ownership, and exposed administration-style names."
		));

		SecurityTrailsResult securityTrails = result.getSecurityTrailsResult();
		FindingSeverity severity = securityTrails.getRiskNotes().stream()
				.anyMatch(note -> note.toLowerCase(Locale.ROOT).contains("vpn")
						|| note.toLowerCase(Locale.ROOT).contains("admin")
						|| note.toLowerCase(Locale.ROOT).contains("dev"))
				? FindingSeverity.LOW : FindingSeverity.INFO;
		findings.add(new Finding(
				FindingCategory.OSINT,
				severity,
				"SecurityTrails-style DNS intelligence collected",
				String.join("\n",
						"Domain: " + result.getDomain(),
						"Mode: " + modeLabel(securityTrails.isDemoMode()),
						"Current DNS records: " + securityTrails.getCurrentRecords().size(),
						"Subdomains: " + securityTrails.getSubdomainCount(),
						"Associated IPs: " + summarize(securityTrails.getAssociatedIps()),
						"Nameservers: " + summarize(securityTrails.getNameservers()),
						"Mail servers: " + summarize(securityTrails.getMailServers()),
						"Risk notes: " + summarize(securityTrails.getRiskNotes())),
				"Review public DNS relationships and verify that interesting hostnames belong to the authorized inventory."
		));
		return findings;
	}

	private List<String> buildDomainRecommendations(DnsDumpsterResult dnsDumpsterResult,
			SecurityTrailsResult securityTrailsResult) {
		List<String> recommendations = new ArrayList<>();
		recommendations.add("Confirm every public subdomain belongs to an approved system owner.");
		recommendations.add("Review mail records, SPF, DMARC, nameservers, and TXT verification records for stale entries.");
		if (!dnsDumpsterResult.getExposedServices().isEmpty()) {
			recommendations.add("Manually validate interesting hostnames such as vpn, dev, admin, and portal without running intrusive scans.");
		}
		if (securityTrailsResult.isDemoMode()) {
			recommendations.add("Configure the SecurityTrails API key and set demo mode to false when real provider data is required.");
		}
		return recommendations;
	}

	private String summarize(List<String> values) {
		if (values == null || values.isEmpty()) {
			return "none observed";
		}
		int limit = Math.min(values.size(), 6);
		String summary = String.join("; ", values.subList(0, limit));
		if (values.size() > limit) {
			summary += "; +" + (values.size() - limit) + " more";
		}
		return summary;
	}

	private String modeLabel(boolean demoMode) {
		return demoMode ? "demo data" : "API data";
	}

	private String normalizeDomain(String domain) {
		return domain == null ? "" : domain.trim().toLowerCase(Locale.ROOT);
	}

	private String normalizeEmail(String email) {
		return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
	}
}
