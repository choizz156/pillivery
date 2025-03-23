package com.team33.modulecore.core.order.domain;

import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Embeddable
public class PaymentId {

	private String sid;

	private String tid;

	public PaymentId(String sid, String tid) {
		this.sid = sid;
		this.tid = tid;
	}

	public static PaymentId addTid(String tid){
		return new PaymentId(null, tid);
	}

	public static PaymentId addSid(String tid, String sid){
		return new PaymentId(sid, tid);
	}
}
