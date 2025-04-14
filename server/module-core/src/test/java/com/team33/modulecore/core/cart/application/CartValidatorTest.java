package com.team33.modulecore.core.cart.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.cart.vo.CartItemVO;
import com.team33.modulecore.core.cart.vo.CartVO;
import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.cart.vo.NormalCartVO;

class CartValidatorTest {

	private CartValidator cartValidator;
	private CartVO cartVO;
	private ItemVO item;

	@BeforeEach
	void setUp() {
		cartValidator = new CartValidator();
		cartVO = new NormalCartVO();
		item = ItemVO.builder()
			.id(1L)
			.realPrice(1000)
			.discountPrice(500)
			.build();
	}

	@DisplayName("수량이 0 이하일 경우 예외가 발생한다")
	@Test
	void test1() {
		// given
		int invalidQuantity = 0;

		// when & then
		assertThatThrownBy(() -> cartValidator.validateQuantity(invalidQuantity))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("수량은 1개 이상이어야 합니다.");
	}

	@DisplayName("수량이 1 이상일 경우 정상 처리된다")
	@Test
	void test2() {
		// given
		int validQuantity = 1;

		// when & then
		assertThatCode(() -> cartValidator.validateQuantity(validQuantity))
			.doesNotThrowAnyException();
	}

	@DisplayName("구독 기간이 1일 미만일 경우 예외가 발생한다")
	@Test
	void test3() {
		// given
		int invalidPeriod = 0;

		// when & then
		assertThatThrownBy(() -> cartValidator.validatePeriod(invalidPeriod))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("구독 기간은 1일 이상이어야 합니다.");
	}

	@DisplayName("구독 기간이 1일 이상일 경우 정상 처리된다")
	@Test
	void test4() {
		// given
		int validPeriod = 1;

		// when & then
		assertThatCode(() -> cartValidator.validatePeriod(validPeriod))
			.doesNotThrowAnyException();
	}

	@DisplayName("장바구니에 이미 존재하는 상품을 추가할 경우 예외가 발생한다")
	@Test
	void test5() {
		// given
		CartItemVO cartItem = CartItemVO.of(item, 1);
		cartVO.getCartItems().add(cartItem);

		// when & then
		assertThatThrownBy(() -> cartValidator.checkDuplication(item.getId(), cartVO))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이미 존재하는 장바구니입니다.");
	}

	@DisplayName("장바구니에 존재하지 않는 상품은 정상 처리된다")
	@Test
	void test6() {
		// given
		Long newItemId = 2L;

		// when & then
		assertThatCode(() -> cartValidator.checkDuplication(newItemId, cartVO))
			.doesNotThrowAnyException();
	}
}