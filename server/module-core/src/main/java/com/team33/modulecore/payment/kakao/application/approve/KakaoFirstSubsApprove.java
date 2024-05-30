package com.team33.modulecore.payment.kakao.application.approve;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.application.PaymentClient;

@Component
public class KakaoFirstSubsApprove extends KaKaoApproveTemplate {
	private final ParameterProvider parameterProvider;

	public KakaoFirstSubsApprove(
		PaymentClient<KakaoApiApproveResponse> kakaoApproveClient,
		ParameterProvider parameterProvider
	) {
		super(kakaoApproveClient);
		this.parameterProvider = parameterProvider;
	}

	public KakaoApiApproveResponse approveFirstSubscription(KakaoApproveOneTimeRequest approveRequest) {
		return super.approve(approveRequest);
	}

	@Override
	public Map<String, Object> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;
		return parameterProvider.getSubscriptionFirstApproveParams(
			request.getTid(),
			request.getPgtoken(),
			request.getOrderId()
		);
	}
}
