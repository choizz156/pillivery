package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoRequestClient implements PaymentClient<KakaoRequestResponse> {

	private final KakaoClientSender kakaoClientSender;

	@Override
	public KakaoRequestResponse send(Map<String, Object> params, String url) {

		return sendRequest(params, url);
	}

	private KakaoRequestResponse sendRequest(Map<String, Object> params, String url) {
		try {
			return kakaoClientSender.send(params, url, KakaoRequestResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
