package com.team33.modulecore.core.cart.application;

import static com.team33.modulecore.cache.CacheType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.team33.modulecore.core.cart.dto.CartItemVO;
import com.team33.modulecore.core.cart.dto.CartVO;
import com.team33.modulecore.core.cart.dto.ItemVO;
import com.team33.modulecore.core.cart.dto.NormalCartVO;
import com.team33.modulecore.core.cart.dto.SubscriptionCartVO;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.exception.BusinessLogicException;

@ExtendWith(MockitoExtension.class)
class MemoryCartClientTest {

    private static final String TEST_KEY = "testKey";

    @InjectMocks
    private MemoryCartClient memoryCartClient;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache(CARTS.name())).thenReturn(cache);
    }

    @Test
    @DisplayName("잘못된 타입의 장바구니를 요청하면 예외가 발생한다")
    void test1() {
        // given
        when(cache.get(eq(TEST_KEY), eq(SubscriptionCartVO.class))).thenReturn(eq(null));

        // when, then
        assertThatThrownBy(() -> memoryCartClient.getCart(TEST_KEY, SubscriptionCartVO.class))
            .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("캐시가 없으면 예외가 발생한다")
    void test2() {
        // given
        when(cacheManager.getCache(CARTS.name())).thenReturn(null);

        // when, then
        assertThatThrownBy(() -> memoryCartClient.getCart(TEST_KEY, CartVO.class))
            .isInstanceOf(BusinessLogicException.class);
    }

    @DisplayName("일반 상품을 장바구니에 추가할 수 있다")
    @Test
    void test3() {
        // given
        ItemVO itemVO = createTestItem();
        NormalCartVO normalCartVO = new NormalCartVO();
        when(cache.get(anyString(), eq(NormalCartVO.class))).thenReturn(normalCartVO);

        // when
        int quantity = 2;
        memoryCartClient.addNormalItem(TEST_KEY, itemVO, quantity);

        // then
        assertThat(normalCartVO.getCartItems()).hasSize(1);
        assertThat(normalCartVO.getCartItems().get(0).getItem()).isEqualTo(itemVO);
        assertThat(normalCartVO.getCartItems().get(0).getTotalQuantity()).isEqualTo(quantity);
    }

    @DisplayName("구독 상품을 장바구니에 추가할 수 있다")
    @Test
    void test4() {
        // given
        ItemVO item = createTestItem();
        SubscriptionCartVO subscriptionCart = new SubscriptionCartVO();
        SubscriptionContext context = createSubscriptionContext(item);
        when(cache.get(eq(TEST_KEY), eq(SubscriptionCartVO.class))).thenReturn(subscriptionCart);

        // when
        memoryCartClient.addSubscriptionItem(TEST_KEY, context);

        // then
        assertThat(subscriptionCart.getCartItems()).hasSize(1);
        assertThat(subscriptionCart.getCartItems().get(0).getItem()).isEqualTo(item);
    }

    @Test
    @DisplayName("장바구니에서 상품을 삭제할 수 있다")
    void test5() {
        // given
        NormalCartVO cart = new NormalCartVO();
        ItemVO item = createTestItem();
        CartItemVO cartItem = CartItemVO.of(item, 1);
        cart.addNormalItem(cartItem);
        when(cache.get(eq(TEST_KEY), eq(CartVO.class))).thenReturn(cart);

        // when
        memoryCartClient.deleteCartItem(TEST_KEY, item.getId());

        // then
        assertThat(cart.getCartItems()).isEmpty();
    }

    @DisplayName("존재하지 않는 장바구니에 접근하면 예외가 발생한다")
    @Test
    void test6() {
        // given
        when(cache.get(eq(TEST_KEY), eq(NormalCartVO.class))).thenReturn(eq(null));

        // when, then
        assertThatThrownBy(() -> memoryCartClient.addNormalItem(TEST_KEY, createTestItem(), 1))
            .isInstanceOf(BusinessLogicException.class);
    }

    @DisplayName("이미 장바구니에 있는 상품을 추가하면 예외가 발생한다")
    @Test
    void test7() {
        // given
        ItemVO item = createTestItem();
        NormalCartVO cart = new NormalCartVO();
        cart.addNormalItem(CartItemVO.of(item, 1));
        when(cache.get(eq(TEST_KEY), eq(NormalCartVO.class))).thenReturn(cart);

        // when, then
        assertThatThrownBy(() -> memoryCartClient.addNormalItem(TEST_KEY, item, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 장바구니에 있습니다.");
    }

    private ItemVO createTestItem() {

        return ItemVO.builder()
            .id(1L)
            .productName("testItem")
            .originPrice(1000)
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