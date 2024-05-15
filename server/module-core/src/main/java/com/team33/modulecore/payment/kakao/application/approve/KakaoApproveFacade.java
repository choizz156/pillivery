package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.ApproveFacade;
import com.team33.modulecore.payment.application.NormalApprove;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade implements ApproveFacade<KaKaoApproveResponse, KakaoApproveOneTimeRequest> {

	private final OrderService orderService;
	private final SubsFirstApproveService subsFirstApproveService;
	private final NormalApprove<KaKaoApproveResponse, KakaoApproveOneTimeRequest> normalApprove;

	@Override
	public KaKaoApproveResponse approve(KakaoApproveOneTimeRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();
		Order order = findOrderById(orderId);

		if (order.isSubscription()) {
			KaKaoApproveResponse approve =
				subsFirstApproveService.approveFirstSubscription(approveRequest);

			orderService.addSid(orderId, approve.getSid());
			orderService.changeOrderStatusToSubscribe(orderId);
			// doKakaoScheduling(orderId);
			return approve;
		}

		KaKaoApproveResponse approve = normalApprove.approveOneTime(approveRequest);
		orderService.changeOrderStatusToComplete(orderId);
		return approve;
	}

	private Order findOrderById(Long orderId) {
		return orderService.findOrder(orderId);
	}
}
