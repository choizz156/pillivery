package com.team33.modulecore.core.payment.kakao.application.events;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PaymentDateUpdatedEvent {

	private final Long SubscriptionOrderId;
	private ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

	public PaymentDateUpdatedEvent(Long SubscriptionOrderId, ZonedDateTime paymentDay) {
		this.SubscriptionOrderId = SubscriptionOrderId;
		this.paymentDay = paymentDay;
	}
}
