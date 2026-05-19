package com.pepitobuscaerror.service;

import com.pepitobuscaerror.model.Indicator;
import com.pepitobuscaerror.model.RiskLevel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BasicRiskCalculator implements RiskCalculator {

	public static final String SUSPICIOUS_IP_ADDRESS = "Suspicious IP address";
	public static final String SUSPICIOUS_CORPORATE_EMAIL = "Suspicious corporate email";
	public static final String DOMAIN_WITHOUT_HTTPS = "Domain without HTTPS";
	public static final String MISSING_SECURITY_HEADERS = "Missing security headers";
	public static final String INVALID_SSL_CERTIFICATE = "Invalid SSL certificate";

	@Override
	public int calculateRisk(List<Indicator> indicators) {
		int total = indicators.stream()
				.mapToInt(indicator -> scoreFor(indicator.getType()))
				.sum();
		return Math.min(100, total);
	}

	public RiskLevel determineRiskLevel(int riskScore) {
		if (riskScore <= 30) {
			return RiskLevel.LOW;
		}
		if (riskScore <= 60) {
			return RiskLevel.MEDIUM;
		}
		if (riskScore <= 80) {
			return RiskLevel.HIGH;
		}
		return RiskLevel.CRITICAL;
	}

	private int scoreFor(String indicatorType) {
		return switch (indicatorType) {
			case SUSPICIOUS_IP_ADDRESS -> 40;
			case SUSPICIOUS_CORPORATE_EMAIL -> 25;
			case DOMAIN_WITHOUT_HTTPS -> 15;
			case MISSING_SECURITY_HEADERS, INVALID_SSL_CERTIFICATE -> 10;
			default -> 0;
		};
	}
}
