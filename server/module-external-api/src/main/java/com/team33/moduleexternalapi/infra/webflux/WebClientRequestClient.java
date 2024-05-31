package com.team33.moduleexternalapi.infra.webflux;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApiRequestResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component("webClientRequestClient")
public class WebClientRequestClient implements PaymentClient<KakaoApiRequestResponse> {

	private final WebFluxClient webFluxClient;

	@Override
	public KakaoApiRequestResponse send(Map<String, Object> params, String url)
	{

		CompletableFuture<KakaoApiRequestResponse> futureResponse = sendRequest(params, url);
		try {
			return futureResponse.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private CompletableFuture<KakaoApiRequestResponse> sendRequest(Map<String, Object> params, String url) {
		try {
			return webFluxClient.send(params, url, KakaoApiRequestResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
