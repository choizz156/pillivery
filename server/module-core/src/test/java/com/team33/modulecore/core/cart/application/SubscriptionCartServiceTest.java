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
import com.team33.modulecore.cache.ItemCacheLoader;
import com.team33.modulecore.cache.ItemCacheManager;
import com.team33.modulecore.config.CacheConfig;
import com.team33.modulecore.config.QueryDslConfig;
import com.team33.modulecore.core.cart.domain.CartPrice;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.vo.CartItemVO;
import com.team33.modulecore.core.cart.vo.ItemVO;
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
    SubscriptionCartItemService.class,
    QueryDslConfig.class,
    ItemQueryDslDao.class,
    ItemCacheManager.class,
    ItemCacheLoader.class
})
class SubscriptionCartServiceTest {

    private static final String CACHE_KEY = "mem:cart:1";

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private MemoryCartService memoryCartClient;
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SubscriptionCartItemService subscriptionCartItemService;

    private SubscriptionCartVO subscriptionCartVO;

    @BeforeEach
    void setUp() {
        subscriptionCartVO = new SubscriptionCartVO(1L, CartPrice.of(0, 0, 0));
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
        SubscriptionCartVO result = subscriptionCartItemService.findCart(CACHE_KEY, subscriptionCartVO.getId());

        // then
        assertThat(result)
            .isNotNull()
            .extracting(SubscriptionCartVO::getId)
            .isEqualTo(subscriptionCartVO.getId());

        assertThat(memoryCartClient.getCart(CACHE_KEY, SubscriptionCartVO.class)).isPresent();
    }

    @DisplayName("캐시에 있는 장바구니를 DB 조회없이 반환한다")
    @Test
    void test2() {
        // given
        CartRepository mockRepository = mock(CartRepository.class);
        SubscriptionCartItemService serviceWithMock = new SubscriptionCartItemService(mockRepository, memoryCartClient);
        memoryCartClient.saveCart(CACHE_KEY, subscriptionCartVO);

        // when
        SubscriptionCartVO result = serviceWithMock.findCart(CACHE_KEY, subscriptionCartVO.getId());

        // then
        assertThat(result)
            .isNotNull()
            .extracting(SubscriptionCartVO::getId)
            .isEqualTo(subscriptionCartVO.getId());

        verify(mockRepository, never()).findSubscriptionCartById(any());
    }

    @DisplayName("존재하지 않는 장바구니 조회시 예외를 발생시킨다")
    @Test
    void test3() {
        assertThatThrownBy(() -> subscriptionCartItemService.findCart(CACHE_KEY, 999L))
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
        CartItemVO cartItemVO = CartItemVO.of(createTestItem(), 1, SubscriptionInfo.of(true,60));
        subscriptionCartVO.addCartItems(cartItemVO);
        memoryCartClient.saveCart(CACHE_KEY, subscriptionCartVO);
    }
}