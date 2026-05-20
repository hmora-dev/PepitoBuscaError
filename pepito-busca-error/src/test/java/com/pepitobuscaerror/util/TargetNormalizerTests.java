package com.pepitobuscaerror.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TargetNormalizerTests {

	@Test
	void normalizesBareDomainToHttpsRoot() {
		TargetNormalizer.NormalizedTarget target = TargetNormalizer.normalize("Example.COM/some/path", "");

		assertThat(target.name()).isEqualTo("example.com");
		assertThat(target.domain()).isEqualTo("example.com");
		assertThat(target.url()).isEqualTo("https://example.com");
	}

	@Test
	void preservesSupportedSchemeAndPort() {
		TargetNormalizer.NormalizedTarget target = TargetNormalizer.normalize("http://app.example.com:8080/login",
				"Client portal");

		assertThat(target.name()).isEqualTo("Client portal");
		assertThat(target.domain()).isEqualTo("app.example.com");
		assertThat(target.url()).isEqualTo("http://app.example.com:8080");
	}

	@Test
	void rejectsNonPublicTargets() {
		assertThatThrownBy(() -> TargetNormalizer.normalize("localhost:8080", "Local"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("public domain");

		assertThatThrownBy(() -> TargetNormalizer.normalize("https://127.0.0.1", "Local"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("public domain");
	}

	@Test
	void rejectsUnsupportedSchemes() {
		assertThatThrownBy(() -> TargetNormalizer.normalize("ftp://example.com", "FTP"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("HTTP and HTTPS");
	}
}
