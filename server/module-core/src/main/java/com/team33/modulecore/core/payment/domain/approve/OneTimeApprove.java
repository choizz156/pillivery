package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.payment.dto.ApproveRequest;

public interface OneTimeApprove<T, R extends ApproveRequest> {

	T approveOneTime(R approveRequest);
}
