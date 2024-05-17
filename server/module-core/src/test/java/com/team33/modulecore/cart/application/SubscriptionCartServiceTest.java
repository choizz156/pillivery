package com.team33.modulecore.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.cart.SubscriptionContext;
import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.mock.FakeCartRepository;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.SubscriptionInfo;

class SubscriptionCartServiceTest {

	private FakeCartRepository cartRepository;
	private Cart cart;
	private Item item;
	private SubscriptionContext context;

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

		context = SubscriptionContext.builder()
			.item(item)
			.quantity(3)
			.subscriptionInfo(SubscriptionInfo.of(true, 30))
			.build();
	}

	@AfterEach
	void tearDown() {
		cartRepository.clear();
	}

	@DisplayName("일반 아이템을 장바구니에 넣을 수 있다.")
	@Test
	void 장바구니_추가() throws Exception {
		//given
		SubscriptionCartService subscriptionCartService = new SubscriptionCartService(cartRepository);

		//when
		subscriptionCartService.addItem(cart.getId(), context);

		//then
		assertThat(cart.getSubscriptionCartItems()).hasSize(1)
			.extracting("item.id")
			.containsOnly(1L);
	}

	@DisplayName("일반 아이템을 장바구니에서 뺄 수 있다.")
	@Test
	void 장바구니_제거() throws Exception {
		//given
		SubscriptionCartService subscriptionCartService = new SubscriptionCartService(cartRepository);
		subscriptionCartService.addItem(cart.getId(), context);

		//when
		subscriptionCartService.removeCartItem(cart.getId(), item);

		//then
		assertThat(cart.getSubscriptionCartItems()).hasSize(0);
	}

	@DisplayName("장바구니의 담겨져 있는 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception {
		//given
		SubscriptionCartService subscriptionCartService = new SubscriptionCartService(cartRepository);
		subscriptionCartService.addItem(cart.getId(), context);

		//when
		subscriptionCartService.changeQuantity(cart.getId(), item, 5);

		//then
		assertThat(cart.getSubscriptionCartItems()).hasSize(1)
			.extracting("totalQuantity")
			.containsOnly(5);
	}

	@DisplayName("정기 결제 기간을 조정할 수 있다.")
	@Test
	void 기한_변경() throws Exception{
		//given
		SubscriptionCartService subscriptionCartService = new SubscriptionCartService(cartRepository);
		subscriptionCartService.addItem(cart.getId(), context);;

		//when
		subscriptionCartService.changePeriod(cart.getId(), item, 50);

		//then
		assertThat(cart.getSubscriptionCartItems()).hasSize(1)
			.extracting("period")
			.containsOnly(50);
	}

	@DisplayName("구매한 장바구니 상품을 제거할 수 있다.")
	@Test
	void 구매_상품_제거() throws Exception{
		//given
		SubscriptionCartService subscriptionCartService = new SubscriptionCartService(cartRepository);
		subscriptionCartService.addItem(cart.getId(), context);;

		//when
		subscriptionCartService.refresh(cart.getId(), List.of(1L));

		//then
		assertThat(cart.getSubscriptionCartItems()).hasSize(0);
	}
}
