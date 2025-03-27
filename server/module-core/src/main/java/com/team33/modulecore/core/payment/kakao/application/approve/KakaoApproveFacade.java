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
	public KakaoApproveResponse approveInitially(KakaoApproveRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();

		Order order = orderFindHelper.findOrder(orderId);

		return isSubscription(order)
			? kakaoSubsApproveService.approveInitially(approveRequest)
			: kakaoOneTimeApproveService.approveOneTime(approveRequest);
	}

	private  boolean isSubscription(Order order) {
		return order.getSubscriptionInfo().isSubscription();
	}

	@Override
	public KakaoApproveResponse approveSubscription(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		return kakaoSubsApproveService.approveSubscribe(order);
	}
}

