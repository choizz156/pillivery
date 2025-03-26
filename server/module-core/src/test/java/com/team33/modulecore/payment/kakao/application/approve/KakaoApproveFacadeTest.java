package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.payment.application.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.application.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoApproveFacade;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

class KakaoApproveFacadeTest {

	@DisplayName("정기 결제 승인시 정기 결제 승인 서비스로 위임한다.")
	@Test
	void 정기_서비스_위임() throws Exception{
		//given
		SubscriptionApproveService subscriptionApproveService = mock(SubscriptionApproveService.class);
		OneTimeApproveService oneTimeApproveService = mock(OneTimeApproveService.class);
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);

		when(orderFindHelper.checkSubscription(anyLong())).thenReturn(true);
		when(subscriptionApproveService.approveInitial(any(ApproveRequest.class))).thenReturn(new KakaoApproveResponse());
		when(oneTimeApproveService.approveOneTime(any(ApproveRequest.class))).thenReturn(new KakaoApproveResponse());
		KakaoApproveFacade kakaoApproveFacade =
			new KakaoApproveFacade(subscriptionApproveService, oneTimeApproveService, orderFindHelper);

		KakaoApproveRequest request = KakaoApproveRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApproveResponse kakaoApiApproveResponse = kakaoApproveFacade.approveFirst(request);

		//then
		verify(subscriptionApproveService, times(1)).approveInitial(any(ApproveRequest.class));
		verify(oneTimeApproveService, times(0)).approveOneTime(any(ApproveRequest.class));
		verify(orderFindHelper, times(1)).checkSubscription(anyLong());

		assertThat(kakaoApiApproveResponse).isNotNull();
	}

	@DisplayName("단건 결제 승인 시 단건 승인 서비스로 위임한다.")
	@Test
	void 단건_승인_위임() throws Exception{
		SubscriptionApproveService subscriptionApproveService = mock(SubscriptionApproveService.class);
		OneTimeApproveService oneTimeApproveService = mock(OneTimeApproveService.class);
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);

		when(orderFindHelper.checkSubscription(anyLong())).thenReturn(false);
		when(subscriptionApproveService.approveInitial(any(ApproveRequest.class))).thenReturn(new KakaoApproveResponse());
		when(oneTimeApproveService.approveOneTime(any(ApproveRequest.class))).thenReturn(new KakaoApproveResponse());
		KakaoApproveFacade kakaoApproveFacade =
			new KakaoApproveFacade(subscriptionApproveService, oneTimeApproveService, orderFindHelper);

		KakaoApproveRequest request = KakaoApproveRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApproveResponse kakaoApiApproveResponse = kakaoApproveFacade.approveFirst(request);

		//then
		verify(subscriptionApproveService, times(0)).approveInitial(any(ApproveRequest.class));
		verify(oneTimeApproveService, times(1)).approveOneTime(any(ApproveRequest.class));
		verify(orderFindHelper, times(1)).checkSubscription(anyLong());

		assertThat(kakaoApiApproveResponse).isNotNull();
	}
}