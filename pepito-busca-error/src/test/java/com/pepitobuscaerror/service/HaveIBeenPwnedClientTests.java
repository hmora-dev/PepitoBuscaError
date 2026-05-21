package com.pepitobuscaerror.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pepitobuscaerror.dto.HibpResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class HaveIBeenPwnedClientTests {

	@Test
	void missingApiKeyReturnsDemoDataWithoutCallingProvider() {
		HaveIBeenPwnedClient client = new HaveIBeenPwnedClient(new RestTemplateBuilder(), new ObjectMapper(), "",
				false);

		HibpResult result = client.checkEmail("security@example.com");

		assertThat(result.isDemoMode()).isTrue();
		assertThat(result.getMessage()).contains("HIBP API key not configured");
		assertThat(result.isBreachesFound()).isTrue();
	}
}
