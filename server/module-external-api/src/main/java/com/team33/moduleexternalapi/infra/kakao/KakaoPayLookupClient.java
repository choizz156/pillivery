package com.team33.moduleexternalapi.infra.kakao;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiPayLookupResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.WebClientSender;

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
		} catch (InterruptedException | ExecutionException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}

	private CompletableFuture<KakaoApiPayLookupResponse> sendRequest(Map<String, Object> params, String url) {
		try {
			return webClientSender.sendToPost(params, url, KakaoApiPayLookupResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
