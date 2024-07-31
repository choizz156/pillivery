package com.team33.modulequartz.subscription.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.Getter;

@Getter
public class PaymentDateUpdatedEvent {

	private final long orderItemId;
	private final ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

	public PaymentDateUpdatedEvent(long orderItemId) {
		this.orderItemId = orderItemId;
	}
}
