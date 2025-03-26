package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoOneTimeApproveService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoOneTimeApproveServiceTest {

	@DisplayName("단건 결제 승인을 위임할 수 있다.")
	@Test
	void 단건_승인_위임() throws Exception {
		//given


		KakaoOneTimeApproveService kakaoNormalApproveService =
			new KakaoOneTimeApproveService(
				order -> new KakaoApiApproveResponse()
			);

		ApproveRequest request = KakaoApproveRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApproveResponse kaKaoApiApproveResponse = kakaoNormalApproveService.approveOneTime(request);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

}