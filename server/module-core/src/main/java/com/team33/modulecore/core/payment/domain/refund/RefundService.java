package com.team33.modulecore.core.payment.domain.refund;

import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;

public interface RefundService {

	void refund(Long orderId, RefundContext refundContext);
}
