package com.team33.moduleapi.ui.payment.mapper;

import org.springframework.stereotype.Component;

import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;

@Component
public class PaymentMapper {

	public KakaoApproveOneTimeRequest toApproveOneTime(String tid, String pgToken, Long orderId) {
		return KakaoApproveOneTimeRequest.of(tid, pgToken, orderId);
	}
}
