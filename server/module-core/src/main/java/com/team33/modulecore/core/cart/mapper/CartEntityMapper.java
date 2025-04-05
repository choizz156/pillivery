package com.team33.modulecore.core.cart.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.cart.dto.CartVO;
import com.team33.modulecore.core.cart.domain.entity.CartEntity;
import com.team33.modulecore.core.cart.domain.entity.CartItemEntity;
import com.team33.modulecore.core.cart.domain.entity.NormalCartEntity;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CartEntityMapper {

	private final ItemQueryRepository itemQueryRepository;

	public CartEntity to(CartVO cart) {
		return isNormalCart(cart) ? toNormalCart(cart) : toSubscriptionCart(cart);
	}

	private static boolean isNormalCart(CartVO cart) {
		return cart.getId() % 2 != 0;
	}

	private CartEntity toNormalCart(CartVO cart) {
		List<CartItemEntity> cartItemEntityEntities = getCartItemEntities(cart);

		NormalCartEntity normalCart = NormalCartEntity.of(cart.getId(), cart.getPrice(), cartItemEntityEntities);
		cartItemEntityEntities.forEach(i -> i.addCart(normalCart));

		return normalCart;
	}

	private CartEntity toSubscriptionCart(CartVO cart) {
		List<CartItemEntity> cartItemEntityEntities = getCartItemEntities(cart);

		SubscriptionCartEntity subscriptionCart = SubscriptionCartEntity.of(cart.getId(), cart.getPrice(), cartItemEntityEntities);
		cartItemEntityEntities.forEach(i -> i.addCart(subscriptionCart));

		return subscriptionCart;
	}

	private List<CartItemEntity> getCartItemEntities(CartVO cart) {
		List<CartItemEntity> cartItemEntityEntities = cart.getCartItems().stream()
			.map(e -> CartItemEntity.builder()
				.totalQuantity(e.getTotalQuantity())
				.subscriptionInfo(e.getSubscriptionInfo())
				.item(
					itemQueryRepository.findById(e.getItem().getId())
				)
				.build()
			)
			.collect(Collectors.toList());
		return cartItemEntityEntities;
	}
}
