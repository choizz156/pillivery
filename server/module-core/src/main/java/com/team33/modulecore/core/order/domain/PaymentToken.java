package com.team33.modulecore.core.order.domain;

import javax.persistence.Embeddable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Embeddable
public class PaymentToken {

	private static final String NOT_REGISTER_SUBSCRIPTION_YET = "not_register_subscription_yet";
    
    private String sid = NOT_REGISTER_SUBSCRIPTION_YET;

	private String tid;

	@Builder
	public PaymentToken(String sid, String tid) {
		this.sid = sid;
		this.tid = tid;
	}

}
