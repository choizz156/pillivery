package com.team33.modulecore.cart.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.cart.mock.FakeCartRepository;
import com.team33.modulecore.config.RedisTestConfig;
import com.team33.modulecore.core.cart.application.MemoryCartService;
import com.team33.modulecore.core.cart.application.NormalCartItemService;
import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.item.domain.entity.Item;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {RedisTestConfig.class, MemoryCartService.class})
@SpringBootTest
class NormalCartItemServiceTest {

	private static final String KEY = "mem:cartId : 1";
	private static final String CART = "cart";
	private FakeCartRepository cartRepository;
	private NormalCart normalCart;
	private Item item;
	private NormalCartItemService normalCartItemService;

	@Autowired
	private MemoryCartService memoryCartService;

	@Autowired
	private RedissonClient redissonClient;

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

		cartRepository = new FakeCartRepository();
		cartRepository.save(normalCart);

		normalCartItemService = new NormalCartItemService(cartRepository, memoryCartService);
	}

	@AfterEach
	void tearDown() {
		cartRepository.deleteById(1L);
	}

	@DisplayName("캐시에 장바구니가 없으면 db에서 조회한 후 캐시에 저장한다.")
	@Test
	void 장바구니_조회1() throws Exception {
		//given//when
		NormalCart result = normalCartItemService.findCart(1L);

		//then
		assertThat(result).isEqualTo(normalCart);
		assertThat(redissonClient.getMapCache(CART).get(KEY)).isNotNull();
	}

	@DisplayName("캐시에 장바구니가 있으면 조회한 캐시에서 조회한다.")
	@Test
	void 장바구니_조회2() throws Exception {
		//given
		redissonClient.getMapCache(CART).put(KEY, normalCart);

		CartRepository cartRepository = mock(CartRepository.class);
		when(cartRepository.findNormalCartById(1L)).thenReturn(Optional.ofNullable(normalCart));

		normalCartItemService = new NormalCartItemService(cartRepository, memoryCartService);

		// when
		NormalCart result = normalCartItemService.findCart(1L);

		//then
		assertThat(result).isEqualTo(normalCart);
		verify(cartRepository, times(0)).findNormalCartById(1L);
	}

	@DisplayName("일반 아이템을 장바구니에 넣을 수 있다.")
	@Test
	void 장바구니_추가() throws Exception {
		//given
		NormalCart result = (NormalCart)redissonClient.getMapCache(CART).put(KEY, normalCart);

		//when
		normalCartItemService.addItem(normalCart.getId(), item, 3);

		//then
		assertThat(result.getCartItems()).hasSize(1)
			.extracting("item.id")
			.containsOnly(1L);
	}

}