package com.team33.moduleexternalapi.infra.kakao;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.WebClientSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component("KakaoRequestClient")
public class KakaoRequestClient implements PaymentClient<KakaoApiRequestResponse> {

	private final WebClientSender webClientSender;

	@Override
	public KakaoApiRequestResponse send(Map<String, Object> params, String url) {

		CompletableFuture<KakaoApiRequestResponse> futureResponse = sendRequest(params, url);

		try {
			return futureResponse.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}

	private CompletableFuture<KakaoApiRequestResponse> sendRequest(Map<String, Object> params, String url) {
		try {
			return webClientSender.sendToPost(params, url, KakaoApiRequestResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
