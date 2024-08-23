package com.team33.moduleapi.api.payment.mapper;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.api.payment.dto.RefundDto;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveOneTimeRequest;

@Component
public class PaymentMapper {

	public KakaoApproveOneTimeRequest toApproveOneTime(String tid, String pgToken, Long orderId) {
		return KakaoApproveOneTimeRequest.of(tid, pgToken, orderId);
	}

	public RefundContext toRefundContext(RefundDto refundDto) {
		return RefundContext.builder()
			.cancelAmount(refundDto.getCancelAmount())
			.cancelTaxFreeAmount(refundDto.getCancelTaxFreeAmount())
			.build();
	}
}
