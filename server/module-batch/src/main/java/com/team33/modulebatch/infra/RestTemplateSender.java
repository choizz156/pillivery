package com.team33.modulebatch.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RestTemplateSender {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final RestTemplate restTemplate;

	public <T> void sendToPost(String params, String url, HttpHeaders headers, Class<T> responseClass) {

		HttpEntity<String> entity = new HttpEntity<>(params, headers);

		ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);

		//TODO: 이거 어떻게 처리할지 고민.

	}

}
