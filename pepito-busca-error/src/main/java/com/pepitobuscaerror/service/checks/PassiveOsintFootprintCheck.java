package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import com.pepitobuscaerror.service.DnsLookupService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class PassiveOsintFootprintCheck implements SecurityCheck {

	private final DnsLookupService dnsLookupService;

	public PassiveOsintFootprintCheck(DnsLookupService dnsLookupService) {
		this.dnsLookupService = dnsLookupService;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		String domain = target.getDomain();
		List<String> aRecords = dnsLookupService.lookup(domain, "A");
		List<String> aaaaRecords = dnsLookupService.lookup(domain, "AAAA");
		List<String> nsRecords = dnsLookupService.lookup(domain, "NS");
		List<String> mxRecords = dnsLookupService.lookup(domain, "MX");
		List<String> txtRecords = dnsLookupService.lookup(domain, "TXT");
		List<String> caaRecords = dnsLookupService.lookup(domain, "CAA");
		List<String> dsRecords = dnsLookupService.lookup(domain, "DS");

		List<Finding> findings = new ArrayList<>();
		findings.add(new Finding(
				FindingCategory.OSINT,
				FindingSeverity.INFO,
				"Passive public footprint collected",
				String.join("\n",
						"A records: " + summarize(aRecords),
						"AAAA records: " + summarize(aaaaRecords),
						"Name servers: " + summarize(nsRecords),
						"Mail exchangers: " + summarize(mxRecords),
						"TXT records: " + summarize(txtRecords),
						"CAA records: " + summarize(caaRecords),
						"DS records: " + summarize(dsRecords)),
				"Use this inventory as a first public exposure baseline. Confirm that every visible service and provider is expected."
		));

		Set<String> providers = identifyProviders(aRecords, aaaaRecords, nsRecords, mxRecords, txtRecords, caaRecords);
		if (!providers.isEmpty()) {
			findings.add(new Finding(
					FindingCategory.OSINT,
					FindingSeverity.INFO,
					"Likely third-party providers identified",
					"Public records suggest these providers or services: " + String.join(", ", providers) + ".",
					"Validate vendor ownership, billing contacts, administrator access, and offboarding procedures for each public dependency."
			));
		}

		List<String> verificationTokens = txtRecords.stream()
				.filter(this::looksLikeVerificationToken)
				.toList();
		if (!verificationTokens.isEmpty()) {
			findings.add(new Finding(
					FindingCategory.DATA,
					FindingSeverity.LOW,
					"Public verification tokens are visible",
					"TXT records include verification-style values: " + summarize(verificationTokens),
					"Remove stale verification records after service ownership has been validated, keeping only records still required by active providers."
			));
		}

		return findings;
	}

	@SafeVarargs
	private Set<String> identifyProviders(List<String>... recordGroups) {
		Set<String> providers = new LinkedHashSet<>();
		for (List<String> records : recordGroups) {
			for (String record : records) {
				String value = record.toLowerCase(Locale.ROOT);
				if (value.contains("cloudflare")) {
					providers.add("Cloudflare");
				}
				if (value.contains("google") || value.contains("googledomain") || value.contains("_spf.google.com")) {
					providers.add("Google Workspace / Google Cloud");
				}
				if (value.contains("outlook") || value.contains("protection.outlook") || value.contains("spf.protection.outlook")) {
					providers.add("Microsoft 365");
				}
				if (value.contains("amazonses") || value.contains("amazonaws") || value.contains("awsdns")) {
					providers.add("Amazon Web Services");
				}
				if (value.contains("github")) {
					providers.add("GitHub");
				}
				if (value.contains("vercel")) {
					providers.add("Vercel");
				}
				if (value.contains("netlify")) {
					providers.add("Netlify");
				}
				if (value.contains("shopify")) {
					providers.add("Shopify");
				}
				if (value.contains("sendgrid")) {
					providers.add("SendGrid");
				}
				if (value.contains("mailgun")) {
					providers.add("Mailgun");
				}
			}
		}
		return providers;
	}

	private boolean looksLikeVerificationToken(String record) {
		String value = record.toLowerCase(Locale.ROOT);
		return value.contains("google-site-verification")
				|| value.contains("ms=")
				|| value.contains("facebook-domain-verification")
				|| value.contains("apple-domain-verification")
				|| value.contains("atlassian-domain-verification")
				|| value.contains("dropbox-domain-verification")
				|| value.contains("canva-site-verification");
	}

	private String summarize(List<String> records) {
		if (records.isEmpty()) {
			return "none observed";
		}
		int limit = Math.min(records.size(), 6);
		String summary = String.join("; ", records.subList(0, limit));
		if (records.size() > limit) {
			summary += "; +" + (records.size() - limit) + " more";
		}
		return summary;
	}
}
