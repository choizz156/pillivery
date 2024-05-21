package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.payment.application.approve.NormalApprove;
import com.team33.modulecore.payment.application.approve.NormalApproveService;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoNormalApproveService implements NormalApproveService<KaKaoApproveResponse> {

	private final NormalApprove<KaKaoApproveResponse, KakaoApproveOneTimeRequest> normalApprove;
	private final OrderService orderService;

	@Override
	public KaKaoApproveResponse approveOneTime( ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		return normalApprove.approveOneTime(request);
	}
}
