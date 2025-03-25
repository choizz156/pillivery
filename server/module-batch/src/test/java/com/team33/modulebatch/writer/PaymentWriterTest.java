package com.team33.modulebatch.writer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.infra.PaymentApiDispatcher;

class PaymentWriterTest {

	@DisplayName("OrderVO 리스트를 받으면 write 메서드가 수행된다.")
	@Test
	void test1() throws Exception {
		// given
		PaymentApiDispatcher mockDispatcher = mock(PaymentApiDispatcher.class);
		PaymentWriter paymentWriter = new PaymentWriter(mockDispatcher);

		OrderVO order1 = new OrderVO();
		OrderVO order2 = new OrderVO();
		var orders = List.of(order1, order2);

		// when
		paymentWriter.write(orders);

		// then
		verify(mockDispatcher, times(1)).dispatch(orders);
	}

	@DisplayName("리스트가 비어 있을 경우 write 메서드가 작동하지 않는다.")
	@Test
	void test2() throws Exception {
		// given 
		PaymentApiDispatcher mockDispatcher = mock(PaymentApiDispatcher.class);
		PaymentWriter paymentWriter = new PaymentWriter(mockDispatcher);

		List<OrderVO> emptyList = List.of();

		// when
		paymentWriter.write(emptyList);

		// then
		verifyNoInteractions(mockDispatcher);
	}

}