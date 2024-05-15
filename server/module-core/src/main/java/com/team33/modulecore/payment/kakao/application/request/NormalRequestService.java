package com.team33.modulecore.payment.kakao.application.request;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.NormalRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;
import com.team33.modulecore.payment.kakao.infra.ParameterProvider;


@Service
public class NormalRequestService
	extends RequestTemplate
	implements NormalRequest<KakaoRequestResponse> {

	private final ParameterProvider parameterProvider;

	public NormalRequestService(ParameterProvider parameterProvider, RestTemplate restTemplate) {
		super(restTemplate);
		this.parameterProvider = parameterProvider;
	}
	// private final RestTemplate restTemplate;
	// private static final String READY_URL = "https//kapi.kakao.com/v1/payment/ready";

	@Override
	public KakaoRequestResponse requestOneTime(Order order) {
		// MultiValueMap<String, String> oneTimeReqsParams
		// 	= parameterProvider.getOneTimeReqsParams(order);
		//
		// return getResponseDtoAboutRequest(oneTimeReqsParams);
		return super.request(order);
	}

	@Override
	public MultiValueMap<String, String> getRequestParams(Order order) {
		return parameterProvider.getSubscriptionReqsParams(order);
	}

	// private KakaoRequestResponse getResponseDtoAboutRequest(
	// 	MultiValueMap<String, String> params
	// ) {
	// 	HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity
	// 		= new HttpEntity<>(params, super.getHeaders());
	//
	// 	return restTemplate.postForObject(READY_URL, kakaoRequestEntity, KakaoRequestResponse.class);
	// }

}
