package com.team33.moduleevent.handler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.DataAccessException;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;
import com.team33.modulecore.exception.DataSaveException;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class SubscriptionRegisteredHandlerTest {

	@Mock
	private EventRepository eventRepository;
	@Mock
	private SubscriptionOrderService subscriptionOrderService;
	@Mock
	private SubscriptionOrderRepository subscriptionOrderRepository;

	@Mock
	private OrderFindHelper orderFindHelper;

	private SubscriptionRegisteredHandler subscriptionRegisteredHandler;

	@BeforeEach
	void setUp() {
		subscriptionRegisteredHandler = new SubscriptionRegisteredHandler(
			subscriptionOrderService,
			eventRepository,
			orderFindHelper
		);
	}

	@DisplayName("구독 등록 이벤트가 발생하면 구독 주문을 생성하고 ApiEvent를 저장한다")
	@Test
	void 구독_등록_이벤트_처리_및_ApiEvent_저장() {
		// given
		Long orderId = 1L;
		SubscriptionRegisteredEvent event = new SubscriptionRegisteredEvent(orderId);

		Order order = mock(Order.class);
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);
		when(subscriptionOrder.getId()).thenReturn(101L);

		when(orderFindHelper.findOrder(orderId)).thenReturn(order);
		when(subscriptionOrderService.create(order)).thenReturn(List.of(subscriptionOrder));

		// when
		subscriptionRegisteredHandler.onEventSet(event);

		// then
		verify(orderFindHelper).findOrder(orderId);
		verify(subscriptionOrderService).create(order);

		ArgumentCaptor<ApiEvent> apiEventCaptor = ArgumentCaptor.forClass(ApiEvent.class);
		verify(eventRepository).save(apiEventCaptor.capture());

		ApiEvent capturedEvent = apiEventCaptor.getValue();
		assertThat(capturedEvent.getType()).isEqualTo(EventType.SUBSCRIPTION_REGISTERED);
		assertThat(capturedEvent.getStatus()).isEqualTo(EventStatus.READY);
		assertThat(capturedEvent.getContentType()).isEqualTo("String");
		assertThat(capturedEvent.getParameters()).isEqualTo("101");
		assertThat(capturedEvent.getUrl()).isEqualTo(
			SubscriptionRegisteredHandler.HOST + "/api/payments/subscriptionsFirst/");
		assertThat(capturedEvent.getCreatedAt()).isNotNull();
	}

	@DisplayName("여러 구독 주문이 생성되면 각각에 대해 ApiEvent가 저장된다")
	@Test
	void 여러_구독_주문_생성_시_ApiEvent_저장() {
		// given
		Long orderId = 1L;
		SubscriptionRegisteredEvent event = new SubscriptionRegisteredEvent(orderId);

		Order order = mock(Order.class);
		SubscriptionOrder subscriptionOrder1 = mock(SubscriptionOrder.class);
		SubscriptionOrder subscriptionOrder2 = mock(SubscriptionOrder.class);
		when(subscriptionOrder1.getId()).thenReturn(101L);
		when(subscriptionOrder2.getId()).thenReturn(102L);

		when(orderFindHelper.findOrder(orderId)).thenReturn(order);
		when(subscriptionOrderService.create(order)).thenReturn(List.of(subscriptionOrder1, subscriptionOrder2));

		// when
		subscriptionRegisteredHandler.onEventSet(event);

		// then
		verify(orderFindHelper).findOrder(orderId);
		verify(subscriptionOrderService).create(order);

		verify(eventRepository, times(2)).save(any(ApiEvent.class));
	}

	@DisplayName("이벤트 저장 중 예외가 발생하면 DataSaveException으로 전환되고 로그가 남는다")
	@Test
	void test3(CapturedOutput capturedOutput) {
		// given
		Long orderId = 1L;
		SubscriptionRegisteredEvent event = new SubscriptionRegisteredEvent(orderId);

		Order order = mock(Order.class);
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);
		when(subscriptionOrder.getId()).thenReturn(101L);

		when(orderFindHelper.findOrder(orderId)).thenReturn(order);
		when(subscriptionOrderService.create(order)).thenReturn(List.of(subscriptionOrder));

		String errorMessage = "DB 접근 오류";
		doThrow(new DataAccessException(errorMessage) {
		})
			.when(eventRepository).save(any(ApiEvent.class));

		assertThatThrownBy(() -> subscriptionRegisteredHandler.onEventSet(event))
			.isInstanceOf(DataSaveException.class);

		assertThat(capturedOutput.toString())
			.contains("정기 구독 승인 이벤트 저장 실패")
			.contains("subscriptionOrderId=101")
			.contains(errorMessage);
	}

	@DisplayName("동시에 같은 구독 등록 이벤트가 발생해도 한번만 처리되어야 한다")
	@Test
	void test4() throws InterruptedException {
		// given
		int threadCount = 3;
		CountDownLatch latch = new CountDownLatch(threadCount);

		Long orderId = 1L;
		SubscriptionRegisteredEvent event = new SubscriptionRegisteredEvent(orderId);

		Order order = mock(Order.class);
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);
		when(subscriptionOrder.getId()).thenReturn(101L);

		when(orderFindHelper.findOrder(orderId)).thenReturn(order);
		when(subscriptionOrderService.create(order)).thenReturn(List.of(subscriptionOrder));

		ApiEvent testEvent = ApiEvent.builder().url("test").build();
		when(eventRepository.findByTypeAndParameters(any(EventType.class), anyString()))
			.thenReturn(Optional.empty())
			.thenReturn(Optional.of(testEvent))
			.thenReturn(Optional.of(testEvent));

		// when
		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				try {
					subscriptionRegisteredHandler.onEventSet(event);
				} catch (Exception ignored) {
				} finally {
					latch.countDown();
				}
			}).start();
		}

		latch.await();

		// then
		verify(orderFindHelper, times(3)).findOrder(orderId);
		verify(subscriptionOrderService, times(3)).create(order);
		verify(eventRepository, times(1)).save(any(ApiEvent.class));
	}
}