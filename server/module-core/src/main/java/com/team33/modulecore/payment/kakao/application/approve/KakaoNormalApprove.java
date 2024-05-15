package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.payment.application.approve.NormalApprove;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;

@Component
public class KakaoNormalApprove
	extends KaKaoApproveTemplate
	implements NormalApprove<KaKaoApproveResponse, KakaoApproveOneTimeRequest>
{
	private final ParameterProvider parameterProvider;
	// private final RestTemplate restTemplate;
	// private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";

	public KakaoNormalApprove(RestTemplate restTemplate, ParameterProvider parameterProvider) {
		super(restTemplate);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KaKaoApproveResponse approveOneTime(KakaoApproveOneTimeRequest approveRequest) {
		// var oneTimeApproveParams = parameterProvider.getOneTimeApproveParams(
		// 	approveRequest.getTid(),
		// 	approveRequest.getPgtoken(),
		// 	approveRequest.getOrderId()
		// );
		//
		// return getResponseDtoAboutApprove(oneTimeApproveParams);
		return super.approve(approveRequest);
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

	@Override
	public MultiValueMap<String, String> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		return parameterProvider.getOneTimeApproveParams(
			request.getTid(),
			request.getPgtoken(),
			request.getOrderId()
		);
	}
}
