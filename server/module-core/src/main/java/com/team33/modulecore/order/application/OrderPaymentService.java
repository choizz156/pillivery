package com.team33.modulecore.order.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.cart.application.NormalCartService;
import com.team33.modulecore.cart.application.SubscriptionCartService;
import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.entity.Order;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderPaymentService {

	private final OrderFindHelper orderFindHelper;
	private final ItemCommandService itemCommandService;
	private final SubscriptionCartService subscriptionCartService;
	private final NormalCartService normalCartService;

	public void changeOrderStatusToComplete(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.COMPLETE);

		List<Long> orderedItemsId = getOrderedIds(order);

		if (order.isOrderedAtCart()) {
			refreshCart(order.isSubscription(), order.getUser().getCartId(), orderedItemsId);
		}

		itemCommandService.addSales(getOrderedItemsId(order));
	}

	public void changeOrderStatusToSubscribe(Long orderId, String sid) {
		Order order = orderFindHelper.findOrder(orderId);

		order.addSid(sid);
		order.changeOrderStatus(OrderStatus.SUBSCRIBE);

		List<Long> orderedItemsId = getOrderedIds(order);

		if (order.isOrderedAtCart()) {
			refreshCart(order.isSubscription(), order.getUser().getCartId(), orderedItemsId);
		}

		itemCommandService.addSales(getOrderedItemsId(order));
	}

	private void refreshCart(boolean isSubscription, Long cartId, List<Long> orderedItemsId) {
		if (isSubscription) {
			subscriptionCartService.refresh(cartId, orderedItemsId);
			return;
		}

		normalCartService.refresh(cartId, orderedItemsId);
	}

	public void addTid(Long orderId, String tid) {
		Order order = orderFindHelper.findOrder(orderId);
		order.addTid(tid);
	}

	private List<Long> getOrderedIds(Order order) {
		return order.getOrderItems()
			.stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toList());
	}

	private List<Long> getOrderedItemsId(Order order) {
		return order.getOrderItems().stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toUnmodifiableList());
	}
}
