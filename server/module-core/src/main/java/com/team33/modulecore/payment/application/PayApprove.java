package com.team33.modulecore.payment.application;

import com.team33.modulecore.order.domain.Order;

public interface PayApprove<T> {

	T approveOneTime(String tid, String pgToken, Long orderId);

	T approveFirstSubscription(String tid, String pgToken, Long orderId);

	T approveSubscription(String sid, Order order);

	// T approveOneTime(ApproveRequest approveRequest);
	//
	// T approveFirstSubscription(ApproveRequest approveRequest);
	//
	// T approveSubscription(ApproveRequest approveRequest);

}
