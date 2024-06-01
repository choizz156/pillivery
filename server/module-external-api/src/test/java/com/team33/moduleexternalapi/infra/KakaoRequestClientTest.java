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
import com.team33.moduleexternalapi.application.WebClientSender;
import com.team33.moduleexternalapi.dto.KakaoApiRequestResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KakaoRequestClientTest {

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

	@DisplayName("단건 결제 요청을 보낼 수 있다.")
	@Test
	void test() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApiRequestResponse dto = KakaoApiRequestResponse.builder().build();

		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);
		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"partner_order_id\":\"1\",\"quantity\":\"1\",\"total_amount\":\"2000\",\"tax_free_amount\":\"0\",\"approve_url\":\"approveUrl/1\",\"partner_user_id\":\"partner_user_id\",\"item_name\":\"itemName\",\"cancel_url\":\"CANCEL_URI\",\"fail_url\":\"FAIL_URI\",\"cid\":\"TC0ONETIME\"}"
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

		Map<String, Object> parameters = getCommonParam();
		parameters.put("cid", "TC0ONETIME");
		parameters.put("approve_url", "approveUrl" + "/" + 1);

		KakaoRequestClient kakaoRequestClient = new KakaoRequestClient(
			new WebClientSender(objectMapper, WebClient.builder().build())
		);

		//when
		KakaoApiRequestResponse send = kakaoRequestClient.send(parameters, REQUEST_URL);

		// Then
		assertThat(send).isNotNull();
	}

	@DisplayName("정기 결제 요청을 보낼 수 있다.")
	@Test
	void test2() throws Exception {
		// given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApiRequestResponse dto = KakaoApiRequestResponse.builder().build();

		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);
		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"partner_order_id\":\"1\",\"quantity\":\"1\",\"total_amount\":\"2000\",\"tax_free_amount\":\"0\",\"approve_url\":\"approveUrl/1\",\"partner_user_id\":\"partner_user_id\",\"item_name\":\"itemName\",\"cancel_url\":\"CANCEL_URI\",\"fail_url\":\"FAIL_URI\",\"cid\":\"TC0ONETIME\"}"
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

		Map<String, Object> parameters = getCommonParam();
		parameters.put("cid", "TC0ONETIME");
		parameters.put("approve_url", "approveUrl" + "/" + 1);

		KakaoRequestClient kakaoRequestClient = new KakaoRequestClient(
			new WebClientSender(objectMapper, WebClient.builder().build())
		);

		//when
		KakaoApiRequestResponse send = kakaoRequestClient.send(parameters, REQUEST_URL);

		// Then
		assertThat(send).isNotNull();
	}

	@DisplayName("결제 요청 오류 시 예외를 던진다.")
	@Test
	void test3() throws Exception {
		// given
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoApiRequestResponse dto = KakaoApiRequestResponse.builder().build();

		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);
		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody(
						"{\"partner_order_id\":\"1\",\"quantity\":\"1\",\"total_amount\":\"2000\",\"tax_free_amount\":\"0\",\"approve_url\":\"approveUrl/1\",\"partner_user_id\":\"partner_user_id\",\"item_name\":\"itemName\",\"cancel_url\":\"CANCEL_URI\",\"fail_url\":\"FAIL_URI\",\"cid\":\"TC0ONETIME\"}"
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

		Map<String, Object> parameters = getCommonParam();
		parameters.put("cid", "TC0ONETIM"); //잘못된 cid
		parameters.put("approve_url", "approveUrl" + "/" + 1);

		KakaoRequestClient kakaoRequestClient = new KakaoRequestClient(
			new WebClientSender(objectMapper, WebClient.builder().build())
		);

		//when// Then
		assertThatThrownBy(() -> kakaoRequestClient.send(parameters, REQUEST_URL))
			.isInstanceOf(PaymentApiException.class);
	}

	private Map<String, Object> getCommonParam() {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put("partner_order_id", String.valueOf(1L));
		parameters.put("partner_user_id", "partner_user_id");
		parameters.put("item_name", "itemName");
		parameters.put("quantity", String.valueOf(1));
		parameters.put("total_amount", String.valueOf(2000));
		parameters.put("tax_free_amount", "0");
		parameters.put("cancel_url", "CANCEL_URI");
		parameters.put("fail_url", "FAIL_URI");

		return parameters;
	}
}