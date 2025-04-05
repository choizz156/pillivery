package com.team33.modulecore.core.cart.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.cart.dto.CartItemVO;
import com.team33.modulecore.core.cart.dto.CartVO;
import com.team33.modulecore.core.cart.dto.ItemVO;
import com.team33.modulecore.core.cart.dto.NormalCartVO;

class NormalCartVOTest {

	private CartVO cartVO;
	private ItemVO item;
	private ItemVO item1;
	private int quantity;

	@BeforeEach
	void setUpEach() {
		cartVO = new NormalCartVO();
		item = FixtureMonkeyFactory.get()
			.giveMeBuilder(ItemVO.class)
			.set("id", 1L)
			.set("realPrice", 1000)
			.set("discountPrice", 500)
			.sample();

		item1 = FixtureMonkeyFactory.get()
			.giveMeBuilder(ItemVO.class)
			.set("id", 2L)
			.set("realPrice", 2000)
			.set("discountPrice", 500)
			.sample();

		quantity = 3;
	}

	@DisplayName("장바구니에 일반 아이템을 추가할 수 있다.")
	@Test
	void 일반_아이템_장바구니_추가() {
		NormalCartVO normalCart = (NormalCartVO)cartVO;
		CartItemVO cartItemVO = CartItemVO.of(item, quantity);
		CartItemVO cartItemVO1 = CartItemVO.of(item1, quantity);
		// when
		normalCart.addNormalItem(cartItemVO);
		normalCart.addNormalItem(cartItemVO1);

		// then
		assertThat(cartVO.getCartItems()).hasSize(2);
		assertThat(cartVO.getTotalItemCount()).isEqualTo(6);
		assertThat(cartVO.getTotalPrice()).isEqualTo(9000);
		assertThat(cartVO.getTotalDiscountPrice()).isEqualTo(3000);
		assertThat(cartVO.getExpectedPrice()).isEqualTo(6000);
	}

	@DisplayName("장바구니의 일반 아이템을 삭제할 수 있다.")
	@Test
	void 장바구니_삭제1() throws Exception {
		//given
		CartItemVO cartItemVO = FixtureMonkeyFactory.get().giveMeBuilder(CartItemVO.class)
			.set("id", 1L)
			.set("totalQuantity", 100)
			.set("item", item)
			.sample();

		NormalCartVO normalCart = (NormalCartVO) this.cartVO;

		normalCart.addNormalItem(cartItemVO);

		//when
		normalCart.removeCartItem(cartItemVO);

		//then
		assertThat(normalCart.getCartItems()).hasSize(0);
	}

	@DisplayName("장바구니의 아이템 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception{
		//given
		CartItemVO cartItemVO = CartItemVO.of(item, quantity);
		NormalCartVO normalCart = (NormalCartVO) this.cartVO;

		normalCart.addNormalItem(cartItemVO);
		//when
		normalCart.changeCartItemQuantity(cartItemVO,2);

		//then
		assertThat(normalCart.getTotalItemCount()).isEqualTo(2);
		assertThat(cartVO.getTotalItemCount()).isEqualTo(2);
		assertThat(cartVO.getTotalPrice()).isEqualTo(2000);
		assertThat(cartVO.getTotalDiscountPrice()).isEqualTo(1000);
		assertThat(cartVO.getExpectedPrice()).isEqualTo(1000);
	}
}