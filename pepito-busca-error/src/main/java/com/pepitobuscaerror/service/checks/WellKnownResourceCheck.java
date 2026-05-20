package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class WellKnownResourceCheck implements SecurityCheck {

	private static final int MAX_METADATA_BYTES = 12000;

	private final HttpProbeClient httpProbeClient;

	public WellKnownResourceCheck(HttpProbeClient httpProbeClient) {
		this.httpProbeClient = httpProbeClient;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		List<Finding> findings = new ArrayList<>();
		checkSecurityTxt(target, findings);
		checkRobotsTxt(target, findings);
		checkSitemap(target, findings);
		return findings;
	}

	private void checkSecurityTxt(AuditTarget target, List<Finding> findings) {
		HttpProbeClient.ProbeResult result = get(target, "/.well-known/security.txt");
		if (result.statusCode() == 200 && !result.body().isBlank()) {
			findings.add(new Finding(
					FindingCategory.OSINT,
					FindingSeverity.INFO,
					"security.txt contact policy is published",
					extractLines(result.body(), "contact:", "expires:", "policy:", "preferred-languages:"),
					"Keep security.txt contacts current so researchers and customers can report vulnerabilities responsibly."
			));
			return;
		}

		findings.add(new Finding(
				FindingCategory.OSINT,
				FindingSeverity.LOW,
				"security.txt is not published",
				"No readable /.well-known/security.txt file was observed for " + target.getDomain() + ".",
				"Publish a security.txt file with contact and policy fields if the organization accepts vulnerability reports."
		));
	}

	private void checkRobotsTxt(AuditTarget target, List<Finding> findings) {
		HttpProbeClient.ProbeResult result = get(target, "/robots.txt");
		if (result.statusCode() != 200 || result.body().isBlank()) {
			return;
		}

		String disallowed = extractLines(result.body(), "disallow:");
		findings.add(new Finding(
				FindingCategory.OSINT,
				FindingSeverity.INFO,
				"robots.txt is publicly available",
				disallowed.isBlank() ? "robots.txt was found, but no Disallow directives were extracted." : disallowed,
				"Review robots.txt as public information. It should not be used as the only protection for sensitive paths."
		));

		String lowerBody = result.body().toLowerCase(Locale.ROOT);
		if (lowerBody.contains("admin") || lowerBody.contains("backup") || lowerBody.contains("private")
				|| lowerBody.contains("staging") || lowerBody.contains("dev") || lowerBody.contains(".git")) {
			findings.add(new Finding(
					FindingCategory.DATA,
					FindingSeverity.LOW,
					"robots.txt references sensitive-looking paths",
					disallowed.isBlank() ? "robots.txt includes sensitive keywords." : disallowed,
					"Confirm those paths are protected by authentication and remove unnecessary hints from public metadata."
			));
		}
	}

	private void checkSitemap(AuditTarget target, List<Finding> findings) {
		HttpProbeClient.ProbeResult result = get(target, "/sitemap.xml");
		if (result.statusCode() == 200 && !result.body().isBlank()) {
			findings.add(new Finding(
					FindingCategory.OSINT,
					FindingSeverity.INFO,
					"sitemap.xml is publicly available",
					"Observed sitemap.xml with approximately " + Math.min(result.body().length(), 12000)
							+ " bytes downloaded for review.",
					"Review the sitemap for outdated, private, staging, or deprecated URLs that should not be indexed."
			));
		}
	}

	private HttpProbeClient.ProbeResult get(AuditTarget target, String path) {
		return httpProbeClient.get(target, path, MAX_METADATA_BYTES);
	}

	private String extractLines(String body, String... prefixes) {
		List<String> lines = body.lines()
				.map(String::trim)
				.filter(line -> startsWithAny(line, prefixes))
				.limit(10)
				.toList();
		return String.join("\n", lines);
	}

	private boolean startsWithAny(String line, String... prefixes) {
		String lower = line.toLowerCase(Locale.ROOT);
		for (String prefix : prefixes) {
			if (lower.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

}
