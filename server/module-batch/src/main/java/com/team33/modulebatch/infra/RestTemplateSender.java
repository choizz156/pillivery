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

	@CircuitBreaker(name = "internalPaymentApiClient", fallbackMethod = "internalApiFallback")
	@Retry(name = "internalPaymentApiClient")
	public <T> void sendToPost(String params, String url, HttpHeaders headers, Class<T> responseClass) {

		HttpEntity<String> entity = new HttpEntity<>(params, headers);

		ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);

		LOGGER.info("내부 API 호출 성공: {}, subscriptionOrderId: {}, response: {}", url, params, exchange.getBody());
	}

	public <T> void internalApiFallback(String params, String url, HttpHeaders headers, Throwable throwable) {
		
		LOGGER.error(" app 서버 통신 에러 -> Circuit Breaker 작동  - URI: {}, fail-susbscriptionOrderId: {}, Headers: {}, 오류: {}",
				url,
				params,
				headers,
				throwable.getMessage());

		throw new SubscriptionFailException(throwable.getMessage(), Long.parseLong(params));
	}

}
