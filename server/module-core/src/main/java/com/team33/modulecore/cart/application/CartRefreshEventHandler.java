package com.team33.modulecore.cart.application;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.events.CartRefreshedEvent;
import com.team33.modulecore.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartRefreshEventHandler {

	private final UserFindHelper userFindHelper;
	private final CommonCartItemService cartItemService;

	@Async
	@Transactional
	@TransactionalEventListener
	public void onCartRefreshEvent(CartRefreshedEvent event) {
		Order order = event.getOrder();
		User user = userFindHelper.findUser(order.getUserId());

		if (isSubscriptionInCart(order)) {
			refreshCart(user.getSubscriptionCartId(), event.getOrderedIds());
			return;
		}

		refreshCart(user.getNormalCartId(), event.getOrderedIds());
	}

	private boolean isSubscriptionInCart(Order order) {
		return order.isOrderedAtCart() && order.isSubscription();
	}

	private void refreshCart(Long cartId, List<Long> orderedItemsId) {
		cartItemService.refresh(cartId, orderedItemsId);
	}
}
