package com.team33.modulecore.core.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoFirstSubsApproveDispatcherTest {

	@DisplayName("첫 정기 결제 승인 요청을 할 수 있다.")
	@Test
	void 최초_정기_승인() throws Exception{

		//given
		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoFirstSubsApproveDispatcher kakaoFirstSubsApproveDispatcher = new KakaoFirstSubsApproveDispatcher(
			(params, url) -> new KakaoApiApproveResponse(),
			parameterProvider
		);

		KakaoApproveRequest request = KakaoApproveRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApiApproveResponse kaKaoApiApproveResponse = kakaoFirstSubsApproveDispatcher.approveFirstSubscription(request);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

}