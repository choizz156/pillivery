package com.team33.modulecore.payment.kakao.application;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.infra.ParameterProvider;

// @RequiredArgsConstructor
@Component
public class SubsFirstApproveService extends KaKaoTemplate {

	private final ParameterProvider parameterProvider;

	public SubsFirstApproveService(RestTemplate restTemplate, ParameterProvider parameterProvider) {
		super(restTemplate);
		this.parameterProvider = parameterProvider;
	}
	// private final RestTemplate restTemplate;
	// private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";


	public KaKaoApproveResponse approveFirstSubscription(KakaoApproveOneTimeRequest approveRequest) {
		// var firstSubscriptionApproveParams =
		// 	parameterProvider.getSubscriptionFirstApproveParams(
		// 		approveRequest.getTid(),
		// 		approveRequest.getPgtoken(),
		// 		approveRequest.getOrderId()
		// 	);
		//
		// return getResponseDtoAboutApprove(firstSubscriptionApproveParams);
		return super.approve(approveRequest);
	}

	@Override
	public MultiValueMap<String, String> getRequestParams(Order order) {
		return null;
	}

	@Override
	public MultiValueMap<String, String> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;
		return parameterProvider.getSubscriptionFirstApproveParams(
			request.getTid(),
			request.getPgtoken(),
			request.getOrderId()
		);
	}

	// private KaKaoApproveResponse getResponseDtoAboutApprove(
	// 	MultiValueMap<String, String> params
	// ) {
	// 	var entity = new HttpEntity<>(params, super.getHeaders());
	//
	// 	return restTemplate.postForObject(
	// 		KAKAO_APPROVE_URL,
	// 		entity,
	// 		KaKaoApproveResponse.class
	// 	);
	// }
}
