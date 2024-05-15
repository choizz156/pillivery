package com.team33.modulecore.payment.kakao.application.approve;

import static com.team33.modulecore.payment.kakao.application.KakaoHeader.*;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KaKaoApproveTemplate {

	private final RestTemplate restTemplate;
	private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";

	public abstract MultiValueMap<String, String> getApproveParams(ApproveRequest approveRequest);


	public KaKaoApproveResponse approve(ApproveRequest approveRequest) {
		MultiValueMap<String, String> approveParams = getApproveParams(approveRequest);
		return getResponseDtoAboutApprove(approveParams);
	}

	private KaKaoApproveResponse getResponseDtoAboutApprove(
		MultiValueMap<String, String> params
	) {
		var entity = new HttpEntity<>(params, HTTP_HEADERS.getHeaders());

		return restTemplate.postForObject(
			KAKAO_APPROVE_URL,
			entity,
			KaKaoApproveResponse.class
		);
	}
}
