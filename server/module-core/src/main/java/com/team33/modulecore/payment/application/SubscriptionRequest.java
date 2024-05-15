package com.team33.modulecore.payment.application;

import com.team33.modulecore.order.domain.entity.Order;

public interface SubscriptionRequest<T> {

	T requestSubscription(Order order);

}
