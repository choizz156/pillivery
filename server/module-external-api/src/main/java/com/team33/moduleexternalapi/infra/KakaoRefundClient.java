package com.team33.moduleexternalapi.infra;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.ClientSender;
import com.team33.moduleexternalapi.domain.RefundClient;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoRefundClient implements RefundClient<KakaoRefundResponse> {

	private final ClientSender clientSender;

	@Override
	public KakaoRefundResponse send(RefundParams params, String url) {

		return sendRefund(params, url);
	}

	private KakaoRefundResponse sendRefund(RefundParams params, String url) {
		try {
			return clientSender.send(params, url, KakaoRefundResponse.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
