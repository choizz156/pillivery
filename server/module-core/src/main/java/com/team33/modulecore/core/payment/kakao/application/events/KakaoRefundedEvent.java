package com.team33.modulecore.core.payment.kakao.application.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KakaoRefundedEvent {

	private final String refundParams;
	private final String refundUrl;
}
