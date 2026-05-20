package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class WebFingerprintCheck implements SecurityCheck {

	private final HttpProbeClient httpProbeClient;

	public WebFingerprintCheck(HttpProbeClient httpProbeClient) {
		this.httpProbeClient = httpProbeClient;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		HttpProbeClient.ProbeResult response = httpProbeClient.get(target, "", 0);
		if (!response.successful()) {
			return List.of(new Finding(
					FindingCategory.OSINT,
					FindingSeverity.LOW,
					"Web fingerprint could not be collected",
					"Request to " + target.getUrl() + " failed: " + response.failureMessage(),
					"Confirm whether the public web presence is expected to be reachable from the internet."
			));
		}
		return buildFindings(target, response);
	}

	private List<Finding> buildFindings(AuditTarget target, HttpProbeClient.ProbeResult response) {
		Map<String, List<String>> headers = response.headers();
		String evidence = String.join("\n",
				"Requested URL: " + target.getUrl(),
				"Final URI: " + response.uri(),
				"HTTP status: " + response.statusCode(),
				"Server: " + firstHeader(headers, "server"),
				"X-Powered-By: " + firstHeader(headers, "x-powered-by"),
				"Content-Type: " + firstHeader(headers, "content-type"),
				"Cache/CDN headers: " + summarizeCdnHeaders(headers),
				"HTTPS fallback: " + (response.usedFallback() ? response.fallbackReason() : "not used"));

		List<Finding> findings = new ArrayList<>();
		findings.add(new Finding(
				FindingCategory.OSINT,
				FindingSeverity.INFO,
				"Web technology fingerprint collected",
				evidence,
				"Use this fingerprint to confirm whether the visible platform, hosting layer, and cache/CDN behavior match the expected architecture."
		));

		Set<String> providers = identifyEdgeProviders(headers);
		if (!providers.isEmpty()) {
			findings.add(new Finding(
					FindingCategory.OSINT,
					FindingSeverity.INFO,
					"Likely edge or hosting provider identified",
					"Header evidence suggests: " + String.join(", ", providers) + ".",
					"Confirm that these public delivery providers are approved and that access to their control panels is managed."
			));
		}
		return findings;
	}

	private Set<String> identifyEdgeProviders(Map<String, List<String>> headers) {
		String joinedHeaders = headers.entrySet().stream()
				.map(entry -> entry.getKey() + ":" + String.join(",", entry.getValue()))
				.reduce("", (left, right) -> left + "\n" + right)
				.toLowerCase(Locale.ROOT);
		Set<String> providers = new LinkedHashSet<>();
		if (joinedHeaders.contains("cloudflare") || joinedHeaders.contains("cf-ray")) {
			providers.add("Cloudflare");
		}
		if (joinedHeaders.contains("cloudfront") || joinedHeaders.contains("x-amz-cf")) {
			providers.add("Amazon CloudFront");
		}
		if (joinedHeaders.contains("fastly") || joinedHeaders.contains("x-served-by")) {
			providers.add("Fastly");
		}
		if (joinedHeaders.contains("akamai")) {
			providers.add("Akamai");
		}
		if (joinedHeaders.contains("vercel")) {
			providers.add("Vercel");
		}
		if (joinedHeaders.contains("netlify")) {
			providers.add("Netlify");
		}
		return providers;
	}

	private String summarizeCdnHeaders(Map<String, List<String>> headers) {
		List<String> names = List.of("cf-ray", "x-cache", "x-served-by", "via", "server-timing", "x-amz-cf-pop");
		List<String> values = names.stream()
				.map(name -> name + "=" + firstHeader(headers, name))
				.filter(value -> !value.endsWith("not observed"))
				.toList();
		return values.isEmpty() ? "not observed" : String.join("; ", values);
	}

	private String firstHeader(Map<String, List<String>> headers, String name) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name) && !entry.getValue().isEmpty()) {
				return entry.getValue().get(0);
			}
		}
		return "not observed";
	}
}
