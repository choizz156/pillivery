package com.team33.modulebatch.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulebatch.config.RestTemplateConfig;
import com.team33.modulebatch.config.RestTemplateErrorHandler;
import com.team33.modulebatch.exception.SubscriptionFailException;

@ActiveProfiles("test")
@EnableAutoConfiguration
@SpringBootTest(classes = {
	RestTemplateSender.class,
	RestTemplateErrorHandler.class,
	RestTemplateConfig.class,
	ObjectMapper.class,
})
class RestTemplateSenderTest {

	private static final String HOST = "localhost";
	private static final int PORT = 9999;
	private static final String BASE_URL = "http://" + HOST + ":" + PORT;

	private ClientAndServer mockServer;
	private MockServerClient mockServerClient;

	@Autowired
	private RestTemplateSender restTemplateSender;

	@BeforeEach
	void setUp() {

		mockServer = ClientAndServer.startClientAndServer(PORT);
		mockServerClient = new MockServerClient(HOST, PORT);
	}

	@AfterEach
	void tearDown() {

		mockServer.stop();
	}

	@DisplayName("결제 승인 요청을 보낼 수 있다.")
	@Test
	void test1() {
		// given
		String subscriptionOrderId = "100";
		String url = BASE_URL + "/api/payments/approve/subscriptions/" + subscriptionOrderId;

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withPath("/api/payments/approve/subscriptions/" + subscriptionOrderId)
					.withBody(subscriptionOrderId)
			)
			.respond(
				response()
					.withStatusCode(200)
					.withContentType(MediaType.APPLICATION_JSON)
			);

		// when, then
		assertThatNoException()
			.isThrownBy(() -> restTemplateSender.sendToPost(
				subscriptionOrderId,
				url,
				new HttpHeaders(),
				String.class
			));
	}

	@DisplayName("Circuit breaker 테스트")
	@Nested
	class CircuitBreaker {

		private static final String FAIL_URL = BASE_URL + "/fail";
		private static final String SLOW_URL = BASE_URL + "/slow";

		@BeforeEach
		void setUp() {

			mockServer.stop();
			mockServer = ClientAndServer.startClientAndServer(PORT);
			mockServerClient = new MockServerClient(HOST, PORT);
		}

		@AfterEach
		void tearDown() {
			mockServer.stop();
		}

		@DisplayName("circuit breaker 동작 테스트, 10번 실패")
		@Test
		void test1() throws Exception {

			mockServerClient
				.when(
					request()
						.withMethod("POST")
						.withPath("/fail")
				)
				.respond(
					response()
						.withStatusCode(500)
						.withContentType(MediaType.APPLICATION_JSON)
				);

			assertThatThrownBy(() -> {
				for (int i = 0; i < 10; i++) {
					restTemplateSender.sendToPost(
						"1",
						FAIL_URL,
						new HttpHeaders(),
						String.class
					);
				}
			})
				.isInstanceOf(SubscriptionFailException.class);
		}
	}
}