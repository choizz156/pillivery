package com.team33.modulecore.payment.kakao.application.approve;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.modulecore.payment.application.approve.NormalApprove;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;

@Component
public class KakaoNormalApprove
	extends KaKaoApproveTemplate
	implements NormalApprove<KaKaoApproveResponse, KakaoApproveOneTimeRequest>
{
	private final ParameterProvider parameterProvider;

	public KakaoNormalApprove(
		PaymentClient<KaKaoApproveResponse> kakaoApproveClient,
		ParameterProvider parameterProvider
	) {
		super(kakaoApproveClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KaKaoApproveResponse approveOneTime(KakaoApproveOneTimeRequest approveRequest) {
		return super.approve(approveRequest);
	}

	@Override
	public Map<String, String> getApproveParams(ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		return parameterProvider.getOneTimeApproveParams(
			request.getTid() ,
			request.getPgtoken(),
			request.getOrderId()
		);
	}
}
