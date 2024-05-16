package com.team33.moduleapi.ui.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.order.dto.OrderDetailResponse;
import com.team33.moduleapi.ui.order.dto.OrderItemSimpleResponse;
import com.team33.moduleapi.ui.order.dto.OrderSimpleResponse;
import com.team33.moduleapi.ui.order.mapper.OrderItemServiceMapper;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.dto.OrderPageDto;
import com.team33.modulecore.order.dto.OrderPageRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(value = "/orders", method = RequestMethod.GET)
public class OrderQueryController {

	private final OrderQueryService orderQueryService;
	private final OrderItemServiceMapper orderItemServiceMapper;


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

	@GetMapping("/{orderId}")
	public SingleResponseDto<OrderDetailResponse> getOrder(
		@PathVariable Long orderId
	) {
		Order order = orderQueryService.findOrder(orderId);
		OrderDetailResponse orderDetailResponse = OrderDetailResponse.of(order);

		return new SingleResponseDto<>(orderDetailResponse);
	}

}
