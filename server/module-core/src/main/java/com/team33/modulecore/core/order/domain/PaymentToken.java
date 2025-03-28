package com.team33.modulecore.core.order.domain;

import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Embeddable
public class PaymentToken {

	private String sid;

	private String tid;

	public PaymentToken(String sid, String tid) {
		this.sid = sid;
		this.tid = tid;
	}

}
