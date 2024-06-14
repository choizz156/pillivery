package com.team33.moduleexternalapi.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SchedulerInternalApiTest {

	private ClientAndServer mockServer;

	private static final String HOST = "localhost";
	private static final int PORT = 9090;
	private static final String REQUEST_URL = "http://localhost:9090";
	private MockServerClient mockServerClient;

	@BeforeAll
	void beforeAll() {
		mockServer = ClientAndServer.startClientAndServer(PORT);
	}

	@AfterAll
	void afterAll() {
		if (mockServerClient != null) {
			mockServerClient.close();
		}

		if (mockServer != null && mockServer.isRunning()) {
			mockServer.stop();
		}
	}

	@DisplayName("스케쥴링 등록 요청을 보낼 수 있다")
	@Test
	void test2() throws Exception {
		// given
		ObjectMapper objectMapper = new ObjectMapper();

		mockServerClient = new MockServerClient(HOST, PORT);
		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"scheduler register event"
					)
					.withHeader(null),
				Times.exactly(1)
			)
			.respond(response()
				.withStatusCode(202)
			);

		RestTemplateSender restTemplateSender = new RestTemplateSender(
			objectMapper,
			new TestRestTemplate().getRestTemplate()
		);

		// when then
		assertThatNoException().isThrownBy(
			() -> restTemplateSender.sendToPost("scheduler register event", "http://localhost:9090/1", null, String.class)
		);
	}

}