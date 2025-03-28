package com.team33.moduleapi.api.payment.mapper;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentData {

	private final String tid;
	private final String sid;
	private final Long targetId;

	@Builder
	public PaymentData(String tid, String sid, Long targetId) {
		this.tid = tid;
		this.sid = sid;
		this.targetId = targetId;
	}
}
