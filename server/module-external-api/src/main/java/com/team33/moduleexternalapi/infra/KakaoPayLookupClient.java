package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.ClientSender;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoPayLookupResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoPayLookupClient implements PaymentClient<KakaoPayLookupResponse> {

	private final ClientSender clientSender;

	@Override
	public KakaoPayLookupResponse send(Map<String, Object> params, String url) {

		return sendRequest(params, url);
	}

	private KakaoPayLookupResponse sendRequest(Map<String, Object> params, String url) {
		try {
			return clientSender.send(params, url, KakaoPayLookupResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
