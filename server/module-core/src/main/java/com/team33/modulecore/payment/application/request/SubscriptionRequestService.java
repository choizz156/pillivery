package com.team33.modulecore.payment.application.request;

import com.team33.modulecore.order.domain.entity.Order;

public interface SubscriptionRequestService<T> {

	T requestSubscription(Order order);
}
