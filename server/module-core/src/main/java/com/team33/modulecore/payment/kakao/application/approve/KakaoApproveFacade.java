package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.approve.ApproveFacade;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;

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
		Order order = orderFindHelper.findOrder(orderId);

		return order.isSubscription()
			? kakaoSubsApproveService.approveFirstTime(orderId, approveRequest)
			: kakaoNormalApproveService.approveOneTime(orderId, approveRequest);
	}

	@Override
	public KaKaoApproveResponse approveSubscription(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		kakaoSubsApproveService.approveSubscribe(order);

		return null;
	}

}
