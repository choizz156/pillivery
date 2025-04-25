package com.team33.modulecore.core.cart.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team33.modulecore.core.cart.application.CartKeySupplier;
import com.team33.modulecore.core.cart.application.MemoryCartService;
import com.team33.modulecore.core.common.UserFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.events.CartRefreshedEvent;
import com.team33.modulecore.core.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartRefreshEventHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final UserFindHelper userFindHelper;
	private final MemoryCartService memoryCartService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onCartRefreshEvent(CartRefreshedEvent event) {
		try {
			Order order = event.getOrder();
			User user = userFindHelper.findUser(order.getUserId());

			if (isSubscriptionInCart(order)) {
				refreshCart(user.getSubscriptionCartId(), event.getOrderedIds());
				return;
			}

			refreshCart(user.getNormalCartId(), event.getOrderedIds());
		}catch (Exception ex) {
			LOGGER.warn("장바구니 정리 중 오류 발생, 무시하고 계속 진행합니다.", ex);
		}
	}

	private boolean isSubscriptionInCart(Order order) {
		return order.isOrderedAtCart() && order.isSubscription();
	}

	private void refreshCart(long cartId, List<Long> orderedItemsId) {
		memoryCartService.refreshOrderedItem(CartKeySupplier.from(cartId), orderedItemsId);
	}

}
