package com.team33.moduleexternalapi.infra;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.*;
import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.team33.moduleexternalapi.config.WebClientConfig;
import com.team33.moduleexternalapi.config.WebClientErrorHandler;
import com.team33.moduleexternalapi.exception.ExternalApiException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;

@ActiveProfiles("test")
@EnableAutoConfiguration
@SpringBootTest(classes = {
	WebClientSender.class,
	WebClientErrorHandler.class,
	WebClientConfig.class,
	ObjectMapper.class,
	RetryRegistry.class,
	CircuitBreakerRegistry.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebClientSenderTest {

	@RegisterExtension
	static WireMockExtension SERVICE = WireMockExtension.newInstance()
		.options(wireMockConfig().port(9090))
		.build();

	@Autowired
	private WebClientSender webClientSender;

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	@DisplayName("Circuit Breaker - OPEN 상태 전환 테스트")
	@Test
	void test1() {
		//given
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("paymentApiClient");

		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(serverError()));

		assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

		//when
		for (int i = 0; i < 10; i++) {
			try {
				webClientSender.sendToPostSync(
					Map.of("k", "v"),
					"http://localhost:9090/api/external",
					new HttpHeaders(),
					String.class
				);
			} catch (Exception ignored) {
			}
		}

		//then
		assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
	}

	@DisplayName("Retry - 재시도 후 실패 테스트")
	@Test
	void test2() throws Exception {
		// given
		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(serverError()));

		// when then
		assertThatThrownBy(() -> webClientSender.executeWithRetrySync(
			Map.of("key", "value"),
			"http://localhost:9090/api/external",
			new HttpHeaders(),
			String.class
		))
			.isInstanceOf(ExternalApiException.class);

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			SERVICE.verify(5, postRequestedFor(urlEqualTo("/api/external")));
		});
	}

	@DisplayName("Time Limiter 타임아웃 테스트")
	@Test
	void test3() throws Exception {
		//given
		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(ResponseDefinitionBuilder.responseDefinition().withFixedDelay(2000)));

		// when
		CompletableFuture<String> future = webClientSender.executeWithTimeoutAsync(
			Map.of("k", "v"),
			"http://localhost:9090/api/external",
			new HttpHeaders(),
			String.class
		);

		// then
		await().atMost(2, SECONDS).untilAsserted(() -> {
			assertThat(future).isCompletedExceptionally();
			try {
				future.join();
				fail("예외가 발생해야 합니다");
			} catch (Exception e) {
				assertThat(e).hasCauseInstanceOf(ExternalApiException.class);
			}
		});
	}

	@DisplayName("Circuit Breaker - HALF_OPEN 상태 전환 테스트")
	@Test
	void test4() {
		//given
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("paymentApiClient");

		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(serverError()));

		//when
		for (int i = 0; i < 10; i++) {
			try {
				webClientSender.sendToPostSync(
					Map.of("k", "v"),
					"http://localhost:9090/api/external",
					new HttpHeaders(),
					String.class
				);
			} catch (Exception ignored) {
			}
		}

		//then
		assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

		await().atMost(3, SECONDS).untilAsserted(() -> {
			assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
		});
	}

	@DisplayName("Circuit Breaker - HALF_OPEN에서 CLOSED 전환 테스트")
	@Test
	void test5() throws Exception {
		//given
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("paymentApiClient");

		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(serverError()));
		for (int i = 0; i < 10; i++) {
			try {
				webClientSender.sendToPostSync(
					Map.of("k", "v"),
					"http://localhost:9090/api/external",
					new HttpHeaders(),
					String.class
				);
			} catch (Exception ignored) {
			}
		}

		await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
		});

		SERVICE.stubFor(post(urlEqualTo("/api/external"))
			.willReturn(ok()));

		//when
		await().atMost(2, SECONDS).untilAsserted(() -> {
			assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);


			webClientSender.sendToPostSync(
				Map.of("k", "v"),
				"http://localhost:9090/api/external",
				new HttpHeaders(),
				String.class
			);

			//then
			assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
		});
	}
}