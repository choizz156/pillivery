package com.team33.modulebatch.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.team33.modulebatch.exception.SubscriptionFailException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RestTemplateSender {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final RestTemplate restTemplate;

	@Retry(name = "paymentRetryApiClient")
	@CircuitBreaker(name = "internalPaymentApiClient", fallbackMethod = "internalApiFallback")
	public <T> void sendToPost(String subscriptionOrderId, String url, HttpHeaders headers, Class<T> responseClass) {

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);

		LOGGER.info("내부 API 호출 성공: {}, response: {}", url, exchange.getBody());
	}

	private <T> void internalApiFallback(
		String subscriptionOrderId,
		String url,
		HttpHeaders headers,
		Class<T> responseClass,
		Throwable throwable
	) {

		LOGGER.error(
			" app 서버 통신 에러 -> Circuit Breaker 작동  - URI: {}, fail-susbscriptionOrderId: {}, Headers: {}, 오류: {}",
			url,
			subscriptionOrderId,
			headers,
			throwable.getMessage());

		throw new SubscriptionFailException(throwable.getMessage(), Long.parseLong(subscriptionOrderId));
	}
}
