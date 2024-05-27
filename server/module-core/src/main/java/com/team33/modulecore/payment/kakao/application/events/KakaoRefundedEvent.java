package com.team33.modulecore.payment.kakao.application.events;

import lombok.Getter;

@Getter
public class KakaoRefundedEvent {

	private final String refundParams;
	private final String refundUrl;

	public KakaoRefundedEvent(String refundParams, String refundUrl) {
		this.refundParams = refundParams;
		this.refundUrl = refundUrl;
	}

	public String getParams() {
		return refundParams;
	}

	public String getUrl() {
		return refundUrl;
	}
}
