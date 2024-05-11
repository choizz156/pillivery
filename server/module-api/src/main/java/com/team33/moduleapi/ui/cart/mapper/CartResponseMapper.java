package com.team33.moduleapi.ui.cart.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.cart.dto.CartItemResponseDto;
import com.team33.moduleapi.ui.cart.dto.CartResponseDto;
import com.team33.moduleapi.ui.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.cart.domain.entity.Cart;

@Component
public class CartResponseMapper {


	public CartResponseDto cartNormalResponseDto(Cart cart){

		return CartResponseDto.builder()
			.cartId(cart.getId())
			.totalDiscountPrice(cart.getTotalDiscountPrice())
			.totalPrice(cart.getTotalPrice())
			.totalItemCount(cart.getTotalItemCount())
			.expectPrice(cart.getExpectedPrice())
			.cartItems(toNormalCartItemResponse(cart))
			.build();
	}

	public CartResponseDto cartSubscriptionResponseDto(Cart cart) {
		return CartResponseDto.builder()
			.cartId(cart.getId())
			.totalDiscountPrice(cart.getTotalDiscountPrice())
			.totalPrice(cart.getTotalPrice())
			.totalItemCount(cart.getTotalItemCount())
			.expectPrice(cart.getExpectedPrice())
			.cartItems(toSubscriptionCartItemResponse(cart))
			.build();

	}

	private  List<CartItemResponseDto> toNormalCartItemResponse(Cart cart) {
		return cart.getNormalCartItems().stream()
			.map(normalCartItem ->
				CartItemResponseDto.builder()
					.createdAt(normalCartItem.getCreatedAt())
					.updatedAt(normalCartItem.getUpdatedAt())
					.quantity(normalCartItem.getTotalQuantity())
					.item(ItemSimpleResponseDto.of(normalCartItem.getItem()))
					.build()
			)
			.collect(Collectors.toList());
	}

	private  List<CartItemResponseDto> toSubscriptionCartItemResponse(Cart cart) {
		return cart.getSubscriptionCartItems().stream()
			.map(subscriptionCartItem ->
				CartItemResponseDto.builder()
					.period(subscriptionCartItem.getSubscriptionInfo().getPeriod())
					.createdAt(subscriptionCartItem.getCreatedAt())
					.updatedAt(subscriptionCartItem.getUpdatedAt())
					.quantity(subscriptionCartItem.getTotalQuantity())
					.subscription(subscriptionCartItem.getSubscriptionInfo().isSubscription())
					.item(ItemSimpleResponseDto.of(subscriptionCartItem.getItem()))
					.build()
			)
			.collect(Collectors.toList());
	}
}
