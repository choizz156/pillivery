package com.team33.modulecore.payment.application.approve;

import com.team33.modulecore.order.domain.entity.Order;

public interface SubscriptionApprove<T> {

	T approveSubscription(Order order);

}
