package com.team33.modulecore.core.payment.domain.approve;

public interface SubscriptionApprove<T,R> {

	T approveSubscription( R approvalRequest);
}
