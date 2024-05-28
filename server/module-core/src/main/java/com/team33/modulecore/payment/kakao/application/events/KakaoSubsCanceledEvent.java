package com.team33.modulecore.payment.kakao.application.events;

import lombok.Getter;

@Getter
public class KakaoSubsCanceledEvent {

	private final String cancelParam;
	private final String cancelUrl;

	public KakaoSubsCanceledEvent(String cancelParam, String cancelUrl) {
		this.cancelParam = cancelParam;
		this.cancelUrl = cancelUrl;
	}
}
