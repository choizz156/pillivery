package com.team33.modulecore.payment.kakao.application.events;

import java.util.Map;

import lombok.Getter;

@Getter
public class KakaoRefundedEvent {

	private final Map<String, Object> refundParams;
	private final String refundUrl;

	public KakaoRefundedEvent(Map<String, Object> refundParams, String refundUrl) {
		this.refundParams = refundParams;
		this.refundUrl = refundUrl;
	}
}
