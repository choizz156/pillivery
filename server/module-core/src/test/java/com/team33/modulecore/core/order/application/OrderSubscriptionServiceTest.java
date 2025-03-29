package com.team33.modulecore.core.order.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.exception.BusinessLogicException;


class OrderSubscriptionServiceTest {

    private OrderFindHelper orderFindHelper;
    private OrderSubscriptionService orderSubscriptionService;

    @BeforeEach
    void setUp() {
        orderFindHelper = mock(OrderFindHelper.class);
        orderSubscriptionService = new OrderSubscriptionService(orderFindHelper);
    }

    @DisplayName("구독 상품의 수량을 변경할 수 있다")
    @Test
    void test1() throws Exception {
        // given
        long orderId = 1L;
        long orderItemId = 10L;
        int newQuantity = 3;

        Order order = mock(Order.class);
        OrderItem orderItem = mock(OrderItem.class);
        List<OrderItem> orderItems = List.of(orderItem);

        when(orderFindHelper.findOrder(orderId)).thenReturn(order);
        when(order.getOrderItems()).thenReturn(orderItems);
        when(orderItem.getId()).thenReturn(orderItemId);

        // when
        orderSubscriptionService.changeSubscriptionItemQuantity(orderId, orderItemId, newQuantity);

        // then
        verify(orderFindHelper, times(1)).findOrder(orderId);
        verify(order, times(1)).getOrderItems();
        verify(orderItem, times(1)).changeQuantity(newQuantity);
        verify(order, times(1)).adjustPriceAndTotalQuantity(orderItems);
    }

    @DisplayName("존재하지 않는 주문 아이템의 수량 변경 시 예외가 발생한다")
    @Test
    void test2() throws Exception {
        // given
        long orderId = 1L;
        long nonExistentOrderItemId = 999L;
        int newQuantity = 3;

        Order order = mock(Order.class);
        OrderItem orderItem = mock(OrderItem.class);
        List<OrderItem> orderItems = List.of(orderItem);

        when(orderFindHelper.findOrder(orderId)).thenReturn(order);
        when(order.getOrderItems()).thenReturn(orderItems);
        when(orderItem.getId()).thenReturn(10L); // 다른 ID로 설정

        // when & then
        assertThatThrownBy(() ->
            orderSubscriptionService.changeSubscriptionItemQuantity(orderId, nonExistentOrderItemId, newQuantity)
        )
            .isInstanceOf(BusinessLogicException.class);

    }

    @DisplayName("여러 주문 아이템 중 특정 아이템의 수량만 변경할 수 있다")
    @Test
    void test3() throws Exception {
        // given
        long orderId = 1L;
        long targetOrderItemId = 20L;
        int newQuantity = 5;

        Order order = mock(Order.class);
        OrderItem orderItem1 = mock(OrderItem.class);
        OrderItem orderItem2 = mock(OrderItem.class);

        List<OrderItem> orderItems = List.of(orderItem1,orderItem2);

        when(orderFindHelper.findOrder(orderId)).thenReturn(order);
        when(order.getOrderItems()).thenReturn(orderItems);
        when(orderItem1.getId()).thenReturn(10L);
        when(orderItem2.getId()).thenReturn(targetOrderItemId);

        // when
        orderSubscriptionService.changeSubscriptionItemQuantity(orderId, targetOrderItemId, newQuantity);

        // then
        verify(orderItem1, never()).changeQuantity(anyInt());
        verify(orderItem2, times(1)).changeQuantity(newQuantity);
        verify(order, times(1)).adjustPriceAndTotalQuantity(orderItems);
    }
}