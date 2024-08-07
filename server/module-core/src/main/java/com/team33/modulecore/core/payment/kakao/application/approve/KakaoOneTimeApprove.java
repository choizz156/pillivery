package com.team33.modulecore.core.payment.kakao.application.approve;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.payment.application.approve.OneTimeApprove;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

@Component
public class KakaoOneTimeApprove
	extends KaKaoApproveTemplate
	implements OneTimeApprove<KakaoApiApproveResponse, KakaoApproveOneTimeRequest>
{
	private final ParameterProvider parameterProvider;

	public KakaoOneTimeApprove(
		PaymentClient<KakaoApiApproveResponse> kakaoApproveClient,
		ParameterProvider parameterProvider
	) {
		super(kakaoApproveClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoApiApproveResponse approveOneTime(KakaoApproveOneTimeRequest approveRequest) {
		return super.approve(approveRequest);
	}

	@Override
	public Map<String, Object> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		return parameterProvider.getOneTimeApproveParams(
			request.getTid() ,
			request.getPgtoken(),
			request.getOrderId()
		);
	}
}
