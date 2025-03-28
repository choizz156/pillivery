package com.team33.modulecore.core.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.domain.approve.OneTimeApprove;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoOneTimeApproveServiceTest {

	@Test
	@DisplayName("일회성 결제 승인 요청 처리")
	void test1() {
		// given
		OneTimeApprove<KakaoApiApproveResponse, KakaoApproveRequest> oneTimeApprove = mock(OneTimeApprove.class);
		var kakaoOneTimeApproveService = new KakaoOneTimeApproveService(oneTimeApprove);

		long orderId = 1L;
		KakaoApproveRequest request = KakaoApproveRequest.of("tid", "test_pg_token", orderId);

		KakaoApiApproveResponse apiResponse = KakaoApiApproveResponse.builder().build();

		when(oneTimeApprove.approveOneTime(request)).thenReturn(apiResponse);

		// when
		KakaoApproveResponse response = kakaoOneTimeApproveService.approveOneTime(request);

		// then
		verify(oneTimeApprove, times(1)).approveOneTime(request);
		assertThat(response).isNotNull();
	}
}