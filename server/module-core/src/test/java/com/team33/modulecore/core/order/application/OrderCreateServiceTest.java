package com.team33.modulecore.core.order.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.exception.BusinessLogicException;

@ExtendWith(MockitoExtension.class)
class OrderCreateServiceTest {

    @Mock
    private OrderCommandRepository orderCommandRepository;

    @InjectMocks
    private OrderCreateService orderCreateService;

    private OrderContext orderContext;
    private List<OrderItem> orderItems;

    @BeforeEach
    void setUp() {
        orderContext = new OrderContext(
            1L,
            "testUser",
            "test@email.com",
            "010-1234-5678",
            "서울시 강남구",
            "상세주소",
            "12345"
        );
        orderItems = List.of(
            new OrderItem(1L, 2, 10000, 1),
            new OrderItem(2L, 1, 15000, 2)
        );
    }

    @Test
    void callOrder_ShouldReturnOrderWithCommonInfo() {
        // given
        Order mockOrder = Order.create(orderItems, OrderCommonInfo.createFromContext(orderItems, orderContext), orderContext);
        given(orderCommandRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        Order result = orderCreateService.callOrder(orderItems, orderContext);

        // then
        assertAll(
            () -> assertEquals(orderItems.size(), result.getOrderItems().size()),
            () -> assertEquals("testUser", result.getOrderCommonInfo().getName()),
            () -> assertEquals(2, result.getTotalQuantity())
        );
    }

    @Test
    void createOrder_ShouldGenerateValidCommonInfo() {
        // when
        Order order = orderCreateService.createOrder(orderItems, orderContext);

        // then
        assertAll(
            () -> assertNotNull(order.getOrderCommonInfo().getOrderId()),
            () -> assertEquals(35000, order.getTotalPrice()),
            () -> assertEquals("12345", order.getOrderCommonInfo().getZipCode())
        );
    }

    @Test
    void findOrder_ShouldReturnOrderWhenExists() {
        // given
        long validOrderId = 1L;
        Order expected = Order.create(orderItems, OrderCommonInfo.createFromContext(orderItems, orderContext), orderContext);
        given(orderCommandRepository.findById(validOrderId)).willReturn(java.util.Optional.of(expected));

        // when
        Order result = orderCreateService.findOrder(validOrderId);

        // then
        assertEquals(validOrderId, result.getOrderId());
    }

    @Test
    void findOrder_ShouldThrowExceptionWhenNotFound() {
        // given
        long invalidOrderId = 999L;
        given(orderCommandRepository.findById(invalidOrderId)).willReturn(java.util.Optional.empty());

        // when & then
        BusinessLogicException exception = assertThrows(BusinessLogicException.class,
            () -> orderCreateService.findOrder(invalidOrderId));
        
        assertEquals("Order not found", exception.getMessage());
    }
}