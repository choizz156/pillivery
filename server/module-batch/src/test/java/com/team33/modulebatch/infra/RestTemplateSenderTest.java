package com.team33.modulebatch.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@ActiveProfiles("test")
@EnableAutoConfiguration
@SpringBootTest(classes = {
	RestTemplateSender.class,
	RestTemplateErrorHandler.class,
	RestTemplateConfig.class,
	ObjectMapper.class,
	DelayedSubscriptionManager.class
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

			)
			.respond(
				response()
					.withStatusCode(200)
					.withContentType(MediaType.APPLICATION_JSON)
			);

		// when, then
		assertThatNoException()
			.isThrownBy(() -> restTemplateSender.sendToPost(
				Long.parseLong(subscriptionOrderId),
				url,
				new HttpHeaders()
			));
	}

}