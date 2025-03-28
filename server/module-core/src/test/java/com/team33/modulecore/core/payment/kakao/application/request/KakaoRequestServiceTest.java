package com.team33.modulecore.core.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.request.Request;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

class KakaoRequestServiceTest {

	@Test
	@DisplayName("주문 ID로 결제 요청을 생성할 수 있다")
	void test1() {
		// given
		var kakaoOneTimeRequest = mock(Request.class);
		var orderFindHelper = mock(OrderFindHelper.class);
		var kakaoRequestService = new KakaoRequestService(kakaoOneTimeRequest, orderFindHelper);

		Long orderId = 1L;
		Order mockOrder = mock(Order.class);

		KakaoApiRequestResponse apiResponse = KakaoApiRequestResponse.builder()
			.tid("test_tid")
			.next_redirect_pc_url("url")
			.build();

		when(orderFindHelper.findOrder(orderId)).thenReturn(mockOrder);
		when(kakaoOneTimeRequest.request(mockOrder)).thenReturn(apiResponse);

		// when
		KakaoRequestResponse response = kakaoRequestService.request(orderId);

		// then
		verify(orderFindHelper, times(1)).findOrder(orderId);
		verify(kakaoOneTimeRequest, times(1)).request(any(Order.class));

		assertThat(response).isNotNull();
		assertThat(response.getTid()).isEqualTo("test_tid");
		assertThat(response.getNext_redirect_pc_url()).isEqualTo("url");
	}
}