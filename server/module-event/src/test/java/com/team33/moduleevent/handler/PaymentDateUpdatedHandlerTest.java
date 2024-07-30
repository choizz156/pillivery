package com.team33.moduleevent.handler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;
import com.team33.modulequartz.subscription.domain.PaymentDateUpdatedEvent;

class PaymentDateUpdatedHandlerTest {

	@DisplayName("다음 결제일을 저장할 수 있다.")
	@Test
	void 결제일_변경() throws Exception {
		//given
		OrderItem orderItem = OrderItem.builder()
			.subscriptionInfo(SubscriptionInfo.of(true, 30))
			.build();

		OrderItemService orderItemService = mock(OrderItemService.class);
		PaymentDateUpdatedHandler paymentDateUpdatedHandler = new PaymentDateUpdatedHandler(orderItemService);

		PaymentDateUpdatedEvent apiEvent = new PaymentDateUpdatedEvent(orderItem);

		//when
		paymentDateUpdatedHandler.onEventSet(apiEvent);

		//then
		verify(orderItemService, times(1))
			.updateNextPaymentDate(
				apiEvent.getPaymentDay(),
				orderItem
			);
	}
}