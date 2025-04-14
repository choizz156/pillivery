package com.team33.modulecore.core.cart.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.team33.modulecore.config.CacheConfig;
import com.team33.modulecore.core.cart.domain.CartPrice;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.vo.CartItemVO;
import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.cart.vo.NormalCartVO;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
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
	NormalCartItemService.class
})
class NormalCartItemServiceTest {

	private static final String CACHE_KEY = "mem:cart:1";

	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private MemoryCartService memoryCartClient;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private ItemCommandRepository itemCommandRepository;
	@Autowired
	private NormalCartItemService normalCartItemService;

	private NormalCartVO normalCartVO;
	private ItemVO itemVO;

	@BeforeEach
	void setUp() {
		itemVO = createTestItem();
		normalCartVO = new NormalCartVO(1L, CartPrice.of(0,0,0));

		cacheManager.getCache("CARTS").clear();
	}

	@AfterEach
	void tearDown() {
		cartRepository.deleteAll();
		cacheManager.getCache("CARTS").clear();
	}

	@DisplayName("캐시에 없는 장바구니를 DB에서 조회하여 캐시에 저장한다")
	@Test
	void test1() {
		// when
		카트_생성();
		NormalCartVO result = normalCartItemService.findCart(CACHE_KEY, normalCartVO.getId());

		// then
		assertThat(result)
			.isNotNull()
			.extracting(NormalCartVO::getId)
			.isEqualTo(normalCartVO.getId());

		assertThat(memoryCartClient.getCart(CACHE_KEY, NormalCartVO.class)).isPresent();
	}

	@DisplayName("캐시에 있는 장바구니를 DB 조회없이 반환한다")
	@Test
	void test2() {
		// given
		CartRepository mockRepository = mock(CartRepository.class);
		NormalCartItemService serviceWithMock = new NormalCartItemService(mockRepository, memoryCartClient);
		memoryCartClient.saveCart(CACHE_KEY, normalCartVO);

		// when
		NormalCartVO result = serviceWithMock.findCart(CACHE_KEY, normalCartVO.getId());

		// then
		assertThat(result)
			.isNotNull()
			.extracting(NormalCartVO::getId)
			.isEqualTo(normalCartVO.getId());

		verify(mockRepository, never()).findNormalCartById(any());
	}

	@DisplayName("존재하지 않는 장바구니 조회시 예외를 발생시킨다")
	@Test
	void test3() {
		assertThatThrownBy(() -> normalCartItemService.findCart(CACHE_KEY, 999L))
			.isInstanceOf(BusinessLogicException.class);
	}

	private ItemVO createTestItem() {
		return ItemVO.builder()
		    .id(1L)
		    .productName("Test Item")
		    .originPrice(10000)
		    .discountRate(0.0)
		    .realPrice(10000)
		    .discountPrice(0)
		    .build();
	}

	private void 카트_생성() {
		CartItemVO cartItemVO = CartItemVO.of(itemVO, 1);
		normalCartVO.addCartItems(cartItemVO);
		memoryCartClient.saveCart(CACHE_KEY, normalCartVO);
	}
}