package com.team33.modulecore.core.payment.kakao.application.events;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.Getter;

@Getter
public class PaymentDateUpdatedEvent {

	private final long orderId;
	private final ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

	public PaymentDateUpdatedEvent(long orderId) {
		this.orderId = orderId;
	}
}
