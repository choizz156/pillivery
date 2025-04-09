package com.team33.modulecore.core.cart.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.config.CacheConfig;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.dto.SubscriptionCartVO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@Transactional
@EnableAutoConfiguration
@EnableJpaRepositories("com.team33.modulecore.core.cart")
@EntityScan("com.team33.modulecore.core")
@SpringBootTest(classes = {CacheConfig.class, CacheManager.class, MemoryCartClient.class, CartRepository.class})
class SubscriptionCartServiceTest {

    private static final String KEY = "mem:cartId : 1";
    private SubscriptionCartVO subscriptionCart;
    private SubscriptionCartEntity subscriptionCartEntity;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemoryCartClient memoryCartClient;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        subscriptionCartEntity = cartRepository.save(SubscriptionCartEntity.create());
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteAll();
        cacheManager.getCache("CARTS").clear();
    }

    @DisplayName("캐시에 장바구니가 없으면 db에서 조회한 후 캐시에 저장한다.")
    @Test
    void 장바구니_조회1() throws Exception {
        //given
        SubscriptionCartItemService subscriptionCartItemService =
            new SubscriptionCartItemService(cartRepository, memoryCartClient);
        //when
        subscriptionCartItemService.findCart(KEY, subscriptionCartEntity.getId());

        //then
        assertThat(memoryCartClient.getCart(KEY, SubscriptionCartVO.class)).isNotNull();
    }

    @DisplayName("캐시에 장바구니가 있으면 조회한 캐시에서 조회한다.")
    @Test
    void 장바구니_조회2() throws Exception {
        //given
        subscriptionCart = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionCartVO.class)
            .set("id", 1L)
            .set("price", null)
            .set("cartItems", new ArrayList<>())
            .sample();
            
        memoryCartClient.saveCart(KEY, subscriptionCart);

        CartRepository cartRepository = mock(CartRepository.class);
        when(cartRepository.findSubscriptionCartById(1L)).thenReturn(null);

        SubscriptionCartItemService subscriptionCartItemService =
            new SubscriptionCartItemService(cartRepository, memoryCartClient);

        // when
        SubscriptionCartVO result = subscriptionCartItemService.findCart(KEY, subscriptionCartEntity.getId());

        //then
        assertThat(result).isEqualTo(subscriptionCart);
        verify(cartRepository, times(0)).findSubscriptionCartById(1L);
    }
}