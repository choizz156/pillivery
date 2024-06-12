package com.team33.moduleexternalapi.infra.kakao;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.WebClientSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveClient implements PaymentClient<KakaoApiApproveResponse> {

	private final WebClientSender webClientSender;

	@Override
	public KakaoApiApproveResponse send(Map<String, Object> params, String url) {
		CompletableFuture<KakaoApiApproveResponse> completableFuture = sendApprove(params, url);
		try {
			return completableFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}

	private CompletableFuture<KakaoApiApproveResponse> sendApprove(Map<String, Object> params, String url) {
		try {
			return webClientSender.sendToPost(params, url, KakaoApiApproveResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
