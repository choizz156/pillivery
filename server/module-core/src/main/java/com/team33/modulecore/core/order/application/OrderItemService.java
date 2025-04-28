package com.team33.modulecore.core.order.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.common.ItemFindHelper;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemService {

	private final ItemFindHelper itemFindHelper;

	@Transactional(readOnly = true)
	public List<OrderItem> convertToOrderItems(List<OrderItemServiceDto> dtos) {

		return dtos.stream()
			.map(this::makeOrderItem)
			.collect(Collectors.toList());
	}

	private Item findItem(long id) {
		return itemFindHelper.findItem(id);
	}

	private OrderItem makeOrderItem(OrderItemServiceDto dto) {

		Item item = findItem(dto.getItemId());
		SubscriptionInfo subscriptionInfo = SubscriptionInfo.of(dto.isSubscription(), dto.getPeriod());
		return OrderItem.create(
			item,
			dto.getQuantity(),
			subscriptionInfo
		);
	}

}