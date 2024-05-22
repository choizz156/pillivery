package com.team33.modulecore.payment.application.refund;

import com.team33.modulecore.payment.kakao.application.refund.RefundContext;

public interface RefundService<T> {

	T refund(RefundContext refundContext);
}
