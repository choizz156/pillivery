package com.team33.modulecore.core.payment.domain.cancel;

public interface CancelSubscriptionService<T> {

	void cancelSubscription(T request);
}
