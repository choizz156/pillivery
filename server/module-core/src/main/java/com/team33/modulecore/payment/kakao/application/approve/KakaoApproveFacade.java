package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.approve.ApproveFacade;
import com.team33.modulecore.payment.application.approve.OneTimeApproveService;
import com.team33.modulecore.payment.application.approve.SubscriptionApproveService;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade implements ApproveFacade<KakaoApproveResponse, KakaoApproveOneTimeRequest> {

	private final SubscriptionApproveService<KakaoApproveResponse> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final OrderFindHelper orderFindHelper;

	@Override
	public KakaoApproveResponse approveFirst(KakaoApproveOneTimeRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();

		boolean isSubscription = orderFindHelper.checkSubscription(orderId);

		return isSubscription
			? kakaoSubsApproveService.approveFirstTime(approveRequest)
			: kakaoOneTimeApproveService.approveOneTime(approveRequest);
	}

	@Override
	public KakaoApproveResponse approveSubscription(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		return kakaoSubsApproveService.approveSubscribe(order);
	}
}
