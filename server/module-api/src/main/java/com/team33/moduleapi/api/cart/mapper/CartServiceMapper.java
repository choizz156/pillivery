package com.team33.moduleapi.api.cart.mapper;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.api.cart.dto.SubscriptionCartItemPostDto;
import com.team33.modulecore.core.cart.domain.ItemVO;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CartServiceMapper {

	private final ItemQueryRepository itemQueryRepository;

	public ItemVO toItemVO(Long itemId) {
		Item item = itemQueryRepository.findById(itemId);
		return ItemVO.builder()
			.id(itemId)
			.enterprise(item.getInformation().getEnterprise())
			.productName(item.getProductName())
			.discountRate((int)item.getDiscountRate())
			.discountPrice(item.getDiscountPrice())
			.originPrice(item.getOriginPrice())
			.thumbnailUrl(item.getThumbnailUrl())
			.realPrice(item.getRealPrice())
			.build();
	}

	public SubscriptionContext toSubscriptionContext(SubscriptionCartItemPostDto postDto) {
		return SubscriptionContext.builder()
			.item(toItemVO(postDto.getItemId()))
			.subscriptionInfo(toSubscriptionInfo(postDto))
			.quantity(postDto.getQuantity())
			.build();
	}

	private SubscriptionInfo toSubscriptionInfo(SubscriptionCartItemPostDto postDto) {
		return SubscriptionInfo.of(postDto.isSubscription(), postDto.getPeriod());
	}
}
