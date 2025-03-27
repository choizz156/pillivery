package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.domain.approve.ApproveFacade;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade1 implements ApproveFacade<KakaoApproveResponse, KakaoApproveRequest> {

	private final SubscriptionApproveService<KakaoApproveResponse> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final OrderFindHelper orderFindHelper;
	private final SubscriptionOrderService subscriptionOrderService;

	@Override
	public KakaoApproveResponse approveInitially(KakaoApproveRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();

		Order order = orderFindHelper.findOrder(orderId);

		if(order.getSubscriptionInfo().isSubscription()){
			KakaoApproveResponse kakaoApproveResponse = kakaoOneTimeApproveService.approveOneTime(approveRequest);
			List<SubscriptionOrder> subscriptionOrders = subscriptionOrderService.createMulti(order);

		}

		if (isSubscriptionAndManyItems(order)) {
			KakaoApproveResponse kakaoApproveResponse = kakaoOneTimeApproveService.approveOneTime(approveRequest);
			//이벤트 발행 -> 비동기로 sid 저장 -> quartz 안써도됨....
			return kakaoApproveResponse;

		} else if (isSubscriptionAndOnlyOneItem(order)) {

			SubscriptionOrder subscriptionOrder = subscriptionOrderService.create(order);
			KakaoApproveRequest subscriptionRequest = KakaoApproveRequest.toSubscriptionRequest(
				approveRequest,
				subscriptionOrder.getId()
			);

			kakaoSubsApproveService.approveInitially(subscriptionRequest);
		}
		//단건결제
		return kakaoOneTimeApproveService.approveOneTime(approveRequest);
	}

	private boolean isSubscriptionAndManyItems(Order order) {
		return order.getSubscriptionInfo().isSubscription() && order.getOrderItems().size() >= 2;
	}

	private boolean isSubscriptionAndOnlyOneItem(Order order) {
		return order.getSubscriptionInfo().isSubscription() && order.getOrderItems().size() == 1;
	}

	@Override
	public KakaoApproveResponse approveSubscription(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		return kakaoSubsApproveService.approveSubscribe(order);
	}
}

