package com.team33.modulecore.core.payment.kakao.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedHandler;
import com.team33.modulecore.exception.DataSaveException;

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

	@DisplayName("데이터 접근 예외가 발생하면 DataSaveException으로 전환된다")
	@Test
	void test2() {
		// given
		long subscriptionOrderId = 1L;
		PaymentDateUpdatedEvent event = new PaymentDateUpdatedEvent(subscriptionOrderId);

		doThrow(new DataAccessException("DB 접근 오류") {
		}).when(subscriptionOrderService)
			.updateNextPaymentDate(event.getPaymentDay(), event.getSubscriptionOrderId());

		// when, then
		assertThatThrownBy(() -> paymentDateUpdatedHandler.onEventSet(event))
			.isInstanceOf(DataSaveException.class);
	}
}