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
import org.mockserver.model.Header;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.application.ClientSender;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KakaoRefundClientTest {

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

		KakaoRefundResponse dto = KakaoRefundResponse.builder().build();
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

		RefundParams param = RefundParams.builder()
			.cid("cid")
			.tid("tid")
			.cancelAmount(2200)
			.cancelTaxFreeAmount(0)
			.build();

		//when
		KakaoRefundResponse refundResponse = new KakaoRefundClient(
			new ClientSender(new ObjectMapper(), new TestRestTemplate().getRestTemplate())
		)
			.send(param, CANCEL_URL);

		// Then
		assertThat(refundResponse).isNotNull();
	}

	@DisplayName("환불 요청 오류 시 예외를 던진다.")
	@Test
	void test2() throws Exception {
		// Given
		ObjectMapper objectMapper = new ObjectMapper();

		KakaoRefundResponse dto = KakaoRefundResponse.builder().build();
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

		RefundParams param = RefundParams.builder()
			.cid("cid")
			.tid("ti") //잘못된 tid
			.cancelAmount(2200)
			.cancelTaxFreeAmount(0)
			.build();

		KakaoRefundClient kakaoRefundClient = new KakaoRefundClient(
			new ClientSender(new ObjectMapper(), new TestRestTemplate().getRestTemplate())
		);

		// when //Then
		assertThatThrownBy(() -> kakaoRefundClient.send(param, CANCEL_URL))
			.isInstanceOf(PaymentApiException.class);
	}
}