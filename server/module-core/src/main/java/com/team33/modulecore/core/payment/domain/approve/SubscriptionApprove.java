package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.order.domain.entity.Order;

public interface SubscriptionApprove<T> {

	T approveSubscription(Order order);
}
