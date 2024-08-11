package com.team33.modulecore.core.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.config.RedisTestConfig;
import com.team33.modulecore.core.cart.domain.entity.Cart;
import com.team33.modulecore.core.cart.domain.entity.CartItem;
import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {RedisTestConfig.class, MemoryCartService.class})
@SpringBootTest
class MemoryCartServiceTest {

	@Autowired
	private MemoryCartService memoryCartService;
	private Item item;

	@BeforeEach
	void setUpEach() {
		item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
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
			.set("cartItems", List.of(CartItem.builder().item(item).build()))
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
}