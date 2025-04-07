package com.team33.modulecore.core.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

@ExtendWith(MockitoExtension.class)
class SubscriptionOrderServiceTest {

	@Mock
	private SubscriptionOrderRepository subscriptionOrderRepository;

	@InjectMocks
	private SubscriptionOrderService subscriptionOrderService;

	private Order mockOrder;
	private SubscriptionOrder mockSubscriptionOrder;
	private long subscriptionOrderId;
	private ZonedDateTime paymentDay;

	@BeforeEach
	void setUp() {
		subscriptionOrderId = 1L;
		paymentDay = ZonedDateTime.now();
		mockOrder = mock(Order.class);
		mockSubscriptionOrder = mock(SubscriptionOrder.class);
	}

	@DisplayName("id로 구독 주문 조회를 할 수 있다.")
	@Test
	void test2() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.of(mockSubscriptionOrder));

		// when
		SubscriptionOrder foundOrder = subscriptionOrderService.findById(subscriptionOrderId);

		// then
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		assertThat(foundOrder).isEqualTo(mockSubscriptionOrder);
	}

	@DisplayName("주어진 주문 정보로 구독 주문들을 생성하고 저장한다")
	@Test
	void test1() {
		// given
		OrderItem mockOrderItem1 = mock(OrderItem.class);
		OrderItem mockOrderItem2 = mock(OrderItem.class);

		OrderCommonInfo mockOrderCommonInfo = mock(OrderCommonInfo.class);
		doNothing().when(mockOrderCommonInfo).addPrice(anyList());

		when(mockOrder.getOrderItems()).thenReturn(List.of(mockOrderItem1, mockOrderItem2));
		when(mockOrder.getOrderCommonInfo()).thenReturn(mockOrderCommonInfo);

		ArgumentCaptor<List<SubscriptionOrder>> captor = ArgumentCaptor.forClass(List.class);
		when(subscriptionOrderRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		List<SubscriptionOrder> createdOrders = subscriptionOrderService.create(mockOrder);

		// then
		verify(mockOrder, times(1)).getOrderItems();
		verify(mockOrder, times(2)).getOrderCommonInfo();
		verify(mockOrderCommonInfo, times(2)).addPrice(anyList());

		verify(subscriptionOrderRepository, times(1)).saveAll(captor.capture());
		List<SubscriptionOrder> savedOrders = captor.getValue();
		assertThat(savedOrders).hasSize(2);
		assertThat(createdOrders).isEqualTo(savedOrders);
	}

	@DisplayName("id로 구독 주문 조회 실패 시 예외 발생")
	@Test
	void test3() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> subscriptionOrderService.findById(subscriptionOrderId))
			.isInstanceOf(BusinessLogicException.class)
			.hasMessageContaining(ExceptionCode.ORDER_NOT_FOUND.getMessage());
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
	}

	@DisplayName("존재하는 구독 주문의 주기를 변경한다")
	@Test
	void test4() {
		// given
		int newPeriod = 30;
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.of(mockSubscriptionOrder));

		// when
		subscriptionOrderService.changeItemPeriod(newPeriod, subscriptionOrderId);

		// then
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, times(1)).changePeriod(newPeriod);
	}

	@DisplayName("존재하지 않는 구독 주문의 주기를 변경 시 예외 발생")
	@Test
	void test5() {
		// given
		int newPeriod = 30;
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> subscriptionOrderService.changeItemPeriod(newPeriod, subscriptionOrderId))
			.isInstanceOf(BusinessLogicException.class)
			.hasMessageContaining(ExceptionCode.ORDER_NOT_FOUND.getMessage());
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, never()).changePeriod(anyInt());
	}

	@Test
	@DisplayName("존재하는 구독 주문의 다음 결제일을 업데이트한다")
	void test6() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.of(mockSubscriptionOrder));

		// when
		subscriptionOrderService.updateNextPaymentDate(paymentDay, subscriptionOrderId);

		// then
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, times(1)).updateSubscriptionPaymentDay(paymentDay);
	}

	@Test
	@DisplayName("존재하지 않는 구독 주문의 다음 결제일 업데이트 시 예외 발생한다")
	void test7() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> subscriptionOrderService.updateNextPaymentDate(paymentDay, subscriptionOrderId))
			.isInstanceOf(BusinessLogicException.class)
			.hasMessageContaining(ExceptionCode.ORDER_NOT_FOUND.getMessage());
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, never()).updateSubscriptionPaymentDay(any(ZonedDateTime.class));
	}

	@DisplayName("존재하는 구독 주문을 취소 상태로 변경한다")
	@Test
	void test8() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.of(mockSubscriptionOrder));

		// when
		subscriptionOrderService.cancelSubscription(subscriptionOrderId);

		// then
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, times(1)).cancelSubscription();
	}

	@DisplayName(" 존재하지 않는 구독 주문 취소 시 예외 발생")
	@Test
	void test9() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> subscriptionOrderService.cancelSubscription(subscriptionOrderId))
			.isInstanceOf(BusinessLogicException.class)
			.hasMessageContaining(ExceptionCode.ORDER_NOT_FOUND.getMessage());
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, never()).cancelSubscription();
	}

	@DisplayName(" 존재하는 구독 주문의 상태를 결제 실패로 변경한다")
	@Test
	void test10() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.of(mockSubscriptionOrder));

		// when
		subscriptionOrderService.updateOrderStatus(subscriptionOrderId);

		// then
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, times(1)).changeOrderStatus(OrderStatus.SUBSCRIBE_PAYMENT_FAIL);
	}

	@DisplayName("존재하지 않는 구독 주문 상태 변경 시 예외 발생")
	@Test
	void test11() {
		// given
		when(subscriptionOrderRepository.findById(subscriptionOrderId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> subscriptionOrderService.updateOrderStatus(subscriptionOrderId))
			.isInstanceOf(BusinessLogicException.class)
			.hasMessageContaining(ExceptionCode.ORDER_NOT_FOUND.getMessage());
		verify(subscriptionOrderRepository, times(1)).findById(subscriptionOrderId);
		verify(mockSubscriptionOrder, never()).changeOrderStatus(any(OrderStatus.class));
	}
}