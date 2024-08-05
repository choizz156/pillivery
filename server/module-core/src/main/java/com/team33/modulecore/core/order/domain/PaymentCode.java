package com.team33.modulecore.core.order.domain;

import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Embeddable
public class PaymentCode {

	private String sid;

	private String tid;

	public PaymentCode(String sid, String tid) {
		this.sid = sid;
		this.tid = tid;
	}

	public static PaymentCode addTid(String tid){
		return new PaymentCode(null, tid);
	}

	public static PaymentCode addSid(String tid, String sid){
		return new PaymentCode(sid, tid);
	}
}
