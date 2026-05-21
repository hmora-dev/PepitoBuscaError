package com.pepitobuscaerror.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyRiskHistoryTests {

	@Test
	void calculatesAverageAndImprovingTrend() {
		Company company = new Company();
		Analysis older = new Analysis(company);
		older.complete(70, RiskLevel.HIGH);
		Analysis latest = new Analysis(company);
		latest.complete(40, RiskLevel.MEDIUM);
		ReflectionTestUtils.setField(older, "analysisDate", LocalDateTime.now().minusDays(1));
		ReflectionTestUtils.setField(latest, "analysisDate", LocalDateTime.now());

		company.getAnalyses().add(older);
		company.getAnalyses().add(latest);

		assertThat(company.getAverageRiskScore()).isEqualTo(55);
		assertThat(company.getLatestRiskScore()).isEqualTo(40);
		assertThat(company.getPreviousRiskScore()).isEqualTo(70);
		assertThat(company.getRiskTrendLabel()).isEqualTo("Improving");
	}

	@Test
	void reportsNoTrendForSingleAnalysis() {
		Company company = new Company();
		Analysis analysis = new Analysis(company);
		analysis.complete(20, RiskLevel.LOW);
		ReflectionTestUtils.setField(analysis, "analysisDate", LocalDateTime.now());
		company.getAnalyses().add(analysis);

		assertThat(company.getRiskTrendLabel()).isEqualTo("No trend yet");
	}
}
