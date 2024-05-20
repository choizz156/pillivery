package com.team33.modulecore.payment.kakao.application.approve;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;
import com.team33.moduleexternalapi.domain.PaymentClient;

@Component
public class KakaoFirstSubsApprove extends KaKaoApproveTemplate {
	private final ParameterProvider parameterProvider;

	public KakaoFirstSubsApprove(
		PaymentClient<KaKaoApproveResponse> kakaoApproveClient,
		ParameterProvider parameterProvider
	) {
		super(kakaoApproveClient);
		this.parameterProvider = parameterProvider;
	}

	public KaKaoApproveResponse approveFirstSubscription(KakaoApproveOneTimeRequest approveRequest) {
		;
		return super.approve(approveRequest);
	}

	@Override
	public Map<String, String> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest)approveRequest;
		return parameterProvider.getSubscriptionFirstApproveParams(
			request.getTid(),
			request.getPgtoken(),
			request.getOrderId()
		);
	}
}
