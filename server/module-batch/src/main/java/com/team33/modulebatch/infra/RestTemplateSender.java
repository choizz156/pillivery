package com.team33.modulebatch.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.team33.modulebatch.exception.SubscriptionPaymentFailException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RestTemplateSender {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final RestTemplate restTemplate;
	private final DelayedSubscriptionManager delayedSubscriptionManager;

	@Retry(name = "paymentRetryApiClient")
	@CircuitBreaker(name = "internalPaymentApiClient", fallbackMethod = "internalApiFallback")
	public void sendToPost(long subscriptionOrderId, String url, HttpHeaders headers) {

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		if (responseEntity.getStatusCode().is4xxClientError()) {
			String body = responseEntity.getBody();
			delayedSubscriptionManager.add(subscriptionOrderId, body);
		}

		LOGGER.info("내부 API 호출 성공: {}, response: {}", url, responseEntity.getBody());
	}

	private void internalApiFallback(
		long subscriptionOrderId,
		String url,
		HttpHeaders headers,
		Throwable throwable
	) {

		LOGGER.error(
			" app 서버 통신 에러 - URI: {}, fail-susbscriptionOrderId: {}, Headers: {}, 오류: {}",
			url,
			subscriptionOrderId,
			headers,
			throwable.getMessage());

		throw new SubscriptionPaymentFailException(throwable.getMessage(), subscriptionOrderId);
	}
}
