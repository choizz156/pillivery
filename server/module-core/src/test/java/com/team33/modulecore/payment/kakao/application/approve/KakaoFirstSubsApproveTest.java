package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoFirstSubsApprove;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoFirstSubsApproveTest {

	@DisplayName("첫 정기 결제 승인 요청을 할 수 있다.")
	@Test
	void 최초_정기_승인() throws Exception{

		//given
		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoFirstSubsApprove kakaoFirstSubsApprove = new KakaoFirstSubsApprove(
			(params, url) -> new KakaoApiApproveResponse(),
			parameterProvider
		);

		//when
		KakaoApproveOneTimeRequest request = KakaoApproveOneTimeRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();
		KakaoApiApproveResponse kaKaoApiApproveResponse = kakaoFirstSubsApprove.approveFirstSubscription(request);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

}