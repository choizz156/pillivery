package com.team33.modulecore.payment.application.cancel;

public interface CancelSubscriptionService<T> {

	T cancelSubscription(Long orderId);
}
