package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.DnsDumpsterResult;
import com.pepitobuscaerror.dto.SubdomainResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class DnsDumpsterClient {

	private final DnsLookupService dnsLookupService;
	private final boolean demoMode;

	public DnsDumpsterClient(DnsLookupService dnsLookupService,
			@Value("${osint.demo-mode:true}") boolean demoMode) {
		this.dnsLookupService = dnsLookupService;
		this.demoMode = demoMode;
	}

	public DnsDumpsterResult analyzeDomain(String domain) {
		List<String> aRecords = dnsLookupService.lookup(domain, "A");
		List<String> aaaaRecords = dnsLookupService.lookup(domain, "AAAA");
		List<String> mxRecords = dnsLookupService.lookup(domain, "MX");
		List<String> nsRecords = dnsLookupService.lookup(domain, "NS");
		List<String> txtRecords = dnsLookupService.lookup(domain, "TXT");

		List<String> dnsSummary = List.of(
				"A records: " + summarize(aRecords),
				"AAAA records: " + summarize(aaaaRecords),
				"MX records: " + summarize(mxRecords),
				"NS records: " + summarize(nsRecords),
				"TXT records: " + summarize(txtRecords)
		);

		String message = demoMode
				? "DNSDumpster-style provider is running in demo mode. Live DNS records are summarized and subdomain examples are simulated."
				: "DNSDumpster-style passive DNS summary generated locally. Subdomain examples remain non-intrusive and simulated.";

		return new DnsDumpsterResult(domain, true, message, demoSubdomains(domain), dnsSummary, mxRecords, nsRecords,
				txtRecords, exposedServiceNotes(domain), riskNotes(txtRecords));
	}

	private List<SubdomainResult> demoSubdomains(String domain) {
		return List.of(
				new SubdomainResult("mail." + domain, "DNSDumpster demo", "MX-related host",
						"Mail hostnames are useful for reviewing SPF, DKIM, and DMARC alignment.", false),
				new SubdomainResult("vpn." + domain, "DNSDumpster demo", "198.51.100.23",
						"VPN-style hostname. Confirm it is intended and protected with MFA.", true),
				new SubdomainResult("dev." + domain, "DNSDumpster demo", "203.0.113.45",
						"Development-style hostname. Validate that non-production systems are not publicly exposed.", true),
				new SubdomainResult("admin." + domain, "DNSDumpster demo", "203.0.113.46",
						"Administration-style hostname. Confirm access controls and patching.", true),
				new SubdomainResult("portal." + domain, "DNSDumpster demo", "203.0.113.47",
						"Customer or employee portal naming pattern.", false)
		);
	}

	private List<String> exposedServiceNotes(String domain) {
		return List.of(
				"mail." + domain + " suggests mail infrastructure should be checked for SPF, DKIM, and DMARC alignment.",
				"vpn." + domain + " and admin." + domain + " are interesting hostnames that deserve manual validation.",
				"No brute force or port scanning was performed; these are safe demo/passive indicators."
		);
	}

	private List<String> riskNotes(List<String> txtRecords) {
		List<String> notes = new ArrayList<>();
		if (txtRecords.stream().anyMatch(this::looksLikeVerificationToken)) {
			notes.add("TXT records include verification-style tokens. Remove records that are no longer required.");
		}
		notes.add("Review demo hostnames such as vpn, dev, and admin against the authorized asset inventory before taking action.");
		return notes;
	}

	private boolean looksLikeVerificationToken(String record) {
		String value = record.toLowerCase(Locale.ROOT);
		return value.contains("google-site-verification")
				|| value.contains("ms=")
				|| value.contains("facebook-domain-verification")
				|| value.contains("apple-domain-verification")
				|| value.contains("atlassian-domain-verification");
	}

	private String summarize(List<String> records) {
		if (records.isEmpty()) {
			return "none observed";
		}
		int limit = Math.min(records.size(), 5);
		String summary = String.join("; ", records.subList(0, limit));
		if (records.size() > limit) {
			summary += "; +" + (records.size() - limit) + " more";
		}
		return summary;
	}
}
