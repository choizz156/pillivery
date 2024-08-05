package com.team33.modulecore.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.cart.SubscriptionContext;
import com.team33.modulecore.core.cart.application.CommonCartItemService;
import com.team33.modulecore.core.cart.application.SubscriptionCartItemService;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.cart.mock.FakeCartRepository;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

class SubscriptionCartServiceTest {

	private FakeCartRepository cartRepository;
	private SubscriptionCart subscriptionCart;
	private SubscriptionContext context;

	@BeforeEach
	void setUp() {

		subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCart.class)
			.set("id", 1L)
			.set("price", null)
			.set("cartItems", new ArrayList<>())
			.sample();

		Item item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information.price.realPrice", 1000)
			.set("information.price.discountPrice", 500)
			.sample();

		cartRepository = new FakeCartRepository();
		cartRepository.save(subscriptionCart);

		context = SubscriptionContext.builder()
			.item(item)
			.quantity(3)
			.subscriptionInfo(SubscriptionInfo.of(true, 30))
			.build();
	}

	@AfterEach
	void tearDown() {
		cartRepository.deleteById(1L);
	}

	@DisplayName("구독 아이템을 장바구니에 넣을 수 있다.")
	@Test
	void 장바구니_추가() throws Exception {
		//given
		SubscriptionCartItemService subscriptionCartService = new SubscriptionCartItemService(cartRepository);

		//when
		subscriptionCartService.addSubscriptionItem(1L, context);

		//then
		assertThat(subscriptionCart.getCartItems()).hasSize(1)
			.extracting("item.id")
			.containsOnly(1L);
	}

	@DisplayName("일반 아이템을 장바구니에서 뺄 수 있다.")
	@Test
	void 장바구니_제거() throws Exception {
		//given
		SubscriptionCartItemService subscriptionCartService = new SubscriptionCartItemService(cartRepository);
		subscriptionCartService.addSubscriptionItem(subscriptionCart.getId(), context);

		CommonCartItemService commonCartItemService = new CommonCartItemService(cartRepository);

		//when
		commonCartItemService.removeCartItem(1L, 1L);

		//then
		assertThat(subscriptionCart.getCartItems()).hasSize(0);
	}

	@DisplayName("장바구니의 담겨져 있는 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception {
		//given
		SubscriptionCartItemService subscriptionCartService = new SubscriptionCartItemService(cartRepository);
		subscriptionCartService.addSubscriptionItem(subscriptionCart.getId(), context);

		CommonCartItemService commonCartItemService = new CommonCartItemService(cartRepository);

		//when
		commonCartItemService.changeQuantity(1L, 1L, 5);

		//then
		assertThat(subscriptionCart.getCartItems()).hasSize(1)
			.extracting("totalQuantity")
			.containsOnly(5);
	}

	@DisplayName("정기 결제 기간을 조정할 수 있다.")
	@Test
	void 기한_변경() throws Exception {
		//given
		SubscriptionCartItemService subscriptionCartService = new SubscriptionCartItemService(cartRepository);
		subscriptionCartService.addSubscriptionItem(subscriptionCart.getId(), context);

		//when
		subscriptionCartService.changePeriod(1L, 1L, 50);

		//then
		assertThat(subscriptionCart.getCartItems()).hasSize(1)
			.extracting("period")
			.containsOnly(50);
	}

	@DisplayName("구매한 장바구니 상품을 제거할 수 있다.")
	@Test
	void 구매_상품_제거() throws Exception {
		//given
		SubscriptionCartItemService subscriptionCartService = new SubscriptionCartItemService(cartRepository);
		subscriptionCartService.addSubscriptionItem(subscriptionCart.getId(), context);

		CommonCartItemService commonCartItemService = new CommonCartItemService(cartRepository);

		//when
		commonCartItemService.refresh(1L, List.of(1L));

		//then
		assertThat(subscriptionCart.getCartItems()).hasSize(0);
	}
}
