package com.team33.modulecore.core.order.application;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;
import com.team33.modulecore.core.order.dto.query.SubscriptionOrderItemQueryDto;

class OrderQueryServiceTest {

	@DisplayName("모든 주문을 조회할 수 있다.")
	@Test
	void 주문_조회() throws Exception{
		//given
		OrderQueryRepository orderQueryRepository = mock(OrderQueryRepository.class);

		OrderQueryService orderQueryService = new OrderQueryService(orderQueryRepository);

		//when
		orderQueryService.findAllOrders(1L, OrderPageRequest.builder().build());

		//then
		verify(orderQueryRepository, times(1)).findOrdersWithItems(any(OrderPageRequest.class), any(OrderFindCondition.class));
	}

	@DisplayName("모든 구독 주문을 조회 요청을 보낼 수 있다.")
	@Test
	void 구독_주문_조회() throws Exception {
		// given
		OrderQueryRepository orderQueryRepository = mock(OrderQueryRepository.class);
		OrderQueryService orderQueryService = new OrderQueryService(orderQueryRepository);

		long userId = 1L;
		OrderPageRequest orderPageRequest = OrderPageRequest.builder().build();
		Page<SubscriptionOrderItemQueryDto> mockedPage = mock(Page.class);
		SubscriptionOrderItemQueryDto mockDto = mock(SubscriptionOrderItemQueryDto.class);
		List<SubscriptionOrderItemQueryDto> content = List.of(mockDto);
		when(orderQueryRepository.findSubscriptionOrderItemsWithItems(any(OrderPageRequest.class), any(OrderFindCondition.class)))
			.thenReturn(mockedPage);

		// when
		Page<SubscriptionOrderItemQueryDto> result =
			orderQueryService.findAllSubscriptions(userId,
			orderPageRequest
			);

		// then
		verify(orderQueryRepository, times(1)).findSubscriptionOrderItemsWithItems(
			eq(orderPageRequest),
			argThat(condition -> condition.getUserId() == userId && condition.getOrderStatus() == OrderStatus.SUBSCRIPTION)
		);
	}
}