package com.team33.modulecore.cart.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CartPriceTest {

	private CartPrice cartPrice;

	@BeforeEach
	void setUp() {
		cartPrice = CartPrice.builder()
			.totalItemCount(5)
			.totalPrice(100)
			.totalDiscountPrice(20)
			.build();
	}

	@DisplayName("장바구니에 아이템이 추가될 때, 장바구니의 총 가격, 총 수량, 총 할인 금액을 더할 수 있다.")
	@Test
	void 장바구니_더하기() throws Exception {
		//given
		CartPrice updatedCartPrice = cartPrice.addPriceInfo(10, 2, 3);

		//then
		assertThat(updatedCartPrice.getTotalPrice()).isEqualTo(130);
		assertThat(updatedCartPrice.getTotalItemCount()).isEqualTo(8);
		assertThat(updatedCartPrice.getTotalDiscountPrice()).isEqualTo(26);
	}

	@DisplayName("장바구에 아이템이 제거될 때, 장바구니의 총 가격, 총 수량, 총 할인 금액을 뺄 수 있다.")
	@Test
	void 장바구니_가격_빼기() throws Exception {
		//given
		//when
		CartPrice updatedCartPrice = cartPrice.subtractPriceInfo(10, 2, 3);
		//then
		assertThat(updatedCartPrice.getTotalPrice()).isEqualTo(70);
		assertThat(updatedCartPrice.getTotalItemCount()).isEqualTo(2);
		assertThat(updatedCartPrice.getTotalDiscountPrice()).isEqualTo(14);
	}
}