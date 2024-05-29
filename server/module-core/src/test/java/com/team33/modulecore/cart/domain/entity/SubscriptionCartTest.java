package com.team33.modulecore.cart.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.SubscriptionInfo;

public class SubscriptionCartTest {

	private Cart cart;
	private Item item;
	private Item item1;
	private int quantity;

	@BeforeEach
	void setUpEach() {
		this.cart = SubscriptionCart.create();
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

	@DisplayName("장바구니에 구독 아이템을 추가할 수 있다.")
	@Test
	void 구독_아이템_장바구니_추가() {
		// given
		CartItem cartItem = CartItem.of(item, quantity, SubscriptionInfo.of(true, 30));
		CartItem cartItem1 = CartItem.of(item1, quantity, SubscriptionInfo.of(true, 60));

		// when
		SubscriptionCart subscriptionCart = (SubscriptionCart)cart;
		subscriptionCart.addSubscriptionItem(cartItem);
		subscriptionCart.addSubscriptionItem(cartItem1);

		// then
		assertThat(cart.getCartItems()).hasSize(2)
			.extracting("subscriptionInfo.subscription", "subscriptionInfo.period")
			.containsExactlyInAnyOrder(
				tuple(true, 30),
				tuple(true, 60)
			);
		assertThat(cart.getTotalItemCount()).isEqualTo(6);
		assertThat(cart.getTotalPrice()).isEqualTo(9000);
		assertThat(cart.getTotalDiscountPrice()).isEqualTo(3000);
		assertThat(cart.getExpectedPrice()).isEqualTo(6000);
	}

	@DisplayName("장바구니에 구독 아이템을 삭제할 수 있다.")
	@Test
	void 구독_아이템_장바구니_삭제() throws Exception {
		//given
		CartItem cartItem = FixtureMonkeyFactory.get().giveMeBuilder(CartItem.class)
			.set("id", 1L)
			.set("item", item)
			.set("totalQuantity", quantity)
			.set("subscriptionInfo", SubscriptionInfo.of(true, 30))
			.sample();

		SubscriptionCart subscriptionCart = (SubscriptionCart)cart;
		subscriptionCart.addSubscriptionItem(cartItem);

		//when
		cart.removeCartItem(cartItem);

		//then
		assertThat(cart.getCartItems()).hasSize(0);
	}
}
