package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApiPayLookupResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoPayLookupClient implements PaymentClient<KakaoApiPayLookupResponse> {

	private final KakaoClientSender kakaoClientSender;

	@Override
	public KakaoApiPayLookupResponse send(Map<String, Object> params, String url) {

		return sendRequest(params, url);
	}

	private KakaoApiPayLookupResponse sendRequest(Map<String, Object> params, String url) {
		try {
			return kakaoClientSender.send(params, url, KakaoApiPayLookupResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
