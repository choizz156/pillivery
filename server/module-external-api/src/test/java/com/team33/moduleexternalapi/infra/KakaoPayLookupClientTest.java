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
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiPayLookupResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.kakao.KakaoApproveClient;
import com.team33.moduleexternalapi.infra.kakao.KakaoPayLookupClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KakaoPayLookupClientTest {

	private ClientAndServer mockServer;

	private static final String HOST = "localhost";
	private static final int PORT = 9090;
	private static final String SEARCH_URL = "http://localhost:9090";
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

	@DisplayName("주문 조회 요청을 보내면 응답 객체를 가져온다.")
	@Test
	void test() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApiPayLookupResponse dto = KakaoApiPayLookupResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"tid\":\"tid\",\"cid\":\"TC0ONETIME\"}"
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

		KakaoApiPayLookupResponse send = toSendNormal();

		// Then
		assertThat(send).isNotNull();
	}

	private KakaoApiPayLookupResponse toSendNormal() {

		KakaoPayLookupClient kakaoPayLookupClient =
			new KakaoPayLookupClient(
				new WebClientSender(new ObjectMapper(), WebClient.builder().build())
			);

		Map<String, Object> parameters = new ConcurrentHashMap<>();
		parameters.put("cid", "TC0ONETIME");
		parameters.put("tid", "tid");

		// When
		return kakaoPayLookupClient.send(parameters, SEARCH_URL);
	}

	@DisplayName("주문 조회 요청 실패시  예외를 던진다.")
	@Test
	void test7() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApiPayLookupResponse dto = KakaoApiPayLookupResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"tid\":\"tid\",\"cid\":\"TC0ONETIME\"}"
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

		PaymentClient<KakaoApiApproveResponse> kaKaoApproveClient =
			new KakaoApproveClient(new WebClientSender(new ObjectMapper(),  WebClient.builder().build()));

		Map<String, Object> parameters = new ConcurrentHashMap<>();
		parameters.put("tid", "ti"); //잘못된 tid
		parameters.put("cid", "TC0ONETIME");


		//when // Then
		assertThatThrownBy(() -> kaKaoApproveClient.send(parameters, SEARCH_URL))
			.isInstanceOf(PaymentApiException.class);

	}
}


