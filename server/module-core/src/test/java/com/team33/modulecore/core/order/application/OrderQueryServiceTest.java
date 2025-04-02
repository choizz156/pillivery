package com.team33.modulecore.core.order.application;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.common.UserFindHelper;
import com.team33.modulecore.core.order.application.OrderQueryService;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;

class OrderQueryServiceTest {

	@DisplayName("모든 주문을 조회할 수 있다.")
	@Test
	void 주문_조회() throws Exception{
		//given
		OrderQueryRepository orderQueryRepository = mock(OrderQueryRepository.class);
		UserFindHelper userFindHelper = mock(UserFindHelper.class);

		OrderQueryService orderQueryService = new OrderQueryService(orderQueryRepository);

		//when
		orderQueryService.findAllOrders(1L, OrderPageRequest.builder().build());

		//then
		verify(orderQueryRepository, times(1)).findOrders(any(OrderPageRequest.class), any(OrderFindCondition.class));
	}
}