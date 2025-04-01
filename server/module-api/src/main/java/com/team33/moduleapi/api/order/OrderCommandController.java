package com.team33.moduleapi.api.order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.api.order.dto.OrderDetailResponse;
import com.team33.moduleapi.api.order.dto.OrderPostListDto;
import com.team33.moduleapi.api.order.mapper.OrderItemMapper;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.order.application.OrderCreateService;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.application.OrderSubscriptionService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderCommandController {

	private final OrderCreateService orderCreateService;
	private final OrderItemService orderItemService;
	private final OrderSubscriptionService orderSubscriptionService;
	private final OrderItemMapper orderItemMapper;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public SingleResponseDto<OrderDetailResponse> postSingleOrder(
		@RequestBody OrderPostListDto orderPostDtoList
	) {

		List<OrderItemServiceDto> orderItemServiceDto =
			orderItemMapper.toOrderItemPostDto(orderPostDtoList.getOrderPostDtoList());

		OrderContext orderContext = orderItemMapper.toOrderContext(orderPostDtoList);

		List<OrderItem> orderItems =
			orderItemService.convertToOrderItems(orderItemServiceDto);

		Order order = orderCreateService.callOrder(orderItems, orderContext);

		return new SingleResponseDto<>(OrderDetailResponse.fromOrder(order));
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/subscriptions/{orderId}")
	public void changeSubscriptionItemQuantity(
		@PathVariable Long orderId,
		@RequestParam Long orderItemId,
		@RequestParam int quantity
	) {
		orderSubscriptionService.changeSubscriptionItemQuantity(orderId, orderItemId, quantity);
	}
}
