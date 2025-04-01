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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.cart.domain.CartItemVO;
import com.team33.modulecore.core.cart.domain.CartPrice;
import com.team33.modulecore.core.cart.domain.CartVO;
import com.team33.modulecore.core.cart.domain.ItemVO;
import com.team33.modulecore.core.cart.domain.NormalCartVO;
import com.team33.modulecore.core.cart.domain.SubscriptionCartVO;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.moduleredis.config.EmbededRedisConfig;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {EmbededRedisConfig.class, MemoryCartClient.class})
@SpringBootTest
class MemoryCartClientTest {

	@Autowired
	private MemoryCartClient memoryCartClient;
	@Autowired
	private RedissonClient redissonClient;

	private ItemVO item;

	@BeforeAll
	void setUp() {
		redissonClient.getKeys().flushall();
		item = FixtureMonkeyFactory.get().giveMeBuilder(ItemVO.class)
			.set("id", 1L)
			.set("originPrice", 10000)
			.set("discountPrice", 1000)
			.set("realPrice", 9000)
			.set("discountRate", 0.1)
			.sample();
	}

	@DisplayName("일반 장바구니를 메모리에 저장할 수 있다.")
	@Test
	void 일반_장바구니_저장() {
		//given
		NormalCartVO normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCartVO.class)
			.set("id", 1L)
			.set("cartItems", List.of(CartItemVO.builder().item(item).totalQuantity(1).build()))
			.set("price", new CartPrice())
			.sample();
		//when
		memoryCartClient.saveCart("test", normalCart);
		//then
		CartVO cart = memoryCartClient.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getId()).isEqualTo(normalCart.getId());
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting("item.originPrice")
			.containsExactly(10000);
	}

	@DisplayName("구독 장바구니를 메모리에 저장할 수 있다.")
	@Test
	void 구독_장바구니_저장() {
		//given
		SubscriptionCartVO subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCartVO.class)
			.set("id", 1L)
			.set("cartItems",
				List.of(CartItemVO.builder().subscriptionInfo(SubscriptionInfo.of(true, 30)).item(item).build()))
			.sample();
		//when
		memoryCartClient.saveCart("test", subscriptionCart);
		//then
		CartVO cart = memoryCartClient.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getId()).isEqualTo(subscriptionCart.getId());
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting("subscriptionInfo.subscription")
			.contains(true);
	}

	@DisplayName("일반 장바구니에 아이템을 넣을 수 있다.")
	@Test
	void 일반_장바구니_아이템_저장() {
		//given
		NormalCartVO normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCartVO.class)
			.set("id", 1L)
			.set("cartItems", new ArrayList<>())
			.set("price", new CartPrice())
			.sample();
		memoryCartClient.saveCart("test", normalCart);
		//when
		memoryCartClient.addNormalItem("test", item, 1);
		//then
		CartVO cart = memoryCartClient.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart).isInstanceOf(NormalCartVO.class);
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting("item.originPrice")
			.containsExactly(10000);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice(1, 9000, 1000));
	}

	@DisplayName("구독 장바구니에 아이템을 넣을 수 있다.")
	@Test
	void 구독_장바구니_아이템_저장() {
		//given
		SubscriptionCartVO subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCartVO.class)
			.set("id", 1L)
			.set("cartItems", new ArrayList<>())
			.set("price", new CartPrice())
			.sample();
		memoryCartClient.saveCart("test", subscriptionCart);
		//when
		memoryCartClient.addSubscriptionItem(
			"test",
			SubscriptionContext.builder()
				.subscriptionInfo(SubscriptionInfo.of(true, 30))
				.item(item)
				.quantity(1)
				.build()
		);
		//then
		CartVO cart = memoryCartClient.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart).isInstanceOf(SubscriptionCartVO.class);
		assertThat(cart.getCartItems()).hasSize(1)
			.extracting(
				"subscriptionInfo.subscription",
				"subscriptionInfo.period")
			.contains(tuple(true, 30));
		assertThat(cart.getPrice()).isEqualTo(new CartPrice(1, 9000, 1000));
	}

	@DisplayName("장바구니에서 특정 아이템을 삭제할 수 있다.")
	@Test
	void 장바구니_아이템_삭제() {
		//given
		NormalCartVO normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCartVO.class)
			.set("id", 1L)
			.set("cartItems", List.of(CartItemVO.builder().id(1L).item(item).build()))
			.set("price", new CartPrice())
			.sample();
		memoryCartClient.saveCart("test", normalCart);
		//when
		memoryCartClient.deleteCartItem("test", 1L);
		//then
		CartVO cart = memoryCartClient.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getCartItems()).hasSize(0);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice());
	}

	@DisplayName("장바구니의 수량을 변경할 수 있다.")
	@Test
	void 장바구니_수량_변경() {
		//given
		SubscriptionCartVO subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCartVO.class)
			.set("id", 1L)
			.set("cartItems", new ArrayList<>())
			.set("price", new CartPrice())
			.sample();
		memoryCartClient.saveCart("test", subscriptionCart);
		memoryCartClient.addSubscriptionItem(
			"test",
			SubscriptionContext.builder()
				.subscriptionInfo(SubscriptionInfo.of(true, 30))
				.item(item)
				.quantity(1)
				.build()
		);
		//when
		memoryCartClient.changeQuantity("test", 1L, 2);
		//then
		CartVO cart = memoryCartClient.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getCartItems()).hasSize(1);
		assertThat(cart.getPrice().getTotalPrice()).isEqualTo(18000);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice(2, 18000, 2000));
	}

	@DisplayName("장바구니를 비울 수 있다.")
	@Test
	void 장바구니_비우기() {
		//given
		NormalCartVO normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCartVO.class)
			.set("id", 1L)
			.set("cartItems", List.of(CartItemVO.builder().id(1L).item(item).build()))
			.set("price", new CartPrice())
			.sample();
		memoryCartClient.saveCart("test", normalCart);
		//when
		memoryCartClient.refresh("test");
		//then
		CartVO cart = memoryCartClient.getCart("test");
		assertThat(cart).isNotNull();
		assertThat(cart.getCartItems()).hasSize(0);
		assertThat(cart.getPrice()).isEqualTo(new CartPrice());
	}
}
