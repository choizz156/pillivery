package com.team33.modulecore.core.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.exception.BusinessLogicException;

@ExtendWith(MockitoExtension.class)
class OrderCreateServiceTest {

    private OrderCommandRepository orderCommandRepository;
    private OrderCreateService orderCreateService;

    @BeforeEach
    void setUp() {
        orderCommandRepository = mock(OrderCommandRepository.class);
        orderCreateService = new OrderCreateService(orderCommandRepository);
    }

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void test1() {
        OrderItem orderItem = mock(OrderItem.class);
        List<OrderItem> orderItems = List.of(orderItem);

        OrderContext orderContext = mock(OrderContext.class);
        Order expectedOrder = mock(Order.class);

        when(orderCommandRepository.save(any(Order.class))).thenReturn(expectedOrder);

        // when
        Order result = orderCreateService.callOrder(orderItems, orderContext);

        // then
        verify(orderCommandRepository, times(1)).save(any(Order.class));
        assertThat(result).isEqualTo(expectedOrder);
    }

    @Test
    @DisplayName("존재하지 않는 주문 id로 조회 시 예외가 발생한다")
    void test3() {
        // given
        long orderId = 999L;

        when(orderCommandRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderCreateService.findOrder(orderId))
            .isInstanceOf(BusinessLogicException.class);

    }
}