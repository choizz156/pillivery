package com.team33.modulecore.core.payment.kakao.application.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SubscriptionFailedEvent {
	private final Long subscriptionOrderId;
}
