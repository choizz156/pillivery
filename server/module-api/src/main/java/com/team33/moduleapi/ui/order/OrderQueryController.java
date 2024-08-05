package com.team33.moduleapi.ui.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.order.dto.OrderDetailResponse;
import com.team33.moduleapi.ui.order.dto.OrderItemSimpleResponse;
import com.team33.moduleapi.ui.order.dto.OrderSimpleResponseDto;
import com.team33.moduleapi.ui.order.mapper.OrderItemMapper;
import com.team33.modulecore.core.order.application.OrderQueryService;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.dto.OrderPageRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(value = "/orders")
public class OrderQueryController {

	private final OrderQueryService orderQueryService;
	private final OrderItemMapper orderItemMapper;

	@GetMapping
	public MultiResponseDto<?> getOrders(
		@RequestParam long userId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "DESC") Direction sort
	) {
		OrderPageRequest orderPageRequest = OrderPageRequest.of(page, size, sort);

		Page<Order> allOrders = orderQueryService.findAllOrders(userId, orderPageRequest);
		List<Order> orders = allOrders.getContent();

		List<OrderSimpleResponseDto> ordersDto = OrderSimpleResponseDto.toList(orders);

		return new MultiResponseDto<>(ordersDto, allOrders);
	}

	@GetMapping("/subscriptions")
	public MultiResponseDto<?> getSubscriptionsOrder(
		@RequestParam Long userId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "DESC") Direction sort
	) {
		OrderPageRequest orderPageRequest = OrderPageRequest.of(page, size, sort);

		List<OrderItem> allSubscriptions = orderQueryService.findAllSubscriptions(userId, orderPageRequest);

		List<OrderItemSimpleResponse> orderSimpleResponse =
			orderItemMapper.toOrderSimpleResponse(allSubscriptions);

		return new MultiResponseDto<>(
			orderSimpleResponse,
			new PageImpl<>(
				orderSimpleResponse,
				PageRequest.of(page - 1, size),
				orderSimpleResponse.size()
			)
		);
	}

	@GetMapping("/{orderId}")
	public SingleResponseDto<OrderDetailResponse> getOrder(
		@PathVariable Long orderId
	) {
		Order order = orderQueryService.findOrder(orderId);
		OrderDetailResponse orderDetailResponse = OrderDetailResponse.of(order);

		return new SingleResponseDto<>(orderDetailResponse);
	}
}
