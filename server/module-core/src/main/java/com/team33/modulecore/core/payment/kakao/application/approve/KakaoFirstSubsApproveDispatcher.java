package com.team33.modulecore.core.payment.kakao.application.approve;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

@Component
public class KakaoFirstSubsApproveDispatcher extends KaKaoApproveTemplate {

	private final ParameterProvider parameterProvider;

	public KakaoFirstSubsApproveDispatcher(
		PaymentClient<KakaoApiApproveResponse> kakaoApproveClient,
		ParameterProvider parameterProvider
	) {
		super(kakaoApproveClient);
		this.parameterProvider = parameterProvider;
	}

	public KakaoApiApproveResponse approveFirstSubscription(KakaoApproveRequest approveRequest) {
		return super.approve(approveRequest);
	}

	@Override
	public Map<String, Object> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveRequest request = (KakaoApproveRequest) approveRequest;
		return parameterProvider.getSubscriptionFirstPaymentApprovalParams(
			request.getTid(),
			request.getPgtoken(),
			request.getOrderId()
		);
	}
}
