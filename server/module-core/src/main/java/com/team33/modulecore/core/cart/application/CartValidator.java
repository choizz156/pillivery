package com.team33.modulecore.core.cart.application;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.cart.vo.CartItemVO;
import com.team33.modulecore.core.cart.vo.CartVO;
import com.team33.modulecore.core.cart.vo.ItemVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CartValidator {

	public void validateQuantity(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
		}
	}

	public void validatePeriod(int period) {
		if (period < 1) {
			throw new IllegalArgumentException("구독 기간은 1일 이상이어야 합니다.");
		}
	}

	public void checkDuplication(Long itemId, CartVO cartVO) {
		List<CartItemVO> cartItems = List.copyOf(cartVO.getCartItems());

		if (cartItems.isEmpty()) {
			return;
		}

		cartItems.stream()
			.filter(Objects::nonNull)
			.map(CartItemVO::getItem)
			.filter(Objects::nonNull)
			.map(ItemVO::getId)
			.filter(itemId::equals)
			.findAny()
			.ifPresent(id -> {
				throw new IllegalArgumentException("이미 존재하는 장바구니입니다.");
			});
	}
}