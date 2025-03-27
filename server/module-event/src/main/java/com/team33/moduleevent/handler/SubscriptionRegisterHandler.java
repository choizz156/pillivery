package com.team33.moduleevent.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.application.OrderPaymentCodeService1;
import com.team33.modulecore.core.payment.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionRegisterHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final SubscriptionOrderService subscriptionOrderService;
	private final SubscriptionApproveService<KakaoApproveResponse> kakaoSubsApproveService;
	private final OrderPaymentCodeService1 orderPaymentCodeService1;

	@Async
	@EventListener
	public void onEventSet(SubscriptionRegisteredEvent event) {
		List<SubscriptionOrder> subscriptionOrders = subscriptionOrderService.createMulti(event.getOrder());

		subscriptionOrders.forEach(subscriptionOrder -> {
			KakaoApproveResponse response = dispatchSubscriptionApprove(event, subscriptionOrder);
			orderPaymentCodeService1.addSid(subscriptionOrder.getId(), response.getSid());
		});
	}

	private KakaoApproveResponse dispatchSubscriptionApprove(
		SubscriptionRegisteredEvent event,
		SubscriptionOrder subscriptionOrder
	) {
		return kakaoSubsApproveService.approveInitially(
			getSubscriptionRequest(event, subscriptionOrder)
		);
	}

	private KakaoApproveRequest getSubscriptionRequest(
		SubscriptionRegisteredEvent event,
		SubscriptionOrder subscriptionOrder
	) {
		return KakaoApproveRequest.toSubscriptionRequest(
			event.getApproveRequest(), subscriptionOrder.getId()
		);
	}

}
