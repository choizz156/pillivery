package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.item.domain.mock.FakeItemQuerydslDao;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.dto.OrderItemServiceDto;
import com.team33.modulecore.order.mock.FakeOrderRepository;

class OrderItemServiceTest {

	@DisplayName("orderItem을 1개 생성할 수 있다.")
	@Test
	void createOrderItem() throws Exception {
		//given
		var dto = OrderItemServiceDto.builder()
			.isSubscription(false)
			.itemId(1L)
			.period(30)
			.quantity(3)
			.build();

		OrderItemService orderItemService = new OrderItemService(new FakeOrderRepository(), new FakeItemQuerydslDao());

		//when
		List<OrderItem> orderItemSingle = orderItemService.getOrderItemSingle(dto);

		//then
		orderItem_생성(orderItemSingle);
	}

	@DisplayName("장바구니에 있는 아이템을 OrderItem 객체를 만들 수 있다.")
	@Test
	void createOrderItemCart() throws Exception {
		//given
		OrderItemService orderItemService = new OrderItemService(new FakeOrderRepository(), new FakeItemQuerydslDao());

		var dto = OrderItemServiceDto.builder()
			.isSubscription(false)
			.itemId(1L)
			.quantity(3)
			.build();
		//when
		List<OrderItem> orderItemsInCart = orderItemService.getOrderItemsInCart(List.of(dto));

		//then
		assertThat(orderItemsInCart).hasSize(1)
			.extracting("subscriptionInfo.isSubscription", "subscriptionInfo.period", "quantity")
			.containsOnly(tuple(false, 0, 3));

	}

	private void orderItem_생성(List<OrderItem> orderItemSingle) {
		OrderItem orderItem = orderItemSingle.get(0);
		assertThat(orderItemSingle).hasSize(1);
		assertThat(orderItem.getItem().getId()).isNotNull();
		assertThat(orderItem.getSubscriptionInfo())
			.isEqualTo(orderItemSingle.get(0).getSubscriptionInfo());
		assertThat(orderItem.getQuantity()).isEqualTo(3);
		assertThat(orderItem.getPeriod()).isEqualTo(30);
		assertThat(orderItem.isSubscription()).isFalse();
	}
}