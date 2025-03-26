package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.approve.ApproveFacade;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade implements ApproveFacade<KakaoApproveResponse, KakaoApproveRequest> {

	private final SubscriptionApproveService<KakaoApproveResponse> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final OrderFindHelper orderFindHelper;

	@Override
	public KakaoApproveResponse approveFirst(KakaoApproveRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();

		boolean isSubscription = orderFindHelper.checkSubscription(orderId);

		return isSubscription
			? kakaoSubsApproveService.approveInitial(approveRequest)
			: kakaoOneTimeApproveService.approveOneTime(approveRequest);
	}

	@Override
	public KakaoApproveResponse approveSubscription(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		 return kakaoSubsApproveService.approveSubscribe(order);
	}
}

