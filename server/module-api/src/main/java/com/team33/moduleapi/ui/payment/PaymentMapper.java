package com.team33.moduleapi.ui.payment;

import org.springframework.stereotype.Component;

import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;

@Component
public class PaymentMapper {

	public KakaoApproveOneTimeRequest toApproveOneTimeRequest(String tid, String pgToken, Long orderId) {
		return KakaoApproveOneTimeRequest.of(tid,pgToken, orderId);
	}
}
