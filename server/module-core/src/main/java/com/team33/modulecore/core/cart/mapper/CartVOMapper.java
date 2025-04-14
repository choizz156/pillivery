package com.team33.modulecore.core.cart.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.team33.modulecore.core.cart.domain.entity.CartItemEntity;
import com.team33.modulecore.core.cart.domain.entity.NormalCartEntity;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;
import com.team33.modulecore.core.cart.vo.CartItemVO;
import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.cart.vo.NormalCartVO;
import com.team33.modulecore.core.cart.vo.SubscriptionCartVO;
import com.team33.modulecore.core.item.domain.entity.Item;

public class CartVOMapper {

	public static NormalCartVO toNormalCartVO(NormalCartEntity entity) {

		return new NormalCartVO(entity.getId(), entity.getPrice());
	}

	public static SubscriptionCartVO toSubscriptionCartVO(SubscriptionCartEntity subscriptionCartEntity) {

		return new SubscriptionCartVO(
			subscriptionCartEntity.getId(),
			subscriptionCartEntity.getPrice()
		);
	}

	private static List<CartItemVO> toCartItemVOs(List<CartItemEntity> entities) {

		return entities.stream()
			.map(CartVOMapper::toCartItemVO)
			.collect(Collectors.toList());
	}

	private static CartItemVO toCartItemVO(CartItemEntity entity) {

		return CartItemVO.builder()
			.totalQuantity(entity.getTotalQuantity())
			.subscriptionInfo(entity.getSubscriptionInfo())
			.item(toItemVO(entity.getItem()))
			.id(entity.getId())
			.build();
	}

	private static ItemVO toItemVO(Item item) {

		return ItemVO.builder()
			.enterprise(item.getInformation().getEnterprise())
			.productName(item.getProductName())
			.discountRate(item.getDiscountRate())
			.discountPrice(item.getDiscountPrice())
			.originPrice(item.getOriginPrice())
			.thumbnailUrl(item.getThumbnailUrl())
			.realPrice(item.getRealPrice())
			.build();
	}
}