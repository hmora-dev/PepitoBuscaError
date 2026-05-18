package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import com.pepitobuscaerror.service.DnsLookupService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DnsRecordCheck implements SecurityCheck {

	private final DnsLookupService dnsLookupService;

	public DnsRecordCheck(DnsLookupService dnsLookupService) {
		this.dnsLookupService = dnsLookupService;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		List<Finding> findings = new ArrayList<>();
		String domain = target.getDomain();

		List<String> aRecords = dnsLookupService.lookup(domain, "A");
		List<String> aaaaRecords = dnsLookupService.lookup(domain, "AAAA");
		if (aRecords.isEmpty() && aaaaRecords.isEmpty()) {
			findings.add(new Finding(
					FindingCategory.DNS,
					FindingSeverity.HIGH,
					"Domain has no public A or AAAA record",
					"No IPv4 or IPv6 address was found for " + domain + ".",
					"Create valid A/AAAA records or confirm this domain is not expected to host services."
			));
		}

		if (dnsLookupService.lookup(domain, "NS").isEmpty()) {
			findings.add(new Finding(
					FindingCategory.DNS,
					FindingSeverity.HIGH,
					"Domain has no name server records",
					"No NS records were returned for " + domain + ".",
					"Configure at least two reliable authoritative name servers."
			));
		}

		if (dnsLookupService.lookup(domain, "CAA").isEmpty()) {
			findings.add(new Finding(
					FindingCategory.DNS,
					FindingSeverity.LOW,
					"CAA records are missing",
					"No CAA records were found for " + domain + ".",
					"Add CAA records to restrict which certificate authorities can issue certificates for this domain."
			));
		}

		if (dnsLookupService.lookup(domain, "DS").isEmpty()) {
			findings.add(new Finding(
					FindingCategory.DNS,
					FindingSeverity.LOW,
					"DNSSEC delegation is not visible",
					"No DS records were found for " + domain + ".",
					"Enable DNSSEC at the registrar and DNS provider if the domain supports it."
			));
		}

		List<String> cnameRecords = dnsLookupService.lookup(domain, "CNAME");
		for (String cname : cnameRecords) {
			String canonicalName = cname.endsWith(".") ? cname.substring(0, cname.length() - 1) : cname;
			boolean targetResolves = !dnsLookupService.lookup(canonicalName, "A").isEmpty()
					|| !dnsLookupService.lookup(canonicalName, "AAAA").isEmpty();
			if (!targetResolves) {
				findings.add(new Finding(
						FindingCategory.DNS,
						FindingSeverity.HIGH,
						"CNAME points to a target that does not resolve",
						domain + " points to " + canonicalName + ", but that target has no A/AAAA records.",
						"Remove the stale CNAME or point it to an active controlled service."
				));
			}
		}

		return findings;
	}
}
