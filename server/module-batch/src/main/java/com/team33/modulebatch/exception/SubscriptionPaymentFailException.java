package com.team33.modulebatch.exception;

import lombok.Getter;

@Getter
public class SubscriptionPaymentFailException extends RuntimeException {

	private String message;
	private long subscriptionOrderId;

	public SubscriptionPaymentFailException(String message, long subscriptionOrderId) {
		super(message);
		this.message = message;
		this.subscriptionOrderId = subscriptionOrderId;
	}

	public SubscriptionPaymentFailException(String message) {
		super(message);
	}

	public SubscriptionPaymentFailException(String message, Long subscriptionOrderId, Throwable cause) {
		super(message, cause);
		this.subscriptionOrderId = subscriptionOrderId;
	}
}
