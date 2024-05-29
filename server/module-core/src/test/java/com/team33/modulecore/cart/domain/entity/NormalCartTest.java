package com.team33.modulecore.cart.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;

class NormalCartTest {

	private Cart cart;
	private Item item;
	private Item item1;
	private int quantity;

	@BeforeEach
	void setUpEach() {
		this.cart = NormalCart.create();
		item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information.price.realPrice", 1000)
			.set("information.price.discountPrice", 500)
			.sample();

		item1 = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", 2L)
			.set("information.price.realPrice", 2000)
			.set("information.price.discountPrice", 500)
			.sample();

		quantity = 3;
	}

	@DisplayName("장바구니에 일반 아이템을 추가할 수 있다.")
	@Test
	void 일반_아이템_장바구니_추가() {
		// given
		NormalCart normalCart = (NormalCart) this.cart;

		CartItem normalCartItem = CartItem.of(item, quantity);
		CartItem normalCartItem1 = CartItem.of(item1, quantity);
		// when
		normalCart.addNormalItem(normalCartItem);
		normalCart.addNormalItem(normalCartItem1);

		// then
		assertThat(cart.getCartItems()).hasSize(2);
		assertThat(cart.getTotalItemCount()).isEqualTo(6);
		assertThat(cart.getTotalPrice()).isEqualTo(9000);
		assertThat(cart.getTotalDiscountPrice()).isEqualTo(3000);
		assertThat(cart.getExpectedPrice()).isEqualTo(6000);
	}

	@DisplayName("장바구니의 일반 아이템을 삭제할 수 있다.")
	@Test
	void 장바구니_삭제1() throws Exception {
		//given
		CartItem cartItem = FixtureMonkeyFactory.get().giveMeBuilder(CartItem.class)
			.set("id", 1L)
			.set("totalQuantity", 100)
			.set("item", item)
			.sample();

		NormalCart normalCart = (NormalCart) this.cart;

		normalCart.addNormalItem(cartItem);

		//when
		normalCart.removeCartItem(cartItem);

		//then
		assertThat(normalCart.getCartItems()).hasSize(0);
	}

	@DisplayName("장바구니의 아이템 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception{
		//given
		CartItem normalCartItem = CartItem.of(item, quantity);
		NormalCart normalCart = (NormalCart) this.cart;

		normalCart.addNormalItem(normalCartItem);
		//when
		cart.changeCartItemQuantity(normalCartItem,2);

		//then
		assertThat(normalCartItem.getTotalQuantity()).isEqualTo(2);
		assertThat(cart.getTotalItemCount()).isEqualTo(2);
		assertThat(cart.getTotalPrice()).isEqualTo(2000);
		assertThat(cart.getTotalDiscountPrice()).isEqualTo(1000);
		assertThat(cart.getExpectedPrice()).isEqualTo(1000);
	}
}