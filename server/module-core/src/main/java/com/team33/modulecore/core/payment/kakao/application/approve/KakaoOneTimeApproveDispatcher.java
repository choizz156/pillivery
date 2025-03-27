package com.team33.modulecore.core.payment.kakao.application.approve;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.payment.domain.approve.OneTimeApprove;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

@Component
public class KakaoOneTimeApproveDispatcher
	extends KaKaoApproveTemplate
	implements OneTimeApprove<KakaoApiApproveResponse, KakaoApproveRequest>
{
	private final ParameterProvider parameterProvider;

	public KakaoOneTimeApproveDispatcher(
		PaymentClient<KakaoApiApproveResponse> kakaoApproveClient,
		ParameterProvider parameterProvider
	) {
		super(kakaoApproveClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoApiApproveResponse approveOneTime(KakaoApproveRequest approveRequest) {
		return super.approve(approveRequest);
	}

	@Override
	public Map<String, Object> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveRequest request = (KakaoApproveRequest) approveRequest;

		return parameterProvider.getOneTimeApproveParams(
			request.getTid() ,
			request.getPgtoken(),
			request.getOrderId()
		);
	}
}
