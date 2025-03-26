package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.application.approve.ApproveFacade;
import com.team33.modulecore.core.payment.application.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.application.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

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
