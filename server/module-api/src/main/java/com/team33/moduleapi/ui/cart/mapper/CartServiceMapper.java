package com.team33.moduleapi.ui.cart.mapper;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.cart.dto.SubscriptionCartItemPostDto;
import com.team33.modulecore.core.cart.SubscriptionContext;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CartServiceMapper {

	private final ItemQueryRepository itemQueryRepository;

	public Item toItem(Long itemId) {
		return itemQueryRepository.findById(itemId);
	}

	private SubscriptionInfo toSubscriptionInfo(SubscriptionCartItemPostDto postDto) {
		return SubscriptionInfo.of(postDto.isSubscription(), postDto.getPeriod());
	}

	public SubscriptionContext toSubscriptionContext(SubscriptionCartItemPostDto postDto) {
		return SubscriptionContext.builder()
			.item(toItem(postDto.getItemId()))
			.subscriptionInfo(toSubscriptionInfo(postDto))
			.quantity(postDto.getQuantity())
			.build();
	}
}
