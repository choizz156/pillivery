package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.approve.ApproveFacade;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade implements ApproveFacade<KaKaoApproveResponse, KakaoApproveOneTimeRequest> {

	private final KakaoSubsApproveService kakaoSubsApproveService;
	private final KakaoNormalApproveService kakaoNormalApproveService;
	private final OrderFindHelper orderFindHelper;

	@Override
	public KaKaoApproveResponse approveFirstTime(KakaoApproveOneTimeRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();
		boolean isSubscription = orderFindHelper.checkSubscription(orderId);

		return isSubscription
			? kakaoSubsApproveService.approveFirstTime(approveRequest)
			: kakaoNormalApproveService.approveOneTime(approveRequest);
	}

	@Override
	public KaKaoApproveResponse approveSubscription(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		return kakaoSubsApproveService.approveSubscribe(order);
	}
}
