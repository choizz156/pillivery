package com.team33.modulecore.core.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

class KakaoOneTimeRequestDispatcherTest {

	@Test
	@DisplayName("단건 결제 요청을 보낼 수 있다.")
	void test1() {
		// given
		var kakaoRequestClient = mock(PaymentClient.class);
		var parameterProvider = mock(ParameterProvider.class);
		var kakaoOneTimeRequestDispatcher = new KakaoOneTimeRequestDispatcher(kakaoRequestClient, parameterProvider);

		Order order = mock(Order.class);

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(Params.CID.getValue(), "TC0ONETIME");
		requestParams.put(Params.PARTNER_ORDER_ID.getValue(), "123");
		requestParams.put(Params.PARTNER_USER_ID.getValue(), "user123");
		requestParams.put(Params.ITEM_NAME.getValue(), "Test Item");
		requestParams.put(Params.QUANTITY.getValue(), "1");
		requestParams.put(Params.TOTAL_AMOUNT.getValue(), "10000");

		KakaoApiRequestResponse expectedResponse = KakaoApiRequestResponse.builder()
			.tid("test_tid")
			.next_redirect_pc_url("url")
			.build();

		when(parameterProvider.getOneTimePaymentRequestParams(order)).thenReturn(requestParams);
		when(kakaoRequestClient.send(anyMap(), any())).thenReturn(expectedResponse);

		// when
		KakaoApiRequestResponse response = kakaoOneTimeRequestDispatcher.request(order);

		// then
		verify(parameterProvider, times(1)).getOneTimePaymentRequestParams(order);
		verify(kakaoRequestClient, times(1)).send(anyMap(), any());

		assertThat(response).isNotNull();
		assertThat(response.getTid()).isEqualTo("test_tid");
		assertThat(response.getNext_redirect_pc_url()).isEqualTo("url");
	}
}