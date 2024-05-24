package com.team33.modulecore.payment.kakao.application.refund;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;

class KakaoRefundServiceTest {

	@DisplayName("환불 요청을 위임할 수 있다.")
	@Test
	void 환불_요청_위임() throws Exception{
		//given
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		when(orderFindHelper.findTid(anyLong())).thenReturn("tid");

		RefundContext context = RefundContext.builder()
			.orderId(1L)
			.cancelAmount(1000)
			.cancelTaxFreeAmount(0)
			.build();

		KakaoRefundService kakaoRefundService =
			new KakaoRefundService((params, url) -> new KakaoRefundResponse(), new ParameterProvider(), orderFindHelper);
		//when
		KakaoRefundResponse refund = kakaoRefundService.refund(context);

		//then
		verify(orderFindHelper, times(1)).findTid(anyLong());
		assertThat(refund).isNotNull();
	}
}