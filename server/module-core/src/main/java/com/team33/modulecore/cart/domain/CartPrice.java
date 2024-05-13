package com.team33.modulecore.cart.domain;

import javax.persistence.Embeddable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CartPrice {

	private int totalItemCount;

	private int totalPrice;

	private int totalDiscountPrice;

	@Builder
	public CartPrice(int totalItemCount, int totalPrice, int totalDiscountPrice) {
		if (isPositive(totalItemCount, totalPrice, totalDiscountPrice)) {
			throw new IllegalArgumentException("모든 가격 정보는 음수여서는 안됩니다.");
		}
		this.totalItemCount = totalItemCount;
		this.totalPrice = totalPrice;
		this.totalDiscountPrice = totalDiscountPrice;
	}

	public static CartPrice of(int price, int discountPrice, int quantity) {
		return CartPrice.builder()
			.totalItemCount(quantity)
			.totalPrice(price * quantity)
			.totalDiscountPrice(discountPrice * quantity)
			.build();
	}

	public CartPrice addPriceInfo(int price, int discountPrice, int quantity) {
		int sumPrice = price * quantity;
		int sumDiscountPrice = discountPrice * quantity;

		return CartPrice.builder()
			.totalPrice(this.totalPrice + sumPrice)
			.totalDiscountPrice(this.totalDiscountPrice + sumDiscountPrice)
			.totalItemCount(this.totalItemCount + quantity)
			.build();
	}

	public CartPrice subtractPriceInfo(int price, int discountPrice, int quantity) {
		int sumPrice = price * quantity;
		int sumDiscountPrice = discountPrice * quantity;

		return CartPrice.builder()
			.totalPrice(this.totalPrice - sumPrice)
			.totalDiscountPrice(this.totalDiscountPrice - sumDiscountPrice)
			.totalItemCount(this.totalItemCount - quantity)
			.build();
	}

	private boolean isPositive(int totalItemCount, int totalPrice, int totalDiscountPrice) {
		return totalItemCount < 0 || totalPrice < 0 || totalDiscountPrice < 0;
	}
}
