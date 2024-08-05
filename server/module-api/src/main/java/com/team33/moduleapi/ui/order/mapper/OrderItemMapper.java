package com.team33.moduleapi.ui.order.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.order.dto.OrderItemSimpleResponse;
import com.team33.moduleapi.ui.order.dto.OrderPostDto;
import com.team33.moduleapi.ui.order.dto.OrderPostListDto;
import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.Receiver;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;

@Component
public class OrderItemMapper {

	public OrderItemServiceDto toOrderItemPostDto(OrderPostDto orderPostDto) {

		return OrderItemServiceDto.builder()
			.itemId(orderPostDto.getItemId())
			.subscription(orderPostDto.isSubscription())
			.quantity(orderPostDto.getQuantity())
			.period(orderPostDto.getPeriod())
			.build();
	}

	public List<OrderItemSimpleResponse> toOrderSimpleResponse(List<OrderItem> allSubscriptions) {
		return allSubscriptions.stream()
			.map(OrderItemSimpleResponse::of)
			.collect(Collectors.toUnmodifiableList());
	}

	public List<OrderItemServiceDto> toOrderItemPostDto(List<OrderPostDto> orderPostDtoList) {
		return orderPostDtoList.stream()
			.map(this::toOrderItemPostDto)
			.collect(Collectors.toUnmodifiableList());
	}

	public OrderContext toOrderContext(OrderPostListDto orderPostDtoList) {
		return OrderContext.builder()
			.userId(orderPostDtoList.getUserId())
			.isOrderedCart(orderPostDtoList.isOrderedAtCart())
			.isSubscription(orderPostDtoList.isSubscription())
			.receiver(
				Receiver.builder()
				.address(new Address(orderPostDtoList.getCity(), orderPostDtoList.getDetailAddress()))
				.phone(orderPostDtoList.getPhoneNumber())
				.realName(orderPostDtoList.getRealName())
				.build()
			)
			.build();
	}
}
