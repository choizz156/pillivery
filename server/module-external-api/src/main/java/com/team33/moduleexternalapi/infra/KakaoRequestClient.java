package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApiRequestResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoRequestClient implements PaymentClient<KakaoApiRequestResponse> {

	private final KakaoClientSender kakaoClientSender;

	@Override
	public KakaoApiRequestResponse send(Map<String, Object> params, String url) {

		return sendRequest(params, url);
	}

	private KakaoApiRequestResponse sendRequest(Map<String, Object> params, String url) {
		try {
			return kakaoClientSender.send(params, url, KakaoApiRequestResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
