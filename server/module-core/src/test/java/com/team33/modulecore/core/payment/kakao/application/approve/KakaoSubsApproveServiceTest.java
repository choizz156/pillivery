package com.team33.modulecore.core.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApprove;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionFailedEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.exception.SubscriptionPaymentException;

class KakaoSubsApproveServiceTest {

	private ApplicationEventPublisher applicationEventPublisher;
	private KakaoFirstSubsApproveDispatcher kakaoFirstSubsApproveDispatcher;
	private SubscriptionApprove<KakaoApiApproveResponse, SubscriptionOrder> subscriptionApprove;
	private KakaoSubsApproveService kakaoSubsApproveService;

	@BeforeEach
	void setUp() {

		applicationEventPublisher = mock(ApplicationEventPublisher.class);
		kakaoFirstSubsApproveDispatcher = mock(KakaoFirstSubsApproveDispatcher.class);
		subscriptionApprove = mock(SubscriptionApprove.class);

		kakaoSubsApproveService = new KakaoSubsApproveService(
				applicationEventPublisher,
				kakaoFirstSubsApproveDispatcher,
				subscriptionApprove);
	}

	@Test
	@DisplayName("최초 구독 승인 요청을 할 수 있다.")
	void test1() {
		// given
		long subscriptionOrderId = 1L;
		KakaoApproveRequest request = KakaoApproveRequest.builder()
				.tid("tid")
				.pgtoken("pg_token")
				.subscriptionOrderId(subscriptionOrderId)
				.build();

		KakaoApiApproveResponse apiResponse = new KakaoApiApproveResponse();

		when(kakaoFirstSubsApproveDispatcher.approveFirstSubscription(request)).thenReturn(apiResponse);

		ArgumentCaptor<PaymentDateUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentDateUpdatedEvent.class);

		// when
		KakaoApproveResponse response = kakaoSubsApproveService.approveInitially(request);

		// then
		assertThat(response).isNotNull();

		verify(kakaoFirstSubsApproveDispatcher, times(1)).approveFirstSubscription(request);
		verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

		PaymentDateUpdatedEvent capturedEvent = eventCaptor.getValue();
		assertThat(capturedEvent.getSubscriptionOrderId()).isEqualTo(subscriptionOrderId);

	}

	@Test
	@DisplayName("정기 구독 결제 승인 요청을 할 수 있다.")
	void test2() {
		// given
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);
		long subscriptionOrderId = 1L;
		long itemId = 1L;
		List<Long> itemIds = List.of(itemId);

		when(subscriptionOrder.getId()).thenReturn(subscriptionOrderId);
		when(subscriptionOrder.getItemId()).thenReturn(itemIds);

		KakaoApiApproveResponse apiResponse = new KakaoApiApproveResponse();

		when(subscriptionApprove.approveSubscription(subscriptionOrder)).thenReturn(apiResponse);

		// when
		KakaoApproveResponse response = kakaoSubsApproveService.approveSubscribe(subscriptionOrder);

		// then
		verify(subscriptionApprove, times(1)).approveSubscription(subscriptionOrder);
	}

	@Test
	@DisplayName("정기 구독 결제 승인 실패시 이벤트를 발행하고 예외를 던진다.")
	void test3() {
		// given
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);
		long subscriptionOrderId = 1L;
		when(subscriptionOrder.getId()).thenReturn(subscriptionOrderId);

		SubscriptionPaymentException expectedException = new SubscriptionPaymentException("결제 실패");
		when(subscriptionApprove.approveSubscription(subscriptionOrder)).thenThrow(expectedException);

		ArgumentCaptor<SubscriptionFailedEvent> eventCaptor = ArgumentCaptor.forClass(SubscriptionFailedEvent.class);

		// when & then
		assertThatThrownBy(() -> kakaoSubsApproveService.approveSubscribe(subscriptionOrder))
				.isInstanceOf(SubscriptionPaymentException.class)
				.hasMessage("결제 실패");

		verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());
		SubscriptionFailedEvent capturedEvent = eventCaptor.getValue();
		assertThat(capturedEvent.getSubscriptionOrderId()).isEqualTo(subscriptionOrderId);
	}

}