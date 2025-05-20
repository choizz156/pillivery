package com.team33.modulecore.core.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KaKaoApproveTemplateTest {

	private PaymentClient<KakaoApiApproveResponse> kakaoApproveClient;
	private KaKaoApproveTemplate kakaoApproveTemplate;

	@BeforeEach
	void setUp() {

		kakaoApproveClient = mock(PaymentClient.class);

		kakaoApproveTemplate = spy(new KaKaoApproveTemplate(kakaoApproveClient) {
			@Override
			public Map<String, Object> getApproveParams(ApproveRequest approveRequest) {

				return new HashMap<>();
			}
		});
	}

	@Test
	@DisplayName("승인 파라미터 생성 및 요청 전송 검증")
	void test1() {
		// given
		long orderId = 1L;
		ApproveRequest request = KakaoApproveRequest.of("tid", "pg_token", orderId);

		Map<String, Object> params = new HashMap<>();
		params.put("pg_token", "pg_token");
		params.put("order_id", 1L);

		KakaoApiApproveResponse approveResponse = new KakaoApiApproveResponse();

		// spy의 메서드 동작 정의
		when(kakaoApproveTemplate.getApproveParams(request)).thenReturn(params);

		when(kakaoApproveClient.send(
			eq(params),
			eq("https://open-api.kakaopay.com/online/v1/payment/approve"))
		)
			.thenReturn(approveResponse);

		// when
		KakaoApiApproveResponse response = kakaoApproveTemplate.approve(request);

		// then
		verify(kakaoApproveTemplate, times(1)).getApproveParams(request);
		verify(kakaoApproveClient, times(1)).send(
			eq(params),
			eq("https://open-api.kakaopay.com/online/v1/payment/approve")
		);
		assertThat(response).isEqualTo(approveResponse);
	}

}