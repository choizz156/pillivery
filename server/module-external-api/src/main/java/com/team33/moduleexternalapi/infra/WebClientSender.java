package com.team33.moduleexternalapi.infra;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebClientSender {

	private static final Logger LOGGER = LoggerFactory.getLogger("filelog");

	private final ObjectMapper objectMapper;
	private final WebClient webClient;

	@CircuitBreaker(name = "paymentApiClient", fallbackMethod = "externalApiFallback")
	@TimeLimiter(name = "paymentLookUpClient", fallbackMethod = "externalApiFallback")
	@Retry(name = "paymentLookUpClient")
	public <T> CompletableFuture<T> sendToPostAsync(
		Map<String, Object> params,
		String uri,
		HttpHeaders headers,
		Class<T> responseClass
	)
		throws JsonProcessingException {

		String kakaoRequest = objectMapper.writeValueAsString(params);

		Mono<T> mono = webClient.post()
			.uri(uri)
			.headers(httpHeaders -> httpHeaders.addAll(headers))
			.bodyValue(kakaoRequest)
			.retrieve()
			.bodyToMono(responseClass);

		return mono.doOnSuccess(response -> successLogging(uri))
			.doOnError(error -> failLogging(uri, error))
			.toFuture();
	}

	@CircuitBreaker(name = "paymentApiClient", fallbackMethod = "externalApiFallback")
	@Retry(name = "paymentApiClient")
	public <T> T sendToPostSync(
		Map<String, Object> params,
		String uri,
		HttpHeaders headers,
		Class<T> responseClass
	) throws JsonProcessingException {

		String kakaoRequest = objectMapper.writeValueAsString(params);

		return webClient.post()
			.uri(uri)
			.headers(httpHeaders -> httpHeaders.addAll(headers))
			.bodyValue(kakaoRequest)
			.retrieve()
			.bodyToMono(responseClass)
			.doOnSuccess(response -> successLogging(uri))
			.doOnError(error -> failLogging(uri, error))
			.block();
	}

	private void externalApiFallback(
		Map<String, Object> params,
		String uri,
		HttpHeaders headers,
		Throwable throwable
	) {

		LOGGER.error("Circuit Breaker 작동 (동기 호출) - URI: {}, Params: {}, Headers: {}, 오류: {}",
			uri,
			params,
			headers,
			throwable.getMessage()
		);

		throw new RuntimeException("외부 API 동기 호출 실패 (Circuit Breaker 작동): " + throwable.getMessage(), throwable);
	}

	private void successLogging(String uri) {

		LOGGER.info("외부 API 호출 성공: {}", uri);
	}

	private void failLogging(String uri, Throwable error) {

		LOGGER.error("외부 API 호출 실패: {}, 오류: {}", uri, error.getMessage());
	}

}
