package com.team33.moduleapi.api.order;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.api.order.dto.OrderDetailResponse;
import com.team33.moduleapi.response.MultiResponseDto;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.order.application.OrderQueryService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.dto.OrderPageRequest;
import com.team33.modulecore.core.order.dto.query.OrderItemQueryDto;
import com.team33.modulecore.core.order.dto.query.SubscriptionOrderItemQueryDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderQueryController {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	private final OrderQueryService orderQueryService;

	@GetMapping
	public MultiResponseDto<OrderItemQueryDto> getOrders(
		@RequestParam long userId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "DESC") Direction sort
	) {

		OrderPageRequest orderPageRequest = OrderPageRequest.of(page, size, sort);
		Page<OrderItemQueryDto> allOrders = orderQueryService.findAllOrders(userId, orderPageRequest);
		List<OrderItemQueryDto> content = allOrders.getContent();

		return new MultiResponseDto<>(content, allOrders);
	}

	@GetMapping("/subscriptions")
	public MultiResponseDto<SubscriptionOrderItemQueryDto> getSubscriptionsOrder(
		@RequestParam Long userId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "DESC") Direction sort) {

		OrderPageRequest orderPageRequest = OrderPageRequest.of(page, size, sort);
		Page<SubscriptionOrderItemQueryDto> allSubscriptions = orderQueryService.findAllSubscriptions(userId,
			orderPageRequest);
		List<SubscriptionOrderItemQueryDto> content = allSubscriptions.getContent();

		return new MultiResponseDto<>(content, allSubscriptions);
	}

	@GetMapping("/{orderId}")
	public SingleResponseDto<OrderDetailResponse> getOrder(
		@PathVariable Long orderId) {

		LOGGER.info("Order ID: {}", orderId);

		Order order = orderQueryService.findOrder(orderId);
		OrderDetailResponse orderDetailResponse = OrderDetailResponse.fromOrder(order);

		return new SingleResponseDto<>(orderDetailResponse);
	}
}
