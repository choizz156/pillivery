package com.team33.moduleexternalapi.infra;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApiPayLookupResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.webflux.WebClientSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoPayLookupClient implements PaymentClient<KakaoApiPayLookupResponse> {

	private final WebClientSender webClientSender;

	@Override
	public KakaoApiPayLookupResponse send(Map<String, Object> params, String url) {

		CompletableFuture<KakaoApiPayLookupResponse> futureResponse = sendRequest(params, url);

		try {
			return futureResponse.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private CompletableFuture<KakaoApiPayLookupResponse> sendRequest(Map<String, Object> params, String url) {
		try {
			return webClientSender.send(params, url, KakaoApiPayLookupResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
