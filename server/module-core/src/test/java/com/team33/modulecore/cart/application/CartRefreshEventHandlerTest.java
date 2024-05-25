package com.team33.modulecore.cart.application;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.events.CartRefreshedEvent;

class CartRefreshEventHandlerTest {

	@DisplayName("구매한 상품의 장바구니를 제거할 수 있다.")
	@Test
	void onCartRefreshEvent() {
		CartRefreshEventHandler cartRefreshEventHandler = mock(CartRefreshEventHandler.class);
		Order order = mock(Order.class);
		CartRefreshedEvent cartRefreshedEvent = new CartRefreshedEvent(order, List.of(1L, 2L));

		cartRefreshEventHandler.onCartRefreshEvent(cartRefreshedEvent);

		verify(cartRefreshEventHandler, times(1)).onCartRefreshEvent(cartRefreshedEvent);
	}

	@DisplayName("장바구니 아이템 삭제 이벤트를 처리할 수 있다.")
	@Test
	void event() throws Exception {

		//given
		ArgumentCaptor<CartRefreshedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(CartRefreshedEvent.class);
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		CartRefreshEventHandler cartRefreshEventHandler = mock(CartRefreshEventHandler.class);
		Order order = mock(Order.class);

		CartRefreshedEvent cartRefreshedEvent = new CartRefreshedEvent(order, List.of(1L, 2L));

		applicationContext.publishEvent(cartRefreshedEvent);

		verify(applicationContext).publishEvent(eventArgumentCaptor.capture());
		CartRefreshedEvent captorValue = eventArgumentCaptor.getValue();

		//when
		cartRefreshEventHandler.onCartRefreshEvent(captorValue);

		//then
		verify(cartRefreshEventHandler, times(1)).onCartRefreshEvent(any(CartRefreshedEvent.class));
	}
}