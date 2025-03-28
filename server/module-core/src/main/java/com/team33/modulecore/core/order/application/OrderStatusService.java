package com.team33.modulecore.core.order.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.events.CartRefreshedEvent;
import com.team33.modulecore.core.order.events.ItemSaleCountedEvent;
import com.team33.modulecore.core.payment.domain.cancel.CancelSubscriptionService;
import com.team33.modulecore.core.payment.domain.refund.RefundService;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class OrderStatusService {

	private final ApplicationContext applicationContext;
	private final OrderFindHelper orderFindHelper;
	private final SubscriptionOrderService subscriptionOrderService;
	private final CancelSubscriptionService<SubscriptionOrder> kakaoSubsCancelService;
	private final RefundService refundService;

	public void processOneTimeStatus(Long orderId) {

		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.COMPLETE);

		List<Long> orderedItemsId = getOrderedIds(order);

		applicationContext.publishEvent(new ItemSaleCountedEvent(orderedItemsId));
		applicationContext.publishEvent(new CartRefreshedEvent(order, orderedItemsId));
	}

	public void processCancel(Long orderId, RefundContext refundContext) {

		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.REFUND);

		refundService.refund(orderId, refundContext);
	}

	public void processSubscriptionCancel(Long subscriptionOrderId) {

		SubscriptionOrder subscriptionOrder = subscriptionOrderService.findById(subscriptionOrderId);
		subscriptionOrder.changeOrderStatus(OrderStatus.SUBSCRIBE_CANCEL);

		kakaoSubsCancelService.cancelSubscription(subscriptionOrder);
	}

	private List<Long> getOrderedIds(Order order) {

		return order.getOrderItems()
			.stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toUnmodifiableList());
	}
}
