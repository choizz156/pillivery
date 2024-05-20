package com.team33.moduleapi.ui.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentData {

	private String tid;
	private String sid;
	private Long orderId;

	@Builder
	public PaymentData(String tid, String sid, Long orderId) {
		this.tid = tid;
		this.sid = sid;
		this.orderId = orderId;
	}
}
