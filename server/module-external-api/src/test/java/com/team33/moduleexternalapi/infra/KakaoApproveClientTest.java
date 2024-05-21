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
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KakaoApproveClientTest {

	private ClientAndServer mockServer;

	private static final String HOST = "localhost";
	private static final int PORT = 9090;
	private static final String APPROVE_URL = "http://localhost:9090";
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

	@DisplayName("단건 승인 요청을 보내면 응답 객체를 가져온다.")
	@Test
	void test() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApproveResponse dto = KakaoApproveResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"partner_order_id\":\"1\",\"pg_token\":\"pgToken\",\"partner_user_id\":\"partner_user_id\",\"tid\":\"tid\",\"cid\":\"TC0ONETIME\"}"
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

		KakaoApproveResponse send = toSendNormal();

		// Then
		assertThat(send).isNotNull();
	}

	private  KakaoApproveResponse toSendNormal() {
		PaymentClient<KakaoApproveResponse> kaKaoApproveClient =
			new KakaoApproveClient(new TestRestTemplate().getRestTemplate(), new ObjectMapper());

		Map<String, String> parameters = new ConcurrentHashMap<>();
		parameters.put("cid", "TC0ONETIME");
		parameters.put("tid", "tid");
		parameters.put("partner_order_id", "1");
		parameters.put("partner_user_id", "partner_user_id");
		parameters.put("pg_token", "pgToken");

		// When
		KakaoApproveResponse send = kaKaoApproveClient.send(parameters, APPROVE_URL);
		return send;
	}

	@DisplayName("최초 정기 결제 승인 요청 시 응답 객체를 가져온다.")
	@Test
	void test4() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();

		KakaoApproveResponse dto = KakaoApproveResponse.builder().build();

		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"partner_order_id\":\"partner_order_id\",\"pg_token\":\"pg_token\",\"partner_user_id\":\"partner_user_id\",\"tid\":\"tid\",\"cid\":\"sub_cid\"}"
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

		//when
		KakaoApproveResponse send = toSendSubsFirst();

		// Then
		assertThat(send).isNotNull();
	}

	private  KakaoApproveResponse toSendSubsFirst() {
		PaymentClient<KakaoApproveResponse> kaKaoApproveClient =
			new KakaoApproveClient(new TestRestTemplate().getRestTemplate(), new ObjectMapper());

		Map<String, String> parameters = new ConcurrentHashMap<>();
		parameters.put("tid", "tid");
		parameters.put("partner_order_id", "partner_order_id");
		parameters.put("partner_user_id", "partner_user_id");
		parameters.put("pg_token", "pg_token");
		parameters.put("cid", "sub_cid");

		// When
		return kaKaoApproveClient.send(parameters, APPROVE_URL);
	}


	@DisplayName("정기 결제 승인 요청 시 응답객체를 받는다.")
	@Test
	void test6() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApproveResponse dto = KakaoApproveResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"partner_order_id\":\"partner_order_id\",\"pg_token\":\"pg_token\",\"partner_user_id\":\"partner_user_id\",\"tid\":\"tid\",\"cid\":\"sub_cid\",\"sid\":\"sid\"}"
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

		//when
		KakaoApproveResponse sendSubs = toSendSubs();

		// Then
		assertThat(sendSubs).isNotNull();

	}

	private  KakaoApproveResponse toSendSubs() {
		PaymentClient<KakaoApproveResponse> kaKaoApproveClient =
			new KakaoApproveClient(new TestRestTemplate().getRestTemplate(), new ObjectMapper());

		Map<String, String> parameters = new ConcurrentHashMap<>();
		parameters.put("tid", "tid");
		parameters.put("partner_order_id", "partner_order_id");
		parameters.put("partner_user_id", "partner_user_id");
		parameters.put("pg_token", "pg_token");
		parameters.put("cid", "sub_cid");
		parameters.put("sid","sid");


		return kaKaoApproveClient.send(parameters, APPROVE_URL);
	}

	@DisplayName("결제 승인 요청 실패시  예외를 던진다.")
	@Test
	void test7() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApproveResponse dto = KakaoApproveResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"partner_order_id\":\"partner_order_id\",\"pg_token\":\"pg_token\",\"partner_user_id\":\"partner_user_id\",\"tid\":\"tid\",\"cid\":\"sub_cid\",\"sid\":\"sid\"}"
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

		PaymentClient<KakaoApproveResponse> kaKaoApproveClient =
			new KakaoApproveClient(new TestRestTemplate().getRestTemplate(), new ObjectMapper());

		Map<String, String> parameters = new ConcurrentHashMap<>();
		parameters.put("tid", "tid");
		parameters.put("partner_order_id", "partner_order_id");
		parameters.put("partner_user_id", "partner_user_id");
		parameters.put("pg_token", "pg_token");
		parameters.put("cid", "sub_cid");
		parameters.put("sid",""); //잘못된 데이터

		//when // Then
		assertThatThrownBy(() -> kaKaoApproveClient.send(parameters, APPROVE_URL))
			.isInstanceOf(PaymentApiException.class);

	}
}


