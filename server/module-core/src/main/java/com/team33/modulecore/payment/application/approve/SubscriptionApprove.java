package com.team33.modulecore.payment.application.approve;

import com.team33.modulecore.payment.dto.ApproveRequest;

public interface SubscriptionApprove<T, S extends ApproveRequest> {

	T approveSubscription(S approveRequest);

}
