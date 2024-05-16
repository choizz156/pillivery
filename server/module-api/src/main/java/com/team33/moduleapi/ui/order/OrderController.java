package com.team33.moduleapi.ui.order;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.order.dto.OrderDetailResponse;
import com.team33.moduleapi.ui.order.dto.OrderItemSimpleResponse;
import com.team33.moduleapi.ui.order.dto.OrderPostListDto;
import com.team33.moduleapi.ui.order.dto.OrderSimpleResponse;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.dto.OrderContext;
import com.team33.modulecore.order.dto.OrderItemServiceDto;
import com.team33.modulecore.order.dto.OrderPageDto;
import com.team33.modulecore.order.dto.OrderPageRequest;

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
public class OrderController {

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


	@GetMapping
	public MultiResponseDto<?> getOrders(
		@RequestParam Long userId,
		OrderPageDto pageDto
	) {
		OrderPageRequest orderPageRequest = OrderPageRequest.of(pageDto);

		Page<Order> allOrders = orderQueryService.findAllOrders(userId, orderPageRequest);
		List<Order> orders = allOrders.getContent();

		List<OrderSimpleResponse> ordersDto = OrderSimpleResponse.toList(orders);

		return new MultiResponseDto<>(ordersDto, allOrders);
	}

	@GetMapping("/subscriptions")
	public MultiResponseDto<?> getSubscriptionsOrder(
		@RequestParam Long userId,
		OrderPageDto pageDto
	) {
		OrderPageRequest orderPageRequest = OrderPageRequest.of(pageDto);

		List<OrderItem> allSubscriptions = orderQueryService.findAllSubscriptions(userId, orderPageRequest);

		List<OrderItemSimpleResponse> orderSimpleResponse =
			orderItemServiceMapper.toOrderSimpleResponse(allSubscriptions);

		return new MultiResponseDto<>(
			orderSimpleResponse,
			new PageImpl<>(
				orderSimpleResponse,
				PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
				orderSimpleResponse.size()
			)
		);
	}

	/**
	 * 특정 주문 상세 내역을 확인
	 */
	@GetMapping("/{orderId}")
	public SingleResponseDto<?> getOrder(
		@RequestParam Long userId,
		@PathVariable Long orderId
	) {
		Order order = orderQueryService.findOrder(orderId);
		OrderDetailResponse orderDetailResponse = OrderDetailResponse.of(order);

		return new SingleResponseDto<>(orderDetailResponse);
	}

	/**
	 * Change quantity single response dto.
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/subscription/{orderId}") // 정기 구독 아이템의 수량 변경
	public void changeSubscriptionItemQuantity(
		@PathVariable Long orderId,
		@RequestParam Long orderItemId,
		@RequestParam int quantity
	) {
		orderService.changeSubscriptionItemQuantity(orderId, orderItemId, quantity);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{orderId}") // 특정 주문 취소
	public void cancelOrder(@NotNull @PathVariable Long orderId) {
		orderService.cancelOrder(orderId);
	}
}
