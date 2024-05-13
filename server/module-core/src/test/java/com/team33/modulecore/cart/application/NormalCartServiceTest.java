package com.team33.modulecore.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.mock.FakeCartRepository;
import com.team33.modulecore.item.domain.entity.Item;

class NormalCartServiceTest {

	private FakeCartRepository cartRepository;
	private Cart cart;
	private Item item;

	@BeforeEach
	void setUp() {
		cart = FixtureMonkeyFactory.get().giveMeBuilder(Cart.class)
			.set("id", 1L)
			.set("price", null)
			.set("normalCartItems", new HashSet<>())
			.set("subscriptionCartItems", new HashSet<>())
			.sample();

		item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information.price.realPrice", 1000)
			.set("information.price.discountPrice", 500)
			.sample();

		cartRepository = new FakeCartRepository();
		cartRepository.save(cart);
	}

	@AfterEach
	void tearDown() {
		cartRepository.clear();
	}

	@DisplayName("일반 아이템을 장바구니에 넣을 수 있다.")
	@Test
	void 장바구니_추가() throws Exception {
		//given
		NormalCartService normalCartService = new NormalCartService(cartRepository);

		//when
		normalCartService.addItem(cart.getId(), item, 3);

		//then
		assertThat(cart.getNormalCartItems()).hasSize(1)
			.extracting("item.id")
			.containsOnly(1L);
	}

	@DisplayName("일반 아이템을 장바구니에서 뺄 수 있다.")
	@Test
	void 장바구니_제거() throws Exception {
		//given
		NormalCartService normalCartService = new NormalCartService(cartRepository);
		normalCartService.addItem(cart.getId(), item, 3);

		//when
		normalCartService.removeCartItem(cart.getId(), item);

		//then
		assertThat(cart.getNormalCartItems()).hasSize(0);
	}

	@DisplayName("장바구니의 담겨져 있는 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception{
		//given
		NormalCartService normalCartService = new NormalCartService(cartRepository);
		normalCartService.addItem(cart.getId(), item, 3);

		//when
		normalCartService.changeQuantity(cart.getId(), item, 5);

		//then
		assertThat(cart.getNormalCartItems()).hasSize(1)
			.extracting("totalQuantity")
			.containsOnly(5);
	}
}