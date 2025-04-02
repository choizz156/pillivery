package com.team33.modulecore.core.cart.application;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.cart.event.CartRefreshEventHandler;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.events.CartRefreshedEvent;

class NormalCartEntityRefreshEventHandlerTest {

	@DisplayName("구매한 상품의 장바구니를 제거할 수 있다.")
	@Test
	void onCartRefreshEvent() {
		CartRefreshEventHandler cartRefreshEventHandler = mock(CartRefreshEventHandler.class);
		Order order = mock(Order.class);
		CartRefreshedEvent cartRefreshedEvent = new CartRefreshedEvent(order, List.of(1L, 2L));

		cartRefreshEventHandler.onCartRefreshEvent(cartRefreshedEvent);

		verify(cartRefreshEventHandler, times(1)).onCartRefreshEvent(cartRefreshedEvent);
	}
}