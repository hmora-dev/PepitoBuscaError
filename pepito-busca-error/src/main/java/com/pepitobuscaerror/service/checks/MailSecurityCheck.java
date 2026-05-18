package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import com.pepitobuscaerror.service.DnsLookupService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class MailSecurityCheck implements SecurityCheck {

	private final DnsLookupService dnsLookupService;

	public MailSecurityCheck(DnsLookupService dnsLookupService) {
		this.dnsLookupService = dnsLookupService;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		List<Finding> findings = new ArrayList<>();
		String domain = target.getDomain();

		List<String> mxRecords = dnsLookupService.lookup(domain, "MX");
		if (mxRecords.isEmpty()) {
			findings.add(new Finding(
					FindingCategory.MAIL,
					FindingSeverity.MEDIUM,
					"No MX records were found",
					domain + " has no visible mail exchanger records.",
					"Add MX records if the domain sends or receives email. If it does not use email, publish SPF and DMARC policies that reject mail."
			));
		}

		List<String> spfRecords = dnsLookupService.lookupTxtStartingWith(domain, "v=spf1");
		if (spfRecords.isEmpty()) {
			findings.add(new Finding(
					FindingCategory.MAIL,
					FindingSeverity.HIGH,
					"SPF record is missing",
					"No TXT record starting with v=spf1 was found for " + domain + ".",
					"Publish an SPF record that lists authorized mail senders and ends with a restrictive policy such as -all."
			));
		} else if (spfRecords.size() > 1) {
			findings.add(new Finding(
					FindingCategory.MAIL,
					FindingSeverity.HIGH,
					"Multiple SPF records found",
					"Found " + spfRecords.size() + " SPF records. Mail receivers may treat this as invalid.",
					"Merge SPF mechanisms into one TXT record for the domain."
			));
		} else {
			String spf = spfRecords.get(0).toLowerCase(Locale.ROOT);
			if (spf.contains("+all") || spf.contains("?all")) {
				findings.add(new Finding(
						FindingCategory.MAIL,
						FindingSeverity.HIGH,
						"SPF policy is too permissive",
						"SPF record: " + spfRecords.get(0),
						"Replace +all or ?all with -all after confirming every legitimate sender is included."
				));
			} else if (spf.contains("~all")) {
				findings.add(new Finding(
						FindingCategory.MAIL,
						FindingSeverity.MEDIUM,
						"SPF uses soft fail",
						"SPF record: " + spfRecords.get(0),
						"Move from ~all to -all when legitimate mail sources are fully documented."
				));
			}
		}

		List<String> dmarcRecords = dnsLookupService.lookupTxtStartingWith("_dmarc." + domain, "v=dmarc1");
		if (dmarcRecords.isEmpty()) {
			findings.add(new Finding(
					FindingCategory.MAIL,
					FindingSeverity.HIGH,
					"DMARC record is missing",
					"No DMARC TXT record was found at _dmarc." + domain + ".",
					"Publish a DMARC record. Start with monitoring, then move to quarantine or reject once reports are clean."
			));
		} else if (dmarcRecords.size() > 1) {
			findings.add(new Finding(
					FindingCategory.MAIL,
					FindingSeverity.HIGH,
					"Multiple DMARC records found",
					"Found " + dmarcRecords.size() + " DMARC records.",
					"Keep exactly one DMARC TXT record at _dmarc." + domain + "."
			));
		} else {
			String dmarc = dmarcRecords.get(0).toLowerCase(Locale.ROOT);
			if (dmarc.contains("p=none")) {
				findings.add(new Finding(
						FindingCategory.MAIL,
						FindingSeverity.MEDIUM,
						"DMARC is only monitoring",
						"DMARC record: " + dmarcRecords.get(0),
						"After validating mail flows, change the policy to quarantine or reject."
				));
			}
			if (!dmarc.contains("rua=")) {
				findings.add(new Finding(
						FindingCategory.MAIL,
						FindingSeverity.LOW,
						"DMARC aggregate reporting is not configured",
						"DMARC record: " + dmarcRecords.get(0),
						"Add a rua mailbox to receive aggregate authentication reports."
				));
			}
		}

		if (dnsLookupService.lookupTxtStartingWith("_mta-sts." + domain, "v=stsv1").isEmpty()) {
			findings.add(new Finding(
					FindingCategory.MAIL,
					FindingSeverity.LOW,
					"MTA-STS policy record is missing",
					"No _mta-sts TXT policy record was found.",
					"Configure MTA-STS to improve protection for inbound mail transport security."
			));
		}

		if (dnsLookupService.lookupTxtStartingWith("_smtp._tls." + domain, "v=tlsrptv1").isEmpty()) {
			findings.add(new Finding(
					FindingCategory.MAIL,
					FindingSeverity.LOW,
					"SMTP TLS reporting is missing",
					"No TLS-RPT TXT record was found at _smtp._tls." + domain + ".",
					"Publish a TLS-RPT record so delivery systems can report TLS failures."
			));
		}

		return findings;
	}
}
