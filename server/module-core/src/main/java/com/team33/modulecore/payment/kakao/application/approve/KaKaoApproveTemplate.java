package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.util.MultiValueMap;

import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;
import com.team33.moduleexternalapi.domain.PaymentClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KaKaoApproveTemplate {

	private final PaymentClient<KaKaoApproveResponse> kakaoApproveClient;
	private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";


	public abstract MultiValueMap<String, String> getApproveParams(ApproveRequest approveRequest);


	public KaKaoApproveResponse approve(ApproveRequest approveRequest) {
		MultiValueMap<String, String> approveParams = getApproveParams(approveRequest);

		return kakaoApproveClient.send(approveParams, KAKAO_APPROVE_URL);
	}
}
