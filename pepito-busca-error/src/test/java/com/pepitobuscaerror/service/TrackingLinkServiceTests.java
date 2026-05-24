package com.pepitobuscaerror.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrackingLinkServiceTests {

	@Test
	void publicCurrentUrlIsRecommendedBeforeSameWifiUrl() {
		TrackingLinkService.TrackingLinks links = new TrackingLinkService.TrackingLinks(
				"https://tracker.example.com/geolocation/live/token",
				"http://192.168.1.20:8080/geolocation/live/token",
				List.of("http://192.168.1.20:8080/geolocation/live/token"),
				"");

		assertThat(links.getRecommendedUrl()).isEqualTo("https://tracker.example.com/geolocation/live/token");
		assertThat(links.getRecommendedLabel()).isEqualTo("Public client link");
		assertThat(links.hasPublicUrl()).isTrue();
	}

	@Test
	void configuredPublicUrlWinsOverCurrentUrl() {
		TrackingLinkService.TrackingLinks links = new TrackingLinkService.TrackingLinks(
				"http://localhost:8080/geolocation/live/token",
				"http://192.168.1.20:8080/geolocation/live/token",
				List.of("http://192.168.1.20:8080/geolocation/live/token"),
				"https://configured.example.com/geolocation/live/token");

		assertThat(links.getRecommendedUrl()).isEqualTo("https://configured.example.com/geolocation/live/token");
		assertThat(links.getRecommendedLabel()).isEqualTo("Public client link");
		assertThat(links.hasPublicUrl()).isTrue();
	}

	@Test
	void publicUrlIsRequiredWhenOnlyLocalAndSameWifiUrlsExist() {
		TrackingLinkService.TrackingLinks links = new TrackingLinkService.TrackingLinks(
				"http://localhost:8080/geolocation/live/token",
				"http://192.168.1.20:8080/geolocation/live/token",
				List.of("http://192.168.1.20:8080/geolocation/live/token"),
				"");

		assertThat(links.getRecommendedUrl()).isEmpty();
		assertThat(links.getRecommendedLabel()).isEqualTo("Public URL required");
		assertThat(links.hasPublicUrl()).isFalse();
	}
}
