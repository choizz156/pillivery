package com.team33.modulecore.core.payment.kakao.application.refund;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.events.KakaoSubscriptionCanceledEvent;
import com.team33.modulecore.core.payment.kakao.application.request.Params;

class KakaoSubscriptionCancelServiceTest {

	@Test
	@DisplayName("구독 주문으로 구독 취소 요청을 생성할 수 있다")
	void test1() throws JsonProcessingException {
		// given

		var applicationEventPublisher = mock(ApplicationEventPublisher.class);
		var parameterProvider = mock(ParameterProvider.class);
		var subscriptionOrder = mock(SubscriptionOrder.class);
		String sid = "sid";

		Map<String, Object> cancelParams = new HashMap<>();
		cancelParams.put(Params.SID.getValue(), sid);

		when(subscriptionOrder.getSid()).thenReturn(sid);
		when(subscriptionOrder.getId()).thenReturn(1L);
		when(parameterProvider.getSubsCancelParams(sid)).thenReturn(cancelParams);

		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(cancelParams);

		KakaoSubscriptionCancelService kakaoSubscriptionCancelService = new KakaoSubscriptionCancelService(
			applicationEventPublisher,
			parameterProvider,
			objectMapper
		);

		ArgumentCaptor<KakaoSubscriptionCanceledEvent> eventCaptor = ArgumentCaptor.forClass(
			KakaoSubscriptionCanceledEvent.class);

		// when
		kakaoSubscriptionCancelService.cancelSubscription(subscriptionOrder);

		// then
		verify(subscriptionOrder, times(1)).getSid();
		verify(parameterProvider, times(1)).getSubsCancelParams(sid);
		verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

		KakaoSubscriptionCanceledEvent capturedEvent = eventCaptor.getValue();
		assertThat(capturedEvent.getCancelParam()).isEqualTo(params);
		assertThat(capturedEvent.getCancelUrl()).isEqualTo(
			"https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive"
		);
	}
}