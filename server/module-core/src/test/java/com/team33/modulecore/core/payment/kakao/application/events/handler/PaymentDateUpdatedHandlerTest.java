package com.team33.modulecore.core.payment.kakao.application.events.handler;

import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;

@ExtendWith(MockitoExtension.class)
class PaymentDateUpdatedHandlerTest {

	@Mock
	private SubscriptionOrderService subscriptionOrderService;
	private PaymentDateUpdatedHandler paymentDateUpdatedHandler;

	@BeforeEach
	void setUp() {
		paymentDateUpdatedHandler = new PaymentDateUpdatedHandler(subscriptionOrderService);
	}

	@DisplayName("결제일 업데이트 이벤트가 처리된다")
	@Test
	void test1() {
		// given
		ZonedDateTime paymentDay = ZonedDateTime.now();
		long subscriptionOrderId = 1L;
		PaymentDateUpdatedEvent event = new PaymentDateUpdatedEvent(subscriptionOrderId, paymentDay);


		doNothing().when(subscriptionOrderService).updateNextPaymentDate(paymentDay, subscriptionOrderId);

		// when
		paymentDateUpdatedHandler.onEventSet(event);

		// then
		verify(subscriptionOrderService, times(1)).updateNextPaymentDate(paymentDay, subscriptionOrderId);
	}
}