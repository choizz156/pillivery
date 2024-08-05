package com.team33.modulecore.payment.kakao.application.refund;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.modulecore.core.payment.kakao.application.refund.KakaoRefundService;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;

class KakaoRefundServiceTest {

	@DisplayName("환불 요청을 위임할 수 있다.")
	@Test
	void 환불_요청_위임() throws Exception{
		//given
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		when(orderFindHelper.findTid(anyLong())).thenReturn("tid");


		RefundContext context = RefundContext.builder()
			.cancelAmount(1000)
			.cancelTaxFreeAmount(0)
			.build();


		KakaoRefundService kakaoRefundService =
			new KakaoRefundService(
				applicationContext,
				new ParameterProvider(),
				orderFindHelper,
				new ObjectMapper()
			);

		//when
		 kakaoRefundService.refund(1L, context);

		//then
		verify(orderFindHelper, times(1)).findTid(anyLong());
		verify(applicationContext, times(1)).publishEvent(any(KakaoRefundedEvent.class));
	}
}