package com.team33.moduleapi.api.cart.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.api.cart.dto.CartItemResponseDto;
import com.team33.moduleapi.api.cart.dto.CartResponseDto;
import com.team33.moduleapi.api.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.core.cart.domain.NormalCartVO;
import com.team33.modulecore.core.cart.domain.SubscriptionCartVO;
@Component
public class CartResponseMapper {

	public CartResponseDto cartNormalResponseDto(NormalCartVO normalCart) {

		return CartResponseDto.builder()
			.cartId(normalCart.getId())
			.totalDiscountPrice(normalCart.getTotalDiscountPrice())
			.totalPrice(normalCart.getTotalPrice())
			.totalItemCount(normalCart.getTotalItemCount())
			.expectPrice(normalCart.getExpectedPrice())
			.cartItems(toNormalCartItemResponse(normalCart))
			.build();
	}

	public CartResponseDto toCartSubscriptionResponseDto(SubscriptionCartVO subscriptionCart) {
		return CartResponseDto.builder()
			.cartId(subscriptionCart.getId())
			.totalDiscountPrice(subscriptionCart.getTotalDiscountPrice())
			.totalPrice(subscriptionCart.getTotalPrice())
			.totalItemCount(subscriptionCart.getTotalItemCount())
			.expectPrice(subscriptionCart.getExpectedPrice())
			.cartItems(toSubscriptionCartItemResponse(subscriptionCart))
			.build();

	}

	private List<CartItemResponseDto> toNormalCartItemResponse(NormalCartVO normalCart) {
		return normalCart.getCartItems().stream()
			.map(normalCartItem ->
				CartItemResponseDto.builder()
					.quantity(normalCartItem.getTotalQuantity())
					.item(ItemSimpleResponseDto.of(normalCartItem.getItem()))
					.build()
			)
			.collect(Collectors.toList());
	}

	private List<CartItemResponseDto> toSubscriptionCartItemResponse(SubscriptionCartVO subscriptionCart) {
		return subscriptionCart.getCartItems().stream()
			.map(subscriptionCartItem ->
				CartItemResponseDto.builder()
					.period(subscriptionCartItem.getSubscriptionInfo().getPeriod())
					.quantity(subscriptionCartItem.getTotalQuantity())
					.subscription(subscriptionCartItem.getSubscriptionInfo().isSubscription())
					.item(ItemSimpleResponseDto.of(subscriptionCartItem.getItem()))
					.build()
			)
			.collect(Collectors.toList());
	}

}
