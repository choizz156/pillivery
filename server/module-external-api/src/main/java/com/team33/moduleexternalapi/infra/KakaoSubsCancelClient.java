package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.ClientSender;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoSubsCancelResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoSubsCancelClient implements PaymentClient<KakaoSubsCancelResponse> {

	private final ClientSender clientSender;

	@Override
	public KakaoSubsCancelResponse send(Map<String, Object> params, String url) {

		return sendSubscriptionCancel(params, url);
	}

	private KakaoSubsCancelResponse sendSubscriptionCancel(Map<String, Object> params, String url) {
		try {
			return clientSender.send(params, url, KakaoSubsCancelResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
