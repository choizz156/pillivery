package com.team33.modulebatch.exception;

import lombok.Getter;

@Getter
public class SubscriptionFailException extends RuntimeException {

	private final String message;
	private long subscriptionOrderId;

	public SubscriptionFailException(String message, long subscriptionOrderId) {
		super(message);
		this.message = message;
		this.subscriptionOrderId = subscriptionOrderId;
	}
}
