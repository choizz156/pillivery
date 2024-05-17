package com.team33.moduleapi.ui.order;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.order.dto.OrderDetailResponse;
import com.team33.moduleapi.ui.order.dto.OrderPostListDto;
import com.team33.moduleapi.ui.order.mapper.OrderItemServiceMapper;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.dto.OrderContext;
import com.team33.modulecore.order.dto.OrderItemServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Order controller.
 */
@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/orders")
public class OrderCommandController {

	private final OrderService orderService;
	private final OrderQueryService orderQueryService;
	private final OrderItemService orderItemService;
	private final OrderItemServiceMapper orderItemServiceMapper;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public SingleResponseDto<OrderDetailResponse> postSingleOrder(
		@RequestBody OrderPostListDto orderPostDtoList
	) {

		List<OrderItemServiceDto> orderItemServiceDto =
			orderItemServiceMapper.toOrderItemPostDto(orderPostDtoList.getOrderPostDtoList());

		List<OrderItem> orderItems =
			orderItemService.toOrderItems(orderItemServiceDto);

		OrderContext orderContext = orderItemServiceMapper.toOrderContext(orderPostDtoList);
		Order order = orderService.callOrder(orderItems, orderContext);

		return new SingleResponseDto<>(OrderDetailResponse.of(order));
	}




	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/subscription/{orderId}") // 정기 구독 아이템의 수량 변경
	public void changeSubscriptionItemQuantity(
		@PathVariable Long orderId,
		@RequestParam Long orderItemId,
		@RequestParam int quantity
	) {
		orderService.changeSubscriptionItemQuantity(orderId, orderItemId, quantity);
	}
}
