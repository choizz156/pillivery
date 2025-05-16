package com.team33.modulebatch.infra;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.team33.modulebatch.config.RestTemplateConfig;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@EnableAutoConfiguration
@SpringBootTest(
	classes = {
		RestTemplateSender.class,
		RestTemplateConfig.class,
		ObjectMapper.class,
		CircuitBreakerRegistry.class
	},
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class RestTemplateSenderCircuitBreakerTest {

	@RegisterExtension
	static WireMockExtension SERVICE = WireMockExtension.newInstance()
		.options(WireMockConfiguration.wireMockConfig()
			.port(9898))
		.build();

	@Autowired
	private RestTemplateSender restTemplateSender;

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	@DisplayName("circuit breaker 동작 테스트")
	@Test
	void test1() throws Exception {

		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("internalPaymentApiClient");

		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(serverError()));

		for (int i = 0; i < 10; i++) {
			try {
				restTemplateSender.sendToPost(
					"1",
					"http://localhost:9898/api/external",
					new HttpHeaders()
				);
			} catch (Exception ignored) {
			}
		}

		assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

	}

	@DisplayName("circuit breaker half open 테스트")
	@Test
	void testCircuitBreakerHalfOpen() throws Exception {
		// given
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("internalPaymentApiClient");

		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(serverError()));

		// when 
		for (int i = 0; i < 10; i++) {
			try {
				restTemplateSender.sendToPost(
					"1",
					"http://localhost:9898/api/external",
					new HttpHeaders()
				);
			} catch (Exception ignored) {
			}
		}

		// then 
		assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

		// then - waitDurationInOpenState(1s) 후 HALF_OPEN 상태 확인
		await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
		});
	}


	@DisplayName("circuit breaker half open에서 close로 전환 테스트")
	@Test
	void testCircuitBreakerCloseFromHalfOpen() throws Exception {
		// given - HALF_OPEN 상태 만들기
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("internalPaymentApiClient");

		// 먼저 OPEN 상태 만들기
		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(serverError()));

		for (int i = 0; i < 10; i++) {
			try {
				restTemplateSender.sendToPost(
					"1",
					"http://localhost:9898/api/external",
					new HttpHeaders()
				);
			} catch (Exception ignored) {
			}
		}

		// HALF_OPEN 상태로 전환 대기
		await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
		});

		// when - 성공 응답으로 변경하고 요청 보내기
		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(ok()));

		restTemplateSender.sendToPost(
			"1",
			"http://localhost:9898/api/external",
			new HttpHeaders()
		);

		// then - CLOSED 상태 확인
		assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
	}
}