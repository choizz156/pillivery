package com.team33.moduleexternalapi.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiCancelResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.kakao.KakaoCancelClient;
import com.team33.moduleexternalapi.infra.kakao.RestTemplateSender;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KakaoCancelClientTest {

	private ClientAndServer mockServer;

	private static final String HOST = "localhost";
	private static final int PORT = 9090;
	private static final String CANCEL_URL = "http://localhost:9090";
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

	@DisplayName("환불 요청을 보내면 응답 객체를 가져온다.")
	@Test
	void test() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();

		KakaoApiCancelResponse dto = KakaoApiCancelResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"cancel_amount\":2200,\"cancel_tax_free_amount\":0,\"tid\":\"tid\",\"cid\":\"cid\"}"
					)
					.withHeader(Header.header("Authorization", "SECRET_KEY DEV9F204C96DFE6655F42DCE22C77CF4CC4E3BD5"))
					.withHeader(Header.header("Content-type", "application/json")),
				Times.exactly(1)
			)
			.respond(response()
				.withStatusCode(200)
				.withHeader("Content-Type", "application/json")
				.withBody(response)
			);

		Map<String, Object> parameters = getMap("tid");
		String request = objectMapper.writeValueAsString(parameters);

		KakaoCancelClient kakaoCancelClient = new KakaoCancelClient(
			new RestTemplateSender(new ObjectMapper(), new TestRestTemplate().getRestTemplate())
		);

		// Then
		assertThatNoException().isThrownBy(() -> kakaoCancelClient.send(request, CANCEL_URL));
	}

	@DisplayName("환불 요청 오류 시 예외를 던진다.")
	@Test
	void test2() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();

		KakaoApiCancelResponse dto = KakaoApiCancelResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"tid\":\"tid\",\"cid\":\"cid\",\"cancel_amount\":2200,\"cancel_tax_free_amount\":0}"
					)
					.withHeader(Header.header("Authorization", "SECRET_KEY DEV9F204C96DFE6655F42DCE22C77CF4CC4E3BD5"))
					.withHeader(Header.header("Content-type", "application/json")),
				Times.exactly(1)
			)
			.respond(response()
				.withStatusCode(200)
				.withHeader("Content-Type", "application/json")
				.withBody(response)
			);

		Map<String, Object> parameters = getMap("ti");
		String request = objectMapper.writeValueAsString(parameters);
		KakaoCancelClient kakaoCancelClient = new KakaoCancelClient(
			new RestTemplateSender(new ObjectMapper(), new TestRestTemplate().getRestTemplate())
		);

		// when //Then
		assertThatThrownBy(() -> kakaoCancelClient.send(request, CANCEL_URL))
			.isInstanceOf(PaymentApiException.class);
	}

	private Map<String, Object> getMap(String ti) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put("tid", ti);
		parameters.put("cid", "cid");
		parameters.put("cancel_amount", 2200);
		parameters.put("cancel_tax_free_amount", 0);

		return parameters;
	}
}