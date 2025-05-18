package com.team33.modulecore.core.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoSubscriptionApproveDispatcherTest {

	@DisplayName("정기 결제 시 승인 요청을 보낼 수 있다.")
	@Test
	void 정기_승인() throws Exception {
		//given
		SubscriptionOrder subscriptionOrder = FixtureMonkeyFactory.get().giveMeOne(SubscriptionOrder.class);

		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoSubscriptionApproveDispatcher kakaoSubscriptionApproveDispatcher =
			new KakaoSubscriptionApproveDispatcher(
				(params, url) -> new KakaoApiApproveResponse(),
				parameterProvider
			);

		//when
		KakaoApiApproveResponse kaKaoApiApproveResponse = kakaoSubscriptionApproveDispatcher.approveSubscription(subscriptionOrder);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

}