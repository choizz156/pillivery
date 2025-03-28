package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.payment.dto.ApproveRequest;

public interface SubscriptionApproveService<T, R> {

	T approveInitially(ApproveRequest approveRequest);

	T approveSubscribe(R approveRequest);
}
