package com.team33.moduleapi.ui.cart.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponseDto {

	private Long cartId;
	private List<CartItemResponseDto> cartItems;
	private int totalItemCount;
	private int totalPrice;
	private int totalDiscountPrice;
	private int expectPrice; // 결제 예상 금액 (totalPrice - totalDiscountPrice)

	@Builder
	private CartResponseDto(
		Long cartId,
		List<CartItemResponseDto> cartItems,
		int totalItemCount,
		int totalPrice,
		int totalDiscountPrice,
		int expectPrice
	) {
		this.cartId = cartId;
		this.cartItems = cartItems;
		this.totalItemCount = totalItemCount;
		this.totalPrice = totalPrice;
		this.totalDiscountPrice = totalDiscountPrice;
		this.expectPrice = expectPrice;
	}

}
