package com.team33.modulecore.core.payment.domain.cancel;

public interface SubscriptionCancelService<T> {

	void cancelSubscription(T request);
}
