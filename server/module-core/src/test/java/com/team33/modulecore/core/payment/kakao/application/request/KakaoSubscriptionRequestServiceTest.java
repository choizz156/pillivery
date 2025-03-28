package com.team33.modulecore.core.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.request.Request;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

class KakaoSubscriptionRequestServiceTest {

	@Test
	@DisplayName("구독 주문 ID로 결제 요청을 생성할 수 있다")
	void test1() {
		// given
		var kakaoSubsRequest = mock(Request.class);
		var subscriptionOrderService = mock(SubscriptionOrderService.class);
		var kakaoSubscriptionRequestService = new KakaoSubscriptionRequestService(
			kakaoSubsRequest,
			subscriptionOrderService
		);

		long subscriptionOrderId = 1L;
		SubscriptionOrder mockSubscriptionOrder = mock(SubscriptionOrder.class);

		KakaoApiRequestResponse apiResponse = KakaoApiRequestResponse.builder()
			.tid("test_subscription_tid")
			.next_redirect_pc_url("url")
			.build();

		KakaoRequestResponse expectedResponse = KakaoRequestResponse.builder()
			.tid("test_subscription_tid")
			.next_redirect_pc_url("url")
			.build();

		when(subscriptionOrderService.findById(subscriptionOrderId)).thenReturn(mockSubscriptionOrder);
		when(kakaoSubsRequest.request(mockSubscriptionOrder)).thenReturn(apiResponse);

		// when
		KakaoRequestResponse response = kakaoSubscriptionRequestService.request(subscriptionOrderId);

		// then
		verify(subscriptionOrderService, times(1)).findById(subscriptionOrderId);
		verify(kakaoSubsRequest, times(1)).request(mockSubscriptionOrder);

		assertThat(response).isNotNull();
		assertThat(response.getTid()).isEqualTo("test_subscription_tid");
		assertThat(response.getNext_redirect_pc_url()).isEqualTo("url");
	}

}