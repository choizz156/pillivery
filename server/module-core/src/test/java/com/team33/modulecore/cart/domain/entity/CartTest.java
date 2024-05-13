package com.team33.modulecore.cart.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.cart.domain.NormalCartItem;
import com.team33.modulecore.cart.domain.SubscriptionCartItem;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.SubscriptionInfo;

class CartTest {

	private Cart cart;
	private Item item;
	private Item item1;
	private int quantity;

	@BeforeEach
	void setUpEach() {
		this.cart = Cart.create();
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
		// when
		cart.addNormalItem(item, quantity);
		cart.addNormalItem(item1, quantity);

		// then
		assertThat(cart.getNormalCartItems()).hasSize(2);
		assertThat(cart.getTotalItemCount()).isEqualTo(6);
		assertThat(cart.getTotalPrice()).isEqualTo(9000);
		assertThat(cart.getTotalDiscountPrice()).isEqualTo(3000);
		assertThat(cart.getExpectedPrice()).isEqualTo(6000);
	}

	@DisplayName("장바구니의 일반 아이템을 삭제할 수 있다.")
	@Test
	void 장바구니_삭제1() throws Exception {
		//given
		NormalCartItem normalCartItem = NormalCartItem.of(item, 3);
		NormalCartItem normalCartItem1 = NormalCartItem.of(item1, 3);

		cart.addNormalItem(item, quantity);
		cart.addNormalItem(item1, quantity);

		//when
		cart.removeNormalCartItem(normalCartItem);

		//then
		assertThat(cart.getNormalCartItems()).contains(normalCartItem1);
		assertThat(cart.getNormalCartItems()).hasSize(1)
			.extracting("item.id", "totalQuantity", "item.information.price.realPrice",
				"item.information.price.discountPrice")
			.containsOnly(tuple(2L, 3, 2000, 500));
	}

	@DisplayName("장바구니에 구독 아이템을 추가할 수 있다.")
	@Test
	void 구독_아이템_장바구니_추가() {
		// given
		// when
		cart.addSubscriptionItem(item, quantity, SubscriptionInfo.of(true, 30));
		cart.addSubscriptionItem(item1, quantity, SubscriptionInfo.of(true, 60));

		// then
		assertThat(cart.getSubscriptionCartItems()).hasSize(2)
			.extracting("subscriptionInfo.isSubscription", "subscriptionInfo.period")
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
		SubscriptionCartItem subscriptionCartItem = SubscriptionCartItem.of(item, 3, SubscriptionInfo.of(true, 30));
		SubscriptionCartItem subscriptionCartItem1 = SubscriptionCartItem.of(item1, 3, SubscriptionInfo.of(true, 60));

		cart.addSubscriptionItem(item, quantity, SubscriptionInfo.of(true, 30));
		cart.addSubscriptionItem(item1, quantity, SubscriptionInfo.of(true, 60));

		//when
		cart.removeSubscriptionCartItem(subscriptionCartItem);

		//then
		assertThat(cart.getSubscriptionCartItems()).contains(subscriptionCartItem1);
		assertThat(cart.getSubscriptionCartItems()).hasSize(1)
			.extracting("subscriptionInfo.isSubscription", "subscriptionInfo.period")
			.containsOnly(tuple(true, 60));
	}

	@DisplayName("장바구니의 아이템 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception{
		//given
		NormalCartItem normalCartItem = NormalCartItem.of(item, quantity);
		cart.addNormalItem(item, quantity);
		//when
		cart.changeNormalCartItemQuantity(normalCartItem,2);

		//then
		assertThat(normalCartItem.getTotalQuantity()).isEqualTo(2);
		assertThat(cart.getTotalItemCount()).isEqualTo(2);
		assertThat(cart.getTotalPrice()).isEqualTo(2000);
		assertThat(cart.getTotalDiscountPrice()).isEqualTo(1000);
		assertThat(cart.getExpectedPrice()).isEqualTo(1000);
	}
}