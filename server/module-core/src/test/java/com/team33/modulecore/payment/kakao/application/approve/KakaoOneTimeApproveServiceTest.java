package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;

class KakaoOneTimeApproveServiceTest {

	@DisplayName("단건 결제 승인을 위임할 수 있다.")
	@Test
	void 단건_승인_위임() throws Exception {
		//given

		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoOneTimeApproveService kakaoNormalApproveService =
			new KakaoOneTimeApproveService(
				order -> new KakaoApproveResponse()
			);

		ApproveRequest request = KakaoApproveOneTimeRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApproveResponse kaKaoApproveResponse = kakaoNormalApproveService.approveOneTime(request);

		//then
		assertThat(kaKaoApproveResponse).isNotNull();
	}

}