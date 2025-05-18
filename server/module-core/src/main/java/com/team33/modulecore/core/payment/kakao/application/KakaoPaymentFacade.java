package com.team33.modulecore.core.payment.kakao.application;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.domain.request.RequestService;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoPaymentFacade{

	private final SubscriptionApproveService<KakaoApproveResponse, SubscriptionOrder> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final RequestService<KakaoRequestResponse, Long> kakaoRequestService;
	private final RequestService<KakaoRequestResponse, Long> kakaoSubscriptionRequestService;

	public KakaoApproveResponse approveInitially(ApproveRequest approveRequest) {
		return kakaoOneTimeApproveService.approveOneTime(approveRequest);
	}

	public KakaoApproveResponse approveSubscription(SubscriptionOrder subscriptionOrder) {
		return kakaoSubsApproveService.approveSubscription(subscriptionOrder);
	}

	public KakaoApproveResponse approveSid(ApproveRequest approveRequest) {
		return kakaoSubsApproveService.approveInitially(approveRequest);
	}

	public KakaoRequestResponse request(Long orderId) {
		return kakaoRequestService.request(orderId);
	}

	public KakaoRequestResponse requestSubscription(Long subscriptionOrderId) {
		return kakaoSubscriptionRequestService.request(subscriptionOrderId);
	}
}

