package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoOneTimeApprove;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoOneTimeApproveTest {

	@DisplayName("일반 결제 승인을 요청할 수 있다.")
	@Test
	void 승인_요청() throws Exception {
		//given
		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoOneTimeApprove kakaoNormalApprove =
			new KakaoOneTimeApprove(
				(params, url) -> new KakaoApiApproveResponse(),
				parameterProvider
			);

		ApproveRequest request = KakaoApproveRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApiApproveResponse approve = kakaoNormalApprove.approve(request);

		//then
		assertThat(approve).isNotNull();
	}
}