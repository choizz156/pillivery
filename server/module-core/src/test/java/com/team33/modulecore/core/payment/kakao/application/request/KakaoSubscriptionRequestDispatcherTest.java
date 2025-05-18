package com.team33.modulecore.core.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

class KakaoSubscriptionRequestDispatcherTest {

	@Test
	@DisplayName("구독 주문 정보로 결제 요청을 생성할 수 있다")
	void test1() {
		// given
		PaymentClient<KakaoApiRequestResponse> kakaoRequestClient = mock(PaymentClient.class);
		var parameterProvider = mock(ParameterProvider.class);
		var kakaoSubsRequestDispatcher = new KakaoSubscriptionRequestDispatcher(kakaoRequestClient, parameterProvider);
		var subscriptionOrder = mock(SubscriptionOrder.class);

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(Params.CID.getValue(), "TCSUBSCRIP");
		requestParams.put(Params.PARTNER_ORDER_ID.getValue(), "123");
		requestParams.put(Params.PARTNER_USER_ID.getValue(), "user123");
		requestParams.put(Params.ITEM_NAME.getValue(), "구독 상품");
		requestParams.put(Params.QUANTITY.getValue(), "1");
		requestParams.put(Params.TOTAL_AMOUNT.getValue(), "10000");

		KakaoApiRequestResponse expectedResponse = KakaoApiRequestResponse.builder()
			.tid("test_subscription_tid")
			.next_redirect_pc_url("url")
			.build();

		when(parameterProvider.getSubscriptionPaymentRequestParams(subscriptionOrder)).thenReturn(requestParams);
		when(kakaoRequestClient.send(anyMap(), any())).thenReturn(expectedResponse);

		// when
		KakaoApiRequestResponse response = kakaoSubsRequestDispatcher.request(subscriptionOrder);

		// then
		verify(parameterProvider, times(1)).getSubscriptionPaymentRequestParams(subscriptionOrder);
		verify(kakaoRequestClient, times(1)).send(anyMap(), any());

		assertThat(response).isNotNull();
		assertThat(response.getTid()).isEqualTo("test_subscription_tid");
		assertThat(response.getNext_redirect_pc_url()).isEqualTo("url");
	}
}