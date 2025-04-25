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
import com.team33.moduleexternalapi.exception.ExternalApiException;

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

	@TimeLimiter(name = "paymentLookUpClient")
	@Retry(name = "paymentLookUpClient")
	@CircuitBreaker(name = "paymentApiClient", fallbackMethod = "externalApiAsyncFallback")
	public <T> CompletableFuture<T> sendToPostAsync(
			Map<String, Object> params,
			String uri,
			HttpHeaders headers,
			Class<T> responseClass) throws JsonProcessingException {

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

	@Retry(name = "paymentRetryApiClient")
	@CircuitBreaker(name = "paymentApiClient", fallbackMethod = "externalApiSyncFallback")
	public <T> T sendToPostSync(
			Map<String, Object> params,
			String uri,
			HttpHeaders headers,
			Class<T> responseClass) throws JsonProcessingException {

		String kakaoRequest = objectMapper.writeValueAsString(params);

		return webClient.post()
			.uri(uri)
			.headers(httpHeaders -> httpHeaders.addAll(headers))
			.bodyValue(kakaoRequest)
			.retrieve()
			.bodyToMono(responseClass)
			.doOnSuccess(response -> successLogging(uri))
			.doOnError(error -> {
					failLogging(uri, error);
				}
			)
			.block();
	}

	private <T> CompletableFuture<T> externalApiAsyncFallback(
			Map<String, Object> params,
			String uri,
			HttpHeaders headers,
			Class<T> responseClass,
			Throwable throwable) {

		LOGGER.error("api 통신 실패(externalApiAsyncFallback) - URI: {}, responseClass: {}, Params: {}, Headers: {}, message: {}",
				uri,
				responseClass,
				params,
				headers,
				throwable.getMessage());

		throw new ExternalApiException("외부 API 비동기 호출 실패): " + throwable.getMessage(), throwable);
	}

	private <T> T externalApiSyncRetryFallback(
			Map<String, Object> params,
			String uri,
			HttpHeaders headers,
			Class<T> responseClass,
			Throwable throwable) {
		LOGGER.error("Retry  - URI: {}, responseClass: {}, Params: {}, Headers: {}, message: {}",
				uri,
				responseClass,
				params,
				headers,
				throwable.getMessage());
		throw new ExternalApiException("외부 API 동기 호출 실패 (Retry): " + throwable.getMessage(), throwable);
	}

	private <T> T externalApiSyncFallback(
			Map<String, Object> params,
			String uri,
			HttpHeaders headers,
			Class<T> responseClass,
			Throwable throwable) {

		LOGGER.error("api 호출 실패(externalApiSyncFallback) - URI: {}, responseClass: {}, Params: {}, Headers: {}, message: {}",
				uri,
				responseClass,
				params,
				headers,
				throwable.getMessage());
		throw new ExternalApiException("외부 API 동기 호출 실패: " + throwable.getMessage(), throwable);
	}

	private void successLogging(String uri) {

		LOGGER.info("외부 API 호출 성공: {}", uri);
	}

	private void failLogging(String uri, Throwable error) {

		LOGGER.error("외부 API 호출 실패: {}, 오류: {}", uri, error.getMessage());
	}

}
