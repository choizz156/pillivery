package com.team33.modulecore.core.payment.kakao.application.events;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import groovy.transform.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PaymentDateUpdatedEvent {

	private final Long SubscriptionOrderId;
	private final ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

}
