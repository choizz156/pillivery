package com.team33.moduleexternalapi.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KakaoApproveClientTest {

	private ClientAndServer mockServer;

	private static final String HOST = "localhost";
	private static final int PORT = 9090;
	private static final String AUTHORIZATION = "KakaoAK 15fe252b3ce1d6da44b790e005f40964";
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";
	private static final String APPROVE_URL = "http://localhost:9090";
	private MockServerClient mockServerClient;

	@BeforeAll
	void beforeAll() {
		mockServer = ClientAndServer.startClientAndServer(PORT);
	}

	@AfterAll
	void afterAll() {
		if (mockServer != null && mockServer.isRunning()) {
			mockServer.stop();
		}
	}

	@BeforeEach
	void setUp() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		KaKaoApproveResponse dto = KaKaoApproveResponse.builder().build();
		String response = objectMapper.writeValueAsString(dto);

		mockServerClient = new MockServerClient(HOST, PORT);

		mockServerClient
			.when(
				request()
					.withMethod("POST")
					.withBody("cid=TC0ONETIME&tid=tid&partner_order_id=1&partner_user_id=partner_user_id&pg_token=pgToken")
					.withHeader(Header.header("Authorization", AUTHORIZATION))
					.withHeader(Header.header("Content-type", CONTENT_TYPE)),
				Times.exactly(1)
			)
			.respond(response()
				.withStatusCode(200)
				.withHeader("Content-Type", "application/json")
				.withBody(response)
			);
	}

	@AfterEach
	void tearDown() {
		if (mockServerClient != null) {
			mockServerClient.close();
		}
	}

	@DisplayName("단건 승인 요청을 보내면 응답 객체를 가져온다.")
	@Test
	void test() throws Exception {
		// Given
		PaymentClient<KaKaoApproveResponse> kaKaoApproveClient =
			new KakaoApproveClient(new TestRestTemplate().getRestTemplate(), new ObjectMapper());

		Map<String, String> parameters = new ConcurrentHashMap<>();
		parameters.put("cid", "TC0ONETIME");
		parameters.put("tid", "tid");
		parameters.put("partner_order_id", "1");
		parameters.put("partner_user_id", "partner_user_id");
		parameters.put("pg_token", "pgToken");

		// When
		KaKaoApproveResponse send = kaKaoApproveClient.send(parameters, APPROVE_URL);

		// Then
		assertThat(send).isNotNull();
	}
}
