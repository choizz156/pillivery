package com.team33.modulecore.cart.application;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.events.CartRefreshedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartRefreshEventHandler {

	private final SubscriptionCartService subscriptionCartService;
	private final NormalCartService normalCartService;

	@Async
	@Transactional
	@TransactionalEventListener
	public void onCartRefreshEvent(CartRefreshedEvent event) {
		Order order = event.getOrder();

		if (order.isOrderedAtCart()) {
			refreshCart(order.isSubscription(), order.getUser().getCartId(), event.getOrderedIds());
		}
	}

	private void refreshCart(boolean isSubscription, Long cartId, List<Long> orderedItemsId) {
		if (isSubscription) {
			subscriptionCartService.refresh(cartId, orderedItemsId);
			return;
		}
		normalCartService.refresh(cartId, orderedItemsId);
	}
}
