package com.team33.modulecore.core.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;
import com.team33.modulecore.exception.DataSaveException;

@ExtendWith(OutputCaptureExtension.class)
class OrderPaymentTokenServiceTest {

	private OrderFindHelper orderFindHelper;
	private SubscriptionOrderRepository subscriptionOrderRepository;

	@BeforeEach
	void setUpEach() {

		orderFindHelper = mock(OrderFindHelper.class);
		subscriptionOrderRepository = mock(SubscriptionOrderRepository.class);
	}

	@DisplayName("sid를 저장할 수 있다.")
	@Test
	void addSid() {
		//given
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);

		long subscriptionOrderId = 1L;
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.ofNullable(subscriptionOrder));
		OrderPaymentCodeService orderPaymentCodeService =
			new OrderPaymentCodeService(orderFindHelper, subscriptionOrderRepository);

		String sid = "sid";

		//when
		orderPaymentCodeService.addSid(subscriptionOrderId, sid);

		//then
		verify(subscriptionOrder, times(1)).addSid(sid);

	}

	@DisplayName("tid를 넣을 수 있다.")
	@Test
	void addTid() {
		// given

		Order order = mock(Order.class);

		long orderId = 1L;
		when(orderFindHelper.findOrder(orderId)).thenReturn(order);
		OrderPaymentCodeService orderPaymentCodeService =
			new OrderPaymentCodeService(orderFindHelper, subscriptionOrderRepository);

		String tid = "tid";

		// when
		orderPaymentCodeService.addTid(orderId, tid);

		// then
		verify(orderFindHelper, times(1)).findOrder(orderId);
		verify(order, times(1)).addTid(tid);

	}

	@DisplayName("sid 저장 실패시 로그를 남기고 예외를 던진다.")
	@Test
	void sidError(CapturedOutput output) throws Exception {
		//given
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);

		long subscriptionOrderId = 1L;
		when(subscriptionOrder.getId()).thenReturn(subscriptionOrderId);
		when(subscriptionOrderRepository.findById(subscriptionOrderId)).thenReturn(Optional.of(subscriptionOrder));
		OrderPaymentCodeService orderPaymentCodeService =
			new OrderPaymentCodeService(orderFindHelper, subscriptionOrderRepository);

		String sid = "sid";
		doThrow(DataSaveException.class).when(subscriptionOrder).addSid(sid);

		//when //then
		assertThatThrownBy(() -> orderPaymentCodeService.addSid(subscriptionOrder.getId(), sid))
			.isInstanceOf(DataSaveException.class);

		assertThat(output).contains("subscriptionOrderId = 1 :: lost sid = sid");
	}

	@DisplayName("tid 저장 실패시 로그를 남기고 예외를 던진다.")
	@Test
	void tidError(CapturedOutput output) throws Exception {
		//given
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		Order order = mock(Order.class);

		long orderId = 1L;
		when(orderFindHelper.findOrder(orderId)).thenReturn(order);
		OrderPaymentCodeService orderPaymentCodeService =
			new OrderPaymentCodeService(orderFindHelper, subscriptionOrderRepository);

		String tid = "tid";
		doThrow(DataSaveException.class).when(order).addTid(tid);

		//when //then
		assertThatThrownBy(() -> orderPaymentCodeService.addTid(orderId, tid))
			.isInstanceOf(DataSaveException.class);

		assertThat(output).contains("orderId = 1 :: lost tid = tid");
	}
}