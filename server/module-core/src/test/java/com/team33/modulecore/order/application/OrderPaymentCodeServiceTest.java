package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.exception.DataSaveException;
import com.team33.modulecore.core.order.domain.entity.Order;

@ExtendWith(OutputCaptureExtension.class)
class OrderPaymentCodeServiceTest {

	@DisplayName("sid를 저장할 수 있다.")
	@Test
	void addSid() {
		//given
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		Order order = mock(Order.class);

		when(orderFindHelper.findOrder(1L)).thenReturn(order);
		OrderPaymentCodeService orderPaymentCodeService = new OrderPaymentCodeService(orderFindHelper);

		//when//then
		assertDoesNotThrow(() -> orderPaymentCodeService.addSid(order.getId(), "sid"));
	}

	@DisplayName("tid를 넣을 수 있다.")
	@Test
	void addTid() {
		// given
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		Order order = mock(Order.class);

		when(orderFindHelper.findOrder(1L)).thenReturn(order);
		OrderPaymentCodeService orderPaymentCodeService = new OrderPaymentCodeService(orderFindHelper);

		//when //then
		assertDoesNotThrow(() -> orderPaymentCodeService.addTid(1L, "tid"));
	}

	@DisplayName("sid 저장 실패시 로그를 남기고 예외를 던진다.")
	@Test
	void sidError(CapturedOutput output) throws Exception {
		//given
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		Order order = mock(Order.class);

		when(order.getId()).thenReturn(1L);
		when(orderFindHelper.findOrder(1L)).thenReturn(order);
		OrderPaymentCodeService orderPaymentCodeService = new OrderPaymentCodeService(orderFindHelper);

		doThrow(DataSaveException.class).when(order).addSid("sid");


		//when //then
		assertThatThrownBy(() -> orderPaymentCodeService.addSid(order.getId(), "sid"))
			.isInstanceOf(DataSaveException.class);

		assertThat(output).contains("orderId = 1 :: lost sid = sid");
	}

	@DisplayName("tid 저장 실패시 로그를 남기고 예외를 던진다.")
	@Test
	void tidError(CapturedOutput output) throws Exception {
		//given
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		Order order = mock(Order.class);

		when(orderFindHelper.findOrder(1L)).thenReturn(order);
		OrderPaymentCodeService orderPaymentCodeService = new OrderPaymentCodeService(orderFindHelper);

		doThrow(DataSaveException.class).when(order).addTid("tid");


		//when //then
		assertThatThrownBy(() -> orderPaymentCodeService.addTid(1L, "tid"))
			.isInstanceOf(DataSaveException.class);

		assertThat(output).contains("orderId = 1 :: lost tid = tid");
	}
}