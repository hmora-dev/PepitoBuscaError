package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpProbeClientTests {

	private HttpServer server;

	@AfterEach
	void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	void requestsRelativePathsAndCapturesResponseMetadata() throws IOException {
		server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
		server.createContext("/robots.txt", exchange -> {
			byte[] body = "User-agent: *".getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream response = exchange.getResponseBody()) {
				response.write(body);
			}
		});
		server.start();

		int port = server.getAddress().getPort();
		AuditTarget target = new AuditTarget("Local fixture", "127.0.0.1", "http://127.0.0.1:" + port);

		HttpProbeClient.ProbeResult result = new HttpProbeClient().get(target, "/robots.txt", 1000);

		assertThat(result.successful()).isTrue();
		assertThat(result.statusCode()).isEqualTo(200);
		assertThat(result.uri().getPath()).isEqualTo("/robots.txt");
		assertThat(result.headers()).containsKey("content-type");
		assertThat(result.body()).isEqualTo("User-agent: *");
	}
}
