package com.team33.modulebatch.step;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulebatch.exception.SubscriptionFailException;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;

class PaymentWriterTest {

	@DisplayName("결제 실패 시 주문 상태를 업데이트한다")
	@Test
	void testExceptionHandling() throws Exception {
		// given
		PaymentApiDispatcher mockDispatcher = mock(PaymentApiDispatcher.class);
		SubscriptionOrderService mockService = mock(SubscriptionOrderService.class);
		PaymentWriter paymentWriter = new PaymentWriter(mockDispatcher, mockService);

		List<SubscriptionOrderVO> orders = List.of(new SubscriptionOrderVO());
		SubscriptionFailException testException = new SubscriptionFailException("결제 실패", 999L);

		doThrow(testException).when(mockDispatcher).dispatch(orders);

		// when
		paymentWriter.write(orders);

		// then
		verify(mockService).updateOrderStatus(999L);
	}
}