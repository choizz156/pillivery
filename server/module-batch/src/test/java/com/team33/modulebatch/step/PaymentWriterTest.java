package com.team33.modulebatch.step;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulebatch.exception.SubscriptionFailException;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;

class PaymentWriterTest {

	private PaymentWriter paymentWriter;
	private PaymentApiDispatcher paymentApiDispatcher;
	private SubscriptionOrderService subscriptionOrderService;

	@BeforeEach
	void setUpEach() {

		paymentApiDispatcher = mock(PaymentApiDispatcher.class);
		subscriptionOrderService = mock(SubscriptionOrderService.class);

		paymentWriter = new PaymentWriter(paymentApiDispatcher, subscriptionOrderService);
	}

	@DisplayName("OrderVO 리스트를 받으면 write 메서드가 수행된다.")
	@Test
	void test1() throws Exception {
		// given
		SubscriptionOrderVO order1 = new SubscriptionOrderVO();
		SubscriptionOrderVO order2 = new SubscriptionOrderVO();
		var orders = List.of(order1, order2);

		// when
		paymentWriter.write(orders);

		// then
		verify(paymentApiDispatcher, times(1)).dispatch(orders);
	}

	@DisplayName("리스트가 비어 있을 경우 write 메서드가 작동하지 않는다.")
	@Test
	void test2() throws Exception {
		// given 
		PaymentApiDispatcher mockDispatcher = mock(PaymentApiDispatcher.class);
		PaymentWriter paymentWriter = new PaymentWriter(paymentApiDispatcher, subscriptionOrderService);

		List<SubscriptionOrderVO> emptyList = List.of();

		// when
		paymentWriter.write(emptyList);

		// then
		verifyNoInteractions(mockDispatcher);
	}


	@DisplayName("결제 실패 시 주문 상태를 업데이트한다")
	@Test
	void test3() throws Exception {
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