package com.team33.modulecore.core.payment.kakao.application.events.handler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionFailedEvent;

class SubscriptionFailedHandlerTest {

    private SubscriptionOrderService subscriptionOrderService;
    private SubscriptionFailedHandler subscriptionFailedHandler;

    @BeforeEach
    void setUp() {
        subscriptionOrderService = mock(SubscriptionOrderService.class);
        subscriptionFailedHandler = new SubscriptionFailedHandler(subscriptionOrderService);
    }

    @Test
    @DisplayName(" 정기 결제 실패 이벤트 처리 성공")
    void test1() {
        // given
        Long subscriptionOrderId = 1L;
        SubscriptionFailedEvent event = new SubscriptionFailedEvent(subscriptionOrderId);
        doNothing().when(subscriptionOrderService).updateOrderStatusToFail(subscriptionOrderId);

        // when
        subscriptionFailedHandler.onEventSet(event);

        // then
        verify(subscriptionOrderService, times(1)).updateOrderStatusToFail(subscriptionOrderId);
    }
}