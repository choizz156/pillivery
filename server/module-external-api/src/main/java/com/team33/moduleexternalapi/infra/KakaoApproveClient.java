package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveClient implements PaymentClient<KakaoApproveResponse> {

	private final KakaoClientSender kakaoClientSender;

	@Override
	public KakaoApproveResponse send(Map<String, Object> params, String url) {
		return sendApprove(params, url);
	}

	private KakaoApproveResponse sendApprove(Map<String, Object> params, String url) {
		try {
			return kakaoClientSender.send(params, url, KakaoApproveResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
