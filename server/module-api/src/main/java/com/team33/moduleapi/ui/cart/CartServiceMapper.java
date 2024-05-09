package com.team33.moduleapi.ui.cart;

import org.springframework.stereotype.Component;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.itemcart.dto.SubscriptionItemPostDto;
import com.team33.modulecore.order.domain.SubscriptionInfo;

import lombok.RequiredArgsConstructor;

@Component
public class CartServiceMapper {

	public static SubscriptionInfo toSubscriptionInfo(SubscriptionItemPostDto postDto) {
		return SubscriptionInfo.of(postDto.isSubscription(), postDto.getPeriod());
	}
}
