package com.team33.modulecore.core.payment.kakao.application.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KakaoSubscriptionCanceledEvent {

	private final String cancelParam;
	private final String cancelUrl;
}
