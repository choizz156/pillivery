package com.team33.modulecore.payment.kakao.application;

import static com.team33.modulecore.payment.kakao.application.KakaoHeader.*;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KaKaoTemplate {

	private final RestTemplate restTemplate;
	private static final String READY_URL = "https//kapi.kakao.com/v1/payment/ready";
	private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";

	public abstract MultiValueMap<String, String> getRequestParams(Order order);

	public abstract MultiValueMap<String, String> getApproveParams(ApproveRequest approveRequest);

	public KakaoRequestResponse request(Order order) {
		MultiValueMap<String, String> requestParams = getRequestParams(order);
		return getResponseDtoAboutRequest(requestParams);
	}

	public KaKaoApproveResponse approve(ApproveRequest approveRequest) {
		MultiValueMap<String, String> approveParams = getApproveParams(approveRequest);
		return getResponseDtoAboutApprove(approveParams);
	}

	private KakaoRequestResponse getResponseDtoAboutRequest(
		MultiValueMap<String, String> params
	) {
		HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity
			= new HttpEntity<>(params, HTTP_HEADERS.getHeaders());

		return restTemplate.postForObject(READY_URL, kakaoRequestEntity, KakaoRequestResponse.class);
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