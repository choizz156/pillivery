package com.team33.modulecore.payment.kakao.application.approve;

import java.util.Map;

import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KaKaoApproveTemplate {

	private static final String KAKAO_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";

	private final PaymentClient<KakaoApproveResponse> kakaoApproveClient;


	public abstract Map<String, Object> getApproveParams(ApproveRequest approveRequest);

	public KakaoApproveResponse approve(ApproveRequest approveRequest) {
		Map<String, Object> approveParams = getApproveParams(approveRequest);

		return kakaoApproveClient.send(approveParams, KAKAO_APPROVE_URL);
	}
}
