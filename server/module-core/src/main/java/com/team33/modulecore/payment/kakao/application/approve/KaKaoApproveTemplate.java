package com.team33.modulecore.payment.kakao.application.approve;

import java.util.Map;

import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KaKaoApproveTemplate {

	private final PaymentClient<KaKaoApproveResponse> kakaoApproveClient;
	private static final String KAKAO_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";

	public abstract Map<String, String> getApproveParams(ApproveRequest approveRequest);

	public KaKaoApproveResponse approve(ApproveRequest approveRequest) {
		Map<String, String> approveParams = getApproveParams(approveRequest);

		return kakaoApproveClient.send(approveParams, KAKAO_APPROVE_URL);
	}
}
