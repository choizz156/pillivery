package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.payment.dto.ApproveRequest;

public interface OneTimeApprove<T, S extends ApproveRequest> {

	T approveOneTime(S approveRequest);
}
