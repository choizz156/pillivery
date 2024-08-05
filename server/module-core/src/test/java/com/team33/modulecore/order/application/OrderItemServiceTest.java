package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.common.ItemFindHelper;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;

class OrderItemServiceTest {

	@DisplayName("orderItem을 생성할 수 있다.")
	@Test
	void createOrderItem() throws Exception {
		//given
		var dto1 = OrderItemServiceDto.builder()
			.subscription(true)
			.itemId(1L)
			.period(30)
			.quantity(3)
			.build();

		var dto2 = OrderItemServiceDto.builder()
			.subscription(false)
			.itemId(2L)
			.period(0)
			.quantity(1)
			.build();

		List<OrderItemServiceDto> dtos = List.of(dto1, dto2);

		ItemFindHelper itemFindHelper = mock(ItemFindHelper.class);

		OrderItemService orderItemService = new OrderItemService(null, null, itemFindHelper, null);

		//when
		List<OrderItem> orderItems = orderItemService.toOrderItems(dtos);

		//then
		assertThat(orderItems).hasSize(2)
			.extracting("subscriptionInfo.subscription", "subscriptionInfo.period", "quantity")
			.containsExactlyInAnyOrder(
				tuple(true, 30, 3),
				tuple(false, 0, 1)
			);

		verify(itemFindHelper, times(2)).findItem(anyLong());

	}

	@DisplayName("다음 결제일을 업데이트 할 수 있다.")
	@Test
	void 다음_결제일_업데이트() throws Exception {
		//given
		OrderItem orderItem = FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("id", 1L)
			.set("subscriptionInfo", SubscriptionInfo.of(true, 30))
			.sample();

		OrderQueryRepository orderQueryRepository = mock(OrderQueryRepository.class);
		when(orderQueryRepository.findSubscriptionOrderItemBy(anyLong())).thenReturn(orderItem);

		OrderItemService orderItemService = new OrderItemService(null, null, null, orderQueryRepository);


		//when
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		orderItemService.updateNextPaymentDate(now, orderItem.getId());

		//then
		assertThat(orderItem.getSubscriptionInfo().getNextPaymentDay()).isEqualTo(now.plusDays(30));
	}
}