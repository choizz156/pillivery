package com.team33.modulecore.core.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.config.RedisTestConfig;
import com.team33.modulecore.core.cart.SubscriptionContext;
import com.team33.modulecore.core.cart.domain.CartPrice;
import com.team33.modulecore.core.cart.domain.entity.Cart;
import com.team33.modulecore.core.cart.domain.entity.CartItem;
import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {RedisTestConfig.class, MemoryCartService.class})
@SpringBootTest
class MemoryCartServiceTest {

	@Autowired
	private MemoryCartService memoryCartService;

	@Autowired
	private RedissonClient redissonClient;

	private Item item;
	private WebSocketServletAutoConfiguration webSocketServletAutoConfiguration;

	@BeforeAll
	void setUpEach() {
		redissonClient.getKeys().flushall();
		item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id",1L)
			.set("information.price.originPrice", 10000)
			.set("information.price.discountPrice", 1000)
			.set("information.price.realPrice", 9000)
			.set("information.price.discountRate", 0.1)
			.setNull("itemCategory")
			.setNull("categories")
			.sample();
	}

	@DisplayName("일반 장바구니를 메모리에 저장할 수 있다.")
	@Test
	void 일반_장바구니_저장() throws Exception {
		//given
		NormalCart normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCart.class)
			.set("id", 1L)
			.set("cartItems", List.of(CartItem.builder().item(item).totalQuantity(1).build()))
			.set("price", new CartPrice())
			.sample();

		//when
		memoryCartService.saveCart("test", normalCart);

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getId()).isEqualTo(normalCart.getId());
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting("item.information.price.originPrice")
			.containsExactly(10000);

	}

	@DisplayName("구독 장바구니를 메로리에 저장할 수 있다.")
	@Test
	void 구독_장바구니_저장() throws Exception {
		//given
		SubscriptionCart subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCart.class)
			.set("id", 1L)
			.set("cartItems",
				List.of(CartItem.builder().subscriptionInfo(SubscriptionInfo.of(true, 30)).item(item).build()))
			.sample();

		//when
		memoryCartService.saveCart("test", subscriptionCart);

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getId()).isEqualTo(subscriptionCart.getId());
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting("subscriptionInfo.subscription")
			.contains(true);

	}

	@DisplayName("일반 장바구니에 아이템을 넣을 수 있다.")
	@Test
	void 일반_장바구니_아이템_저장() throws Exception {
		//given
		NormalCart normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCart.class)
			.set("id", 1L)
			.set("cartItems", new ArrayList<>())
			.set("price", new CartPrice())
			.sample();

		memoryCartService.saveCart("test", normalCart);

		//when
		memoryCartService.addNormalItem("test", item, 1);

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart).isInstanceOf(NormalCart.class);
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting("item.information.price.originPrice")
			.containsExactly(10000);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice(1, 9000, 1000));
	}

	@DisplayName("구독 장바구니에 아이템을 넣을 수 있다.")
	@Test
	void 구독_장바구니_아이템_저장() throws Exception {
		//given
		SubscriptionCart subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCart.class)
			.set("id", 1L)
			.set("cartItems", new ArrayList<>())
			.set("price", new CartPrice())
			.sample();

		memoryCartService.saveCart("test", subscriptionCart);

		//when
		memoryCartService.addSubscriptionItem(
			"test",
			SubscriptionContext.builder()
				.subscriptionInfo(SubscriptionInfo.of(true, 30))
				.item(item)
				.quantity(1)
				.build()
		);

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart).isInstanceOf(SubscriptionCart.class);
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting(
				"subscriptionInfo.subscription",
				"subscriptionInfo.period")
			.contains(tuple(true, 30));
		assertThat(cart.getPrice()).isEqualTo(new CartPrice(1, 9000, 1000));
	}

	@DisplayName("장바구니에서 특정 아이템을 삭제할 수 있다.")
	@Test
	void 장바구니_아이템_삭제() throws Exception {
		//given
		NormalCart normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCart.class)
			.set("id", 1L)
			.set("cartItems", List.of(CartItem.builder().id(1L).item(item).build()))
			.set("price", new CartPrice())
			.sample();

		memoryCartService.saveCart("test", normalCart);
		//when
		memoryCartService.deleteCartItem("test", 1L);
		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getCartItems()).hasSize(0);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice());
	}

	@DisplayName("장바구니의 수량을 변경할 수 있다.")
	@Test
	void 장바구니_수량_변경() throws Exception {
		//given
		SubscriptionCart subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCart.class)
			.set("id", 1L)
			.set("cartItems", new ArrayList<>())
			.set("price", new CartPrice())
			.sample();

		memoryCartService.saveCart("test", subscriptionCart);

		memoryCartService.addSubscriptionItem(
			"test",
			SubscriptionContext.builder()
				.subscriptionInfo(SubscriptionInfo.of(true, 30))
				.item(item)
				.quantity(1)
				.build()
		);

		//when
		memoryCartService.changeQuantity("test", 1L, 2);

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getCartItems()).hasSize(1);
		assertThat(cart.getPrice().getTotalPrice()).isEqualTo(18000);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice(2,18000, 2000));
	}

	@DisplayName("장바구니를 비울 수 있다.")
	@Test
	void 장바구니_비우기() throws Exception {
		//given
		NormalCart normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCart.class)
			.set("id", 1L)
			.set("cartItems", List.of(CartItem.builder().id(1L).item(item).build()))
			.set("price", new CartPrice())
			.sample();

		memoryCartService.saveCart("test", normalCart);

		//when
		memoryCartService.refresh("test");

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getCartItems()).hasSize(0);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice());
	}

	@DisplayName("구입이 완료된 상품은 장바구이에서 삭제할 수 있다.")
	@Test
	void 구입_상품_장바구니_삭제() throws Exception {
		//given
		NormalCart normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCart.class)
			.set("id", 1L)
			.set("cartItems", List.of(CartItem.builder().id(1L).item(item).build()))
			.set("price", new CartPrice())
			.sample();

		memoryCartService.saveCart("test", normalCart);

		//when
		memoryCartService.refreshOrderedItem("test", List.of(1L)  );

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getCartItems()).hasSize(0);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice());
	}

	@DisplayName("구독 장바구니에 아이템의 구독 기한을 변경할 수 있다.")
	@Test
	void 구독_주기_변경() throws Exception {
		//given
		SubscriptionCart subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCart.class)
			.set("id", 1L)
			.set("cartItems",
				List.of(CartItem.builder().subscriptionInfo(SubscriptionInfo.of(true, 30)).item(item).build()))
			.sample();

		memoryCartService.saveCart("test", subscriptionCart);

		//when
		memoryCartService.changePeriod("test", 1L, 60);

		//then
		Cart cart = memoryCartService.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getId()).isEqualTo(subscriptionCart.getId());
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting(
				"subscriptionInfo.subscription",
				"subscriptionInfo.period")
			.contains(tuple(true, 60));
	}
}
