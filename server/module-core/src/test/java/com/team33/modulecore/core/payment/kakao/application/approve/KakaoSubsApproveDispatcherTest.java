package com.team33.modulecore.core.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoSubsApproveDispatcherTest {

	@DisplayName("정기 결제 시 승인 요청을 보낼 수 있다.")
	@Test
	void 정기_승인() throws Exception {
		//given
		var subscriptionOrder = mock(SubscriptionOrder.class);

		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoSubsApproveDispatcher kakaoSubsApproveDispatcher =
			new KakaoSubsApproveDispatcher(
				(params, url) -> new KakaoApiApproveResponse(),
				parameterProvider
			);

		//when
		KakaoApiApproveResponse kaKaoApiApproveResponse = kakaoSubsApproveDispatcher.approveSubscription(subscriptionOrder);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

}