package com.team33.modulecore.core.payment.kakao.application.refund;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.modulecore.core.payment.kakao.application.request.Params;

class KakaoRefundServiceTest {

	@DisplayName("환불 요청을 위임할 수 있다.")
	@Test
	void 환불_요청_위임() throws Exception {
		//given
		var applicationEventPublisher = mock(ApplicationEventPublisher.class);
		var parameterProvider = mock(ParameterProvider.class);
		var orderFindHelper = mock(OrderFindHelper.class);

		String testTid = "test_tid";

		Map<String, Object> refundParams = new HashMap<>();
		refundParams.put(Params.TID.getValue(), testTid);
		refundParams.put(Params.CANCEL_AMOUNT.getValue(), "10000");
		refundParams.put(Params.CANCEL_TAX_FREE_AMOUNT.getValue(), "0");

		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(refundParams);

		RefundContext context = RefundContext.builder()
			.cancelAmount(10000)
			.cancelTaxFreeAmount(0)
			.build();

		long orderId = 1L;
		when(orderFindHelper.findTid(orderId)).thenReturn(testTid);
		when(parameterProvider.getPaymentRefundParams(context, testTid)).thenReturn(refundParams);
		when(objectMapper.writeValueAsString(refundParams)).thenReturn(params);

		KakaoRefundService kakaoRefundService =
			new KakaoRefundService(
				applicationEventPublisher,
				new ParameterProvider(),
				orderFindHelper,
				objectMapper
			);

		ArgumentCaptor<KakaoRefundedEvent> eventCaptor = ArgumentCaptor.forClass(KakaoRefundedEvent.class);

		//when
		kakaoRefundService.refund(orderId, context);

		//then
		verify(orderFindHelper, times(1)).findTid(orderId);
		verify(parameterProvider, times(1)).getPaymentRefundParams(context, testTid);
		verify(objectMapper, times(1)).writeValueAsString(refundParams);
		verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

		KakaoRefundedEvent capturedEvent = eventCaptor.getValue();
		assertThat(capturedEvent.getRefundParams()).isEqualTo(params);
		assertThat(capturedEvent.getRefundUrl()).isEqualTo("https://open-api.kakaopay.com/online/v1/payment/cancel");

	}
}