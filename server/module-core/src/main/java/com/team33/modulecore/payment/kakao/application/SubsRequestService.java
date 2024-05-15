package com.team33.modulecore.payment.kakao.application;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.SubscriptionRequest;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;
import com.team33.modulecore.payment.kakao.infra.ParameterProvider;

@Service
public class SubsRequestService
	extends KaKaoTemplate
	implements SubscriptionRequest<KakaoRequestResponse>
{
	private final ParameterProvider parameterProvider;

	public SubsRequestService(RestTemplate restTemplate, ParameterProvider parameterProvider) {
		super(restTemplate);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoRequestResponse requestSubscription(Order order) {
		// MultiValueMap<String, String> subscriptionReqsParams
		// 	= parameterProvider.getSubscriptionReqsParams(order);
		//
		// return getResponseDtoAboutRequest(subscriptionReqsParams);
		return super.request(order);
	}

	@Override
	public MultiValueMap<String, String> getRequestParams(Order order) {
		return parameterProvider.getSubscriptionReqsParams(order);
	}

	@Override
	public MultiValueMap<String, String> getApproveParams(ApproveRequest approveRequest) {
		return null;
	}
	//
	// private KakaoRequestResponse getResponseDtoAboutRequest(
	// 	MultiValueMap<String, String> params
	// ) {
	// 	HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity
	// 		= new HttpEntity<>(params, super.getHeaders());
	//
	// 	return restTemplate.postForObject(READY_URL, kakaoRequestEntity, KakaoRequestResponse.class);
	// }
}
