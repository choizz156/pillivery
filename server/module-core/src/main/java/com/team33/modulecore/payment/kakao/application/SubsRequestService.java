package com.team33.modulecore.payment.kakao.application;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.SubscriptionRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;
import com.team33.modulecore.payment.kakao.infra.ParameterProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubsRequestService
	extends KaKaoHeader
	implements SubscriptionRequest<KakaoRequestResponse>
{
	private final ParameterProvider parameterProvider;
	private final RestTemplate restTemplate;
	private static final String READY_URL = "https//kapi.kakao.com/v1/payment/ready";

	@Override
	public KakaoRequestResponse requestSubscription(Order order) {
		MultiValueMap<String, String> subscriptionReqsParams
			= parameterProvider.getSubscriptionReqsParams(order);

		return getResponseDtoAboutRequest(subscriptionReqsParams);
	}

	private KakaoRequestResponse getResponseDtoAboutRequest(
		MultiValueMap<String, String> params
	) {
		HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity
			= new HttpEntity<>(params, super.getHeaders());

		return restTemplate.postForObject(READY_URL, kakaoRequestEntity, KakaoRequestResponse.class);
	}
}
