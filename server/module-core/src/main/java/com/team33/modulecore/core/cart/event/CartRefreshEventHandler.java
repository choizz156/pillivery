// package com.team33.modulecore.core.cart.event;
//
// import java.util.List;
//
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.event.TransactionalEventListener;
//
// import com.team33.modulecore.core.cart.application.CartKeySupplier;
// import com.team33.modulecore.core.cart.application.MemoryCartClient;
// import com.team33.modulecore.core.common.UserFindHelper;
// import com.team33.modulecore.core.order.domain.entity.Order;
// import com.team33.modulecore.core.order.events.CartRefreshedEvent;
// import com.team33.modulecore.core.user.domain.entity.User;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Service
// public class CartRefreshEventHandler {
//
// 	private final UserFindHelper userFindHelper;
// 	private final MemoryCartClient memoryCartClient;
//
// 	@Async
// 	@TransactionalEventListener
// 	public void onCartRefreshEvent(CartRefreshedEvent event) {
// 		Order order = event.getOrder();
// 		User user = userFindHelper.findUser(order.getUserId());
//
// 		if (isSubscriptionInCart(order)) {
// 			refreshCart(user.getSubscriptionCartId(), event.getOrderedIds());
// 			return;
// 		}
//
// 		refreshCart(user.getNormalCartId(), event.getOrderedIds());
// 	}
//
// 	private boolean isSubscriptionInCart(Order order) {
// 		return order.isOrderedAtCart() && order.isSubscription();
// 	}
//
// 	private void refreshCart(long cartId, List<Long> orderedItemsId) {
// 		memoryCartClient.refreshOrderedItem(CartKeySupplier.from(cartId), orderedItemsId);
// 	}
//
// }
