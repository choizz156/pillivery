package com.team33.modulecore.payment.application.cancel;

import com.team33.modulecore.order.domain.entity.Order;

public interface CancelSubscriptionService {

	void cancelSubscription(Order order);
}
