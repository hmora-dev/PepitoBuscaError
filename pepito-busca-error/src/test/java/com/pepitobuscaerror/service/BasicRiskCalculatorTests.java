package com.pepitobuscaerror.service;

import com.pepitobuscaerror.model.Indicator;
import com.pepitobuscaerror.model.RiskLevel;
import com.pepitobuscaerror.model.Severity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BasicRiskCalculatorTests {

	private final BasicRiskCalculator calculator = new BasicRiskCalculator();

	@Test
	void calculatesKnownIndicatorScore() {
		List<Indicator> indicators = List.of(
				new Indicator(BasicRiskCalculator.SUSPICIOUS_IP_ADDRESS, "203.0.113.10", "Review hosting",
						Severity.CRITICAL),
				new Indicator(BasicRiskCalculator.DOMAIN_WITHOUT_HTTPS, "http://example.com", "Enable HTTPS",
						Severity.MEDIUM)
		);

		assertThat(calculator.calculateRisk(indicators)).isEqualTo(55);
	}

	@Test
	void capsRiskScoreAtOneHundred() {
		List<Indicator> indicators = List.of(
				new Indicator(BasicRiskCalculator.SUSPICIOUS_IP_ADDRESS, "1", "A", Severity.CRITICAL),
				new Indicator(BasicRiskCalculator.SUSPICIOUS_IP_ADDRESS, "2", "B", Severity.CRITICAL),
				new Indicator(BasicRiskCalculator.SUSPICIOUS_IP_ADDRESS, "3", "C", Severity.CRITICAL)
		);

		assertThat(calculator.calculateRisk(indicators)).isEqualTo(100);
	}

	@Test
	void determinesRiskLevels() {
		assertThat(calculator.determineRiskLevel(30)).isEqualTo(RiskLevel.LOW);
		assertThat(calculator.determineRiskLevel(45)).isEqualTo(RiskLevel.MEDIUM);
		assertThat(calculator.determineRiskLevel(75)).isEqualTo(RiskLevel.HIGH);
		assertThat(calculator.determineRiskLevel(90)).isEqualTo(RiskLevel.CRITICAL);
	}
}
