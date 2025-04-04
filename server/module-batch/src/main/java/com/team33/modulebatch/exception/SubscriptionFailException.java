package com.team33.modulebatch.exception;

import lombok.Getter;

@Getter
public class SubscriptionFailException extends RuntimeException {

	private String message;
	private long subscriptionOrderId;

	public SubscriptionFailException(String message, long subscriptionOrderId) {
		super(message);
		this.message = message;
		this.subscriptionOrderId = subscriptionOrderId;
	}

	public SubscriptionFailException(String message) {
		super(message);
	}

	public SubscriptionFailException(String message, Long subscriptionOrderId, Throwable cause) {
		super(message, cause);
		this.subscriptionOrderId = subscriptionOrderId;
	}
}
