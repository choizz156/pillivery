package com.team33.modulecore.core.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
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
import com.team33.modulecore.core.item.domain.entity.Item;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {RedisTestConfig.class, MemoryCartService.class})
@SpringBootTest
class CommonCartItemServiceTest {

	private static final String KEY = "mem:cartId : 1";
	private static final String CART = "cart";
	private NormalCart normalCart;
	private Item item;

	@Autowired
	private MemoryCartService memoryCartService;

	@Autowired
	private RedissonClient redissonClient;

	private CommonCartItemService cartItemService;


	@BeforeEach
	void setUp() {
		normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCart.class)
			.set("id", 1L)
			.set("price", null)
			.set("cartItems", new ArrayList<>())
			.sample();

		item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information.price.realPrice", 1000)
			.set("information.price.discountPrice", 500)
			.sample();

		cartItemService = new CommonCartItemService(memoryCartService);

	}

	@AfterEach
	void tearDown() {
		redissonClient.getKeys().flushall();
	}

	@DisplayName("일반 아이템을 장바구니에서 뺄 수 있다.")
	@Test
	void 장바구니_제거() throws Exception {
		//given
		RMapCache<String, Cart> mapCache = inputData();

		//when
		cartItemService.removeCartItem(normalCart.getId(), 1L);

		//then
		Cart result = mapCache.get(KEY);
		assertThat(result.getCartItems()).hasSize(0);
		assertThat(result.getTotalItemCount()).isEqualTo(0);
		assertThat(result.getTotalDiscountPrice()).isEqualTo(0);
		assertThat(result.getExpectedPrice()).isEqualTo(0);
	}

	@DisplayName("장바구니의 담겨져 있는 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception {
		//given
		RMapCache<String, Cart> mapCache = inputData();

		//when
		cartItemService.changeQuantity(normalCart.getId(), 1L, 5);

		//then
		Cart result = mapCache.get(KEY);
		assertThat(result.getCartItems()).hasSize(1)
			.extracting("totalQuantity")
			.containsOnly(5);
		assertThat(result.getTotalItemCount()).isEqualTo(5);
		assertThat(result.getTotalDiscountPrice()).isEqualTo(2500);
		assertThat(result.getExpectedPrice()).isEqualTo(2500);
	}

	@DisplayName("구매 수량 변경 시 기존 수량과 동일하면 아무일도 일어나지 않는다.")
	@Test
	void 수량_변경_예외() throws Exception {
		//given
		RMapCache<String, Cart> mapCache = inputData();

		//when
		cartItemService.changeQuantity(normalCart.getId(), 1L, 3);

		//then
		NormalCart result = (NormalCart)mapCache.get(KEY);
		assertThat(result.getCartItems()).hasSize(1)
			.extracting("totalQuantity")
			.containsOnly(3);
	}

	@DisplayName("구매 완료 후 구매한 상품을 장바구니에서 제거한다.")
	@Test
	void 구매상품_장바구니_제거() throws Exception {
		//given
		RMapCache<String, Cart> mapCache = inputData();

		//when
		cartItemService.refresh(normalCart.getId(), List.of(1L));

		//then
		Cart result = mapCache.get(KEY);
		assertThat(result.getCartItems()).hasSize(0);
	}

	private RMapCache<String, Cart> inputData() {
		RMapCache<String,Cart> mapCache = redissonClient.getMapCache(CART);
		mapCache.put(KEY, normalCart);
		NormalCart cart = (NormalCart)mapCache.get(KEY);
		cart.addNormalItem(CartItem.of(item, 3));
		mapCache.put(KEY, cart);
		return mapCache;
	}
}