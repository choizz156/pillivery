package com.team33.modulecore.payment.kakao.application.approve;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.modulecore.payment.application.approve.OneTimeApprove;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;

@Component
public class KakaoOneTimeApprove
	extends KaKaoApproveTemplate
	implements OneTimeApprove<KakaoApproveResponse, KakaoApproveOneTimeRequest>
{
	private final ParameterProvider parameterProvider;

	public KakaoOneTimeApprove(
		PaymentClient<KakaoApproveResponse> kakaoApproveClient,
		ParameterProvider parameterProvider
	) {
		super(kakaoApproveClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoApproveResponse approveOneTime(KakaoApproveOneTimeRequest approveRequest) {
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
