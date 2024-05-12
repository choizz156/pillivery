package com.team33.moduleapi.ui.order;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.order.dto.OrderItemSimpleResponse;
import com.team33.moduleapi.ui.order.dto.OrderPostDto;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.dto.OrderItemServiceDto;

@Component
public class OrderItemServiceMapper {
	public OrderItemServiceDto toOrderItemPostDto(OrderPostDto orderPostDto) {

		return OrderItemServiceDto.builder()
			.itemId(orderPostDto.getItemId())
			.isSubscription(orderPostDto.isSubscription())
			.quantity(orderPostDto.getQuantity())
			.period(orderPostDto.getPeriod())
			.build();
	}

	public List<OrderItemServiceDto> toCartItemPostList(CartOrderPostDto cartOrderPostDto) {
		return cartOrderPostDto.getOrderPostDtoList().stream()
			.map(this::toOrderItemPostDto)
			.collect(Collectors.toList());
	}

	public List<OrderItemSimpleResponse> toOrderSimpleResponse(List<OrderItem> allSubscriptions) {
		return allSubscriptions.stream()
			.map(OrderItemSimpleResponse::of)
			.collect(Collectors.toUnmodifiableList());
	}
}
