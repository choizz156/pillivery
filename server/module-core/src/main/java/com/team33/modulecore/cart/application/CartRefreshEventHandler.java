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

	private final SubscriptionCartItemService subscriptionCartService;
	private final CommonCartItemService cartItemService;

	@Async
	@Transactional
	@TransactionalEventListener
	public void onCartRefreshEvent(CartRefreshedEvent event) {
		Order order = event.getOrder();

		if (isSubscriptionInCart(order)) {
			refreshCart(order.getUser().getSubscriptionCartId(), event.getOrderedIds());
			return;
		}

		refreshCart(order.getUser().getNormalCartId(), event.getOrderedIds());
	}

	private boolean isSubscriptionInCart(Order order) {
		return order.isOrderedAtCart() && order.isSubscription();
	}

	private void refreshCart(Long cartId, List<Long> orderedItemsId) {
		cartItemService.refresh(cartId, orderedItemsId);
	}
}
