package com.team33.modulecore.payment.kakao.application.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoRefundHandler {

	private final PaymentClient<KakaoRefundResponse> kakaoRefundClient;

	@EventListener
	public void onRefundEvent(KakaoRefundedEvent refundEvent) {
		KakaoRefundResponse send = kakaoRefundClient.send(refundEvent.getRefundParams(), refundEvent.getRefundUrl());
	}
}
