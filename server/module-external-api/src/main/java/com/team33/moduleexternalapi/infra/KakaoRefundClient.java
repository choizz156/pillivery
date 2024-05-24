package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.ClientSender;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoRefundClient implements PaymentClient<KakaoRefundResponse> {

	private final ClientSender clientSender;

	@Override
	public KakaoRefundResponse send(Map<String, Object> params, String url) {
		return sendRefund(params, url);
	}

	private KakaoRefundResponse sendRefund(Map<String, Object> params, String url) {
		try {
			return clientSender.send(params, url, KakaoRefundResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
