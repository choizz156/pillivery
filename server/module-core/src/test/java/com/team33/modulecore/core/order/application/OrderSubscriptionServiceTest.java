package com.team33.modulecore.core.order.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.exception.BusinessLogicException;

@ExtendWith(MockitoExtension.class)
class OrderSubscriptionServiceTest {

    @Mock
    private OrderFindHelper orderFindHelper;

    @InjectMocks
    private OrderSubscriptionService orderSubscriptionService;

    @Test
    void changeSubscriptionItemQuantity_ShouldUpdateQuantity() {
        // given
        Order mockOrder = new Order();
        OrderItem item = new OrderItem(1L, 2, 10000, 1);
        mockOrder.setOrderItems(List.of(item));
        
        given(orderFindHelper.findOrder(anyLong())).willReturn(mockOrder);

        // when
        orderSubscriptionService.changeSubscriptionItemQuantity(1L, 1L, 5);

        // then
        assertEquals(5, item.getQuantity());
        assertEquals(5, mockOrder.getTotalQuantity());
        assertEquals(50000, mockOrder.getTotalPrice());
    }

    @Test
    void changeSubscriptionItemQuantity_ShouldThrowWhenItemNotFound() {
        // given
        Order mockOrder = new Order();
        mockOrder.setOrderItems(List.of());
        given(orderFindHelper.findOrder(anyLong())).willReturn(mockOrder);

        // when & then
        assertThrows(BusinessLogicException.class, () -> {
            orderSubscriptionService.changeSubscriptionItemQuantity(1L, 999L, 5);
        });
    }
}