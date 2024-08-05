package com.team33.modulecore.core.payment.application.cancel;

import com.team33.modulecore.core.order.domain.entity.Order;

public interface CancelSubscriptionService {

	void cancelSubscription(Order order);
}
