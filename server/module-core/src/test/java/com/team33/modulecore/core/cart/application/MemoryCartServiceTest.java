package com.team33.modulecore.core.cart.application;

import static com.team33.modulecore.cache.CacheType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulecore.cache.CacheClient;
import com.team33.modulecore.cache.ItemCacheLoader;
import com.team33.modulecore.cache.ItemCacheManager;
import com.team33.modulecore.config.CacheConfig;
import com.team33.modulecore.config.QueryDslConfig;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.cart.vo.CartItemVO;
import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.cart.vo.NormalCartVO;
import com.team33.modulecore.core.cart.vo.SubscriptionCartVO;
import com.team33.modulecore.core.item.infra.ItemQueryDslDao;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.exception.BusinessLogicException;

@ActiveProfiles("test")
@EnableAutoConfiguration
@EnableJpaRepositories("com.team33.modulecore.core")
@EntityScan("com.team33.modulecore.core")
@SpringBootTest(classes = {
	CacheConfig.class,
	CacheClient.class,
	CartCacheManager.class,
	MemoryCartService.class,
	CartValidator.class,
	CartRepository.class,
	NormalCartItemService.class,
	SubscriptionCartItemService.class,
	QueryDslConfig.class,
	ItemQueryDslDao.class,
	ItemCacheManager.class,
	ItemCacheLoader.class
})
class MemoryCartServiceTest {

	private static final String CACHE_KEY = "mem:cart:1";

	@Autowired
	private MemoryCartService memoryCartService;
	@Autowired
	private CartCacheManager cartCacheManager;
	@Autowired
	private CacheManager cacheManager;

	private ItemVO item;

	@BeforeEach
	void setUp() {

		item = createTestItem();
		cacheManager.getCache(CARTS.name()).clear();
	}

	@AfterEach
	void tearDown() {

		cacheManager.getCache(CARTS.name()).clear();
	}

	@DisplayName("일반 상품을 장바구니에 추가한다")
	@Test
	void test1() {
		// given
		NormalCartVO normalCart = new NormalCartVO();
		cartCacheManager.saveCart(CACHE_KEY, normalCart);
		int quantity = 2;

		// when
		memoryCartService.addNormalItem(CACHE_KEY, item, quantity);

		// then
		NormalCartVO result = cartCacheManager.getCart(CACHE_KEY, NormalCartVO.class)
			.orElseThrow();

		assertThat(result.getCartItems())
			.hasSize(1)
			.first()
			.satisfies(cartItem -> {
				assertThat(cartItem.getItem()).isEqualTo(item);
				assertThat(cartItem.getTotalQuantity()).isEqualTo(quantity);
			});
	}

	@DisplayName("구독 상품을 장바구니에 추가한다")
	@Test
	void test2() {
		// given
		SubscriptionCartVO subscriptionCart = SubscriptionCartVO.create();
		cartCacheManager.saveCart(CACHE_KEY, subscriptionCart);
		SubscriptionContext context = createSubscriptionContext(item);

		// when
		memoryCartService.addSubscriptionItem(CACHE_KEY, context);

		// then
		SubscriptionCartVO result = cartCacheManager.getCart(CACHE_KEY, SubscriptionCartVO.class)
			.orElseThrow();

		assertThat(result.getCartItems())
			.hasSize(1)
			.first()
			.satisfies(cartItem -> {
				assertThat(cartItem.getItem()).isEqualTo(item);
				assertThat(cartItem.getSubscriptionInfo().getPeriod())
					.isEqualTo(context.getSubscriptionInfo().getPeriod());
			});
	}

	@DisplayName("장바구니에서 상품을 삭제한다")
	@Test
	void test3() {
		// given
		NormalCartVO cart = new NormalCartVO();
		CartItemVO cartItem = CartItemVO.of(item, 1);
		cart.addNormalItem(cartItem);
		cartCacheManager.saveCart(CACHE_KEY, cart);

		// when
		memoryCartService.deleteCartItem(CACHE_KEY, item.getId(), NormalCartVO.class);

		// then
		NormalCartVO result = cartCacheManager.getCart(CACHE_KEY, NormalCartVO.class)
			.orElseThrow();
		assertThat(result.getCartItems()).isEmpty();
	}

	@Test
	@DisplayName("존재하지 않는 장바구니 접근시 예외를 발생시킨다")
	void test4() {

		assertThatThrownBy(() -> memoryCartService.addNormalItem(CACHE_KEY, createTestItem(), 1))
			.isInstanceOf(BusinessLogicException.class);

	}

	@DisplayName("이미 존재하는 상품 추가시 예외를 발생시킨다")
	@Test
	void test5() {
		// given
		NormalCartVO cart = new NormalCartVO();
		CartItemVO cartItem = CartItemVO.of(item, 1);
		cart.addNormalItem(cartItem);
		cartCacheManager.saveCart(CACHE_KEY, cart);

		// when & then
		assertThatThrownBy(() -> memoryCartService.addNormalItem(CACHE_KEY, item, 1))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("장바구니 상품의 수량을 변경한다")
	@Test
	void test6() {
	    // given
	    NormalCartVO cart = new NormalCartVO();
	    CartItemVO cartItem = CartItemVO.of(item, 1);
	    cart.addNormalItem(cartItem);
	    cartCacheManager.saveCart(CACHE_KEY, cart);
	    int newQuantity = 3;
	    
	    // when
	    memoryCartService.changeQuantity(CACHE_KEY, item.getId(), newQuantity);
	    
	    // then
	    NormalCartVO result = cartCacheManager.getCart(CACHE_KEY, NormalCartVO.class)
	        .orElseThrow();
	    assertThat(result.getCartItems())
	        .hasSize(1)
	        .first()
	        .satisfies(updatedItem -> 
	            assertThat(updatedItem.getTotalQuantity()).isEqualTo(newQuantity)
	        );
	}
	
	@DisplayName("구독 상품의 구독 기간을 변경한다")
	@Test
	void test7() {
	    // given
	    SubscriptionCartVO cart = new SubscriptionCartVO();
	    SubscriptionContext initialContext = createSubscriptionContext(item);
		CartItemVO.of(item,1, initialContext.getSubscriptionInfo());
		cartCacheManager.saveCart(CACHE_KEY, cart);
		memoryCartService.addSubscriptionItem(CACHE_KEY, initialContext);

	    int newPeriod = 90;
	    
	    // when
	    memoryCartService.changePeriod(CACHE_KEY, item.getId(), newPeriod);
	    
	    // then
	    SubscriptionCartVO result = cartCacheManager.getCart(CACHE_KEY, SubscriptionCartVO.class)
	        .orElseThrow();

	    assertThat(result.getCartItems())
	        .hasSize(1)
	        .first()
	        .satisfies(updatedItem -> 
	            assertThat(updatedItem.getSubscriptionInfo().getPeriod()).isEqualTo(newPeriod)
	        );
	}

	private ItemVO createTestItem() {

		return ItemVO.builder()
			.id(1L)
			.originPrice(10000)
			.discountRate(0.0)
			.realPrice(10000)
			.discountPrice(0)
			.productName("Test Item")
			.build();
	}

	private SubscriptionContext createSubscriptionContext(ItemVO item) {
		return SubscriptionContext.builder()
			.item(item)
			.quantity(1)
			.subscriptionInfo(SubscriptionInfo.of(true, 60))
			.build();
	}
}