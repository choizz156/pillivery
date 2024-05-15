package com.team33.modulecore.payment.kakao.application;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.infra.ParameterProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubsFirstApproveService extends KaKaoHeader {

	private final ParameterProvider parameterProvider;
	private final RestTemplate restTemplate;
	private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";


	public KaKaoApproveResponse approveFirstSubscription(KakaoApproveOneTimeRequest approveRequest) {
		var firstSubscriptionApproveParams =
			parameterProvider.getSubscriptionFirstApproveParams(
				approveRequest.getTid(),
				approveRequest.getPgtoken(),
				approveRequest.getOrderId()
			);

		return getResponseDtoAboutApprove(firstSubscriptionApproveParams);
	}

	private KaKaoApproveResponse getResponseDtoAboutApprove(
		MultiValueMap<String, String> params
	) {
		var entity = new HttpEntity<>(params, super.getHeaders());

		return restTemplate.postForObject(
			KAKAO_APPROVE_URL,
			entity,
			KaKaoApproveResponse.class
		);
	}
}
