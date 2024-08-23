package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RestTemplateSender {

	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;

	public <T> T sendToPost(Map<String, Object> params, String url, HttpHeaders headers, Class<T> responseClass)
		throws JsonProcessingException {

		String request = objectMapper.writeValueAsString(params);
		HttpEntity<String> entity = new HttpEntity<>(request, headers);

		ResponseEntity<T> exchange =
			restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);

		checkSuccess(exchange);

		return exchange.getBody() != null ? exchange.getBody() : (T)exchange.getStatusCode();
	}

	public <T> void sendToPost(String params, String url, HttpHeaders headers, Class<T> responseClass) {

		HttpEntity<String> entity = new HttpEntity<>(params, headers);

		ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);

		checkSuccess(exchange);
	}

	private void checkSuccess(ResponseEntity<?> exchange) {
		if (!exchange.getStatusCode().is2xxSuccessful()) {
			throw new PaymentApiException("api 오류가 발생했습니다.");
		}
	}
}
