package com.pepitobuscaerror.service;

import com.pepitobuscaerror.dto.PublicLinkInfo;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class PublicLinkServiceTests {

	@Test
	void configuredPublicHttpsBaseUrlBuildsRemoteReadyLink() {
		PublicLinkService service = new PublicLinkService(new MockEnvironment()
				.withProperty("app.public-base-url", "https://tracker.example.com/"));

		PublicLinkInfo linkInfo = service.buildGpsLink("abc-token", localhostRequest());

		assertThat(linkInfo.getUrl()).isEqualTo("https://tracker.example.com/geolocation/live/abc-token");
		assertThat(linkInfo.getStatusLabel()).isEqualTo("Public HTTPS ready");
		assertThat(linkInfo.isConfiguredFromEnvironment()).isTrue();
		assertThat(linkInfo.isDerivedFromRequest()).isFalse();
		assertThat(linkInfo.isPublicHttpsReady()).isTrue();
		assertThat(linkInfo.isUsableFromAnotherNetwork()).isTrue();
		assertThat(linkInfo.getWarningMessage()).isEmpty();
	}

	@Test
	void publicHttpsTunnelRequestIsUsedWhenEnvironmentIsMissing() {
		PublicLinkService service = new PublicLinkService(new MockEnvironment());
		MockHttpServletRequest request = localhostRequest();
		request.addHeader("X-Forwarded-Proto", "https");
		request.addHeader("X-Forwarded-Host", "abc123.ngrok-free.app");

		PublicLinkInfo linkInfo = service.buildGpsLink("abc-token", request);

		assertThat(linkInfo.getUrl()).isEqualTo("https://abc123.ngrok-free.app/geolocation/live/abc-token");
		assertThat(linkInfo.getStatusLabel()).isEqualTo("Public HTTPS ready");
		assertThat(linkInfo.isConfiguredFromEnvironment()).isFalse();
		assertThat(linkInfo.isDerivedFromRequest()).isTrue();
		assertThat(linkInfo.isPublicHttpsReady()).isTrue();
		assertThat(linkInfo.isUsableFromAnotherNetwork()).isTrue();
	}

	@Test
	void publicBaseUrlAliasIsSupportedWhenPreferredPropertyIsMissing() {
		PublicLinkService service = new PublicLinkService(new MockEnvironment()
				.withProperty("public.base-url", "https://alias.example.com"));

		PublicLinkInfo linkInfo = service.buildGpsLink("abc-token", localhostRequest());

		assertThat(linkInfo.getUrl()).isEqualTo("https://alias.example.com/geolocation/live/abc-token");
		assertThat(linkInfo.getStatusLabel()).isEqualTo("Public HTTPS ready");
		assertThat(linkInfo.isConfiguredFromEnvironment()).isTrue();
	}

	@Test
	void localhostRequestStillShowsLocalTestLink() {
		PublicLinkService service = new PublicLinkService(new MockEnvironment());

		PublicLinkInfo linkInfo = service.buildGpsLink("abc-token", localhostRequest());

		assertThat(linkInfo.getUrl()).isEqualTo("http://localhost:8080/geolocation/live/abc-token");
		assertThat(linkInfo.getStatusLabel()).isEqualTo("Local test link");
		assertThat(linkInfo.isLocalOnly()).isTrue();
		assertThat(linkInfo.isSameWifiOnly()).isFalse();
		assertThat(linkInfo.isUsableFromAnotherNetwork()).isFalse();
		assertThat(linkInfo.getWarningMessage()).contains("only works on this computer");
	}

	@Test
	void lanRequestShowsSameWifiOnlyLink() {
		PublicLinkService service = new PublicLinkService(new MockEnvironment());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setScheme("http");
		request.setServerName("192.168.1.50");
		request.setServerPort(8080);

		PublicLinkInfo linkInfo = service.buildGpsLink("abc-token", request);

		assertThat(linkInfo.getUrl()).isEqualTo("http://192.168.1.50:8080/geolocation/live/abc-token");
		assertThat(linkInfo.getStatusLabel()).isEqualTo("Same Wi-Fi only");
		assertThat(linkInfo.isSameWifiOnly()).isTrue();
		assertThat(linkInfo.isRequiresHttpsWarning()).isTrue();
		assertThat(linkInfo.isUsableFromAnotherNetwork()).isFalse();
		assertThat(linkInfo.getWarningMessage()).contains("same Wi-Fi");
	}

	@Test
	void publicHttpConfiguredBaseUrlWarnsThatHttpsIsRequired() {
		PublicLinkService service = new PublicLinkService(new MockEnvironment()
				.withProperty("app.public-base-url", "http://tracker.example.com"));

		PublicLinkInfo linkInfo = service.buildGpsLink("abc-token", localhostRequest());

		assertThat(linkInfo.getUrl()).isEqualTo("http://tracker.example.com/geolocation/live/abc-token");
		assertThat(linkInfo.getStatusLabel()).isEqualTo("HTTPS required");
		assertThat(linkInfo.isRequiresHttpsWarning()).isTrue();
		assertThat(linkInfo.isPublicHttpsReady()).isFalse();
		assertThat(linkInfo.isUsableFromAnotherNetwork()).isFalse();
		assertThat(linkInfo.getWarningMessage()).contains("usually requires HTTPS");
	}

	@Test
	void normalizeBaseUrlRemovesTrailingSlash() {
		PublicLinkService service = new PublicLinkService(new MockEnvironment());

		assertThat(service.normalizeBaseUrl("https://abc123.trycloudflare.com/"))
				.isEqualTo("https://abc123.trycloudflare.com");
	}

	private MockHttpServletRequest localhostRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setScheme("http");
		request.setServerName("localhost");
		request.setServerPort(8080);
		return request;
	}
}
