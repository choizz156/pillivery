package com.team33.moduleevent.handler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;

class PaymentDateUpdatedHandlerTest {

	@DisplayName("다음 결제일을 저장할 수 있다.")
	@Test
	void 결제일_변경() throws Exception {
		//given

		OrderItemService orderItemService = mock(OrderItemService.class);
		PaymentDateUpdatedHandler paymentDateUpdatedHandler = new PaymentDateUpdatedHandler(orderItemService);

		PaymentDateUpdatedEvent apiEvent = new PaymentDateUpdatedEvent(1L);

		//when
		paymentDateUpdatedHandler.onEventSet(apiEvent);

		//then
		verify(orderItemService, times(1))
			.updateNextPaymentDate(
				apiEvent.getPaymentDay(),
				1L
			);
	}
}