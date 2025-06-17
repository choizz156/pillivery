package com.team33.modulecore.core.order.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.item.event.ItemSaleCountedEvent;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.events.CartRefreshedEvent;
import com.team33.modulecore.core.payment.domain.cancel.SubscriptionCancelService;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderStatusService {

	private final ApplicationContext applicationContext;
	private final OrderFindHelper orderFindHelper;
	private final SubscriptionOrderService subscriptionOrderService;
	private final SubscriptionCancelService<SubscriptionOrder> kakaoSubsCancelService;


	public void processOneTimeApprove(Long orderId) {

		Order order = orderFindHelper.findOrder(orderId);

		publishSubscriptionOrder(order);

		order.changeOrderStatus(OrderStatus.COMPLETE);

		List<Long> orderedItemsId = getOrderedIds(order);

		applicationContext.publishEvent(new ItemSaleCountedEvent(orderedItemsId));

		publishCartRefreshEvent(order, orderedItemsId);
	}


	public void processCancel(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		order.changeOrderStatus(OrderStatus.REFUND);
	}

	public void processSubscriptionCancel(Long subscriptionOrderId) {

		SubscriptionOrder subscriptionOrder = subscriptionOrderService.findById(subscriptionOrderId);
		subscriptionOrder.changeOrderStatus(OrderStatus.SUBSCRIBE_CANCEL);

		kakaoSubsCancelService.cancelSubscription(subscriptionOrder);
	}

	private void publishCartRefreshEvent(Order order, List<Long> orderedItemsId) {

		if (order.isOrderedAtCart()) {
			applicationContext.publishEvent(new CartRefreshedEvent(order, orderedItemsId));
		}
	}

	private void publishSubscriptionOrder(Order order) {

		if (order.isSubscription()) {
			applicationContext.publishEvent(new SubscriptionRegisteredEvent(order.getId()));
		}
	}

	private List<Long> getOrderedIds(Order order) {

		return order.getOrderItems()
			.stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toUnmodifiableList());
	}
}
