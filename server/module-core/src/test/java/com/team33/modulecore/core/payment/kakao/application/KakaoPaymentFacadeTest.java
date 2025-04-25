package com.team33.modulecore.core.payment.kakao.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.domain.request.RequestService;
import com.team33.modulecore.core.payment.kakao.application.request.KakaoRequestService;
import com.team33.modulecore.core.payment.kakao.application.request.KakaoSubscriptionRequestService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;

class KakaoPaymentFacadeTest {

	private SubscriptionApproveService<KakaoApproveResponse, SubscriptionOrder> kakaoSubsApproveService;
	private OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private SubscriptionOrderService subscriptionOrderService;
	private ApplicationEventPublisher eventPublisher;
	private RequestService<KakaoRequestResponse, Long> kakaoRequestService;
	private RequestService<KakaoRequestResponse, Long> kakaoSubscriptionRequestService;
	private KakaoPaymentFacade kakaoPaymentFacade;

	@BeforeEach
	void setUp() {
		kakaoSubsApproveService = mock(SubscriptionApproveService.class);
		kakaoOneTimeApproveService = mock(OneTimeApproveService.class);
		subscriptionOrderService = mock(SubscriptionOrderService.class);
		kakaoRequestService = mock(KakaoRequestService.class);
		kakaoSubscriptionRequestService = mock(KakaoSubscriptionRequestService.class);
		kakaoPaymentFacade = new KakaoPaymentFacade(
			kakaoSubsApproveService,
			kakaoOneTimeApproveService,
			subscriptionOrderService,
			kakaoRequestService,
			kakaoSubscriptionRequestService
		);
	}

	@Test
	@DisplayName("최초 구독 승인 시 이벤트 발행한다.")
	void test1() {
		// given
		long orderId = 1L;
		KakaoApproveRequest request = KakaoApproveRequest.of("tid", "pgToken", orderId);
		Order order = mock(Order.class);
		KakaoApproveResponse approveResponse = new KakaoApproveResponse();

		when(order.isSubscription()).thenReturn(true);
		when(order.getId()).thenReturn(orderId);
		when(kakaoOneTimeApproveService.approveOneTime(request)).thenReturn(approveResponse);


		// when
		KakaoApproveResponse response = kakaoPaymentFacade.approveInitially(request);

		// then
		assertThat(response).isEqualTo(approveResponse);
	}

	@Test
	@DisplayName("구독 결제 승인 처리")
	void test2() {
		// given
		long subscriptionOrderId = 1L;
		SubscriptionOrder subscriptionOrder = mock(SubscriptionOrder.class);
		KakaoApproveResponse expectedResponse = new KakaoApproveResponse();

		when(subscriptionOrderService.findById(subscriptionOrderId)).thenReturn(subscriptionOrder);
		when(kakaoSubsApproveService.approveSubscribe(subscriptionOrder)).thenReturn(expectedResponse);

		// when
		KakaoApproveResponse response = kakaoPaymentFacade.approveSubscription(subscriptionOrderId);

		// then
		verify(subscriptionOrderService, times(1)).findById(subscriptionOrderId);
		verify(kakaoSubsApproveService, times(1)).approveSubscribe(subscriptionOrder);
		assertThat(response).isEqualTo(expectedResponse);
	}

	@Test
	@DisplayName("sid를 받기 위해 승인 요청을 보낼 수 있다.")
	void test3() {
		// given
		long orderId = 1L;
		KakaoApproveRequest request = KakaoApproveRequest.of("tid", "pgToken", orderId);
		KakaoApproveResponse expectedResponse = new KakaoApproveResponse();

		when(kakaoSubsApproveService.approveInitially(request)).thenReturn(expectedResponse);

		// when
		KakaoApproveResponse response = kakaoPaymentFacade.approveSid(request);

		// then
		verify(kakaoSubsApproveService, times(1)).approveInitially(request);
		assertThat(response).isEqualTo(expectedResponse);
	}

}