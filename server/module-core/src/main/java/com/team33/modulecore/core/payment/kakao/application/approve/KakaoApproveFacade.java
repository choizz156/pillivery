package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.approve.ApproveFacade;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade implements ApproveFacade<KakaoApproveResponse, KakaoApproveRequest> {

	private final SubscriptionApproveService<KakaoApproveResponse, SubscriptionOrder> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final SubscriptionOrderService subscriptionOrderService;

	@Override
	public KakaoApproveResponse approveInitially(KakaoApproveRequest approveRequest) {
		return kakaoOneTimeApproveService.approveOneTime(approveRequest);
	}

	@Override
	public KakaoApproveResponse approveSubscription(Long subscriptionOrderId) {

		SubscriptionOrder subscriptionOrder = subscriptionOrderService.findById(subscriptionOrderId);
		return kakaoSubsApproveService.approveSubscribe(subscriptionOrder);
	}

	public KakaoApproveResponse approveSid(ApproveRequest approveRequest) {

		return kakaoSubsApproveService.approveInitially(approveRequest);
	}

}

