package com.team33.modulecore.payment.application.approve;

import com.team33.modulecore.payment.dto.ApproveRequest;

public interface NormalApprove<T, S extends ApproveRequest> {

	T approveOneTime(S approveRequest);

}
