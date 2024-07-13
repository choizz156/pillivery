package com.team33.modulecore.payment.kakao.application.refund;

import lombok.Getter;

@Getter
public class SchedulerCanceledEvent {

	private final long orderId;

	public SchedulerCanceledEvent(long orderId) {
		this.orderId = orderId;
	}
}
