package com.team33.modulecore.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.RequestFacade;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;


class KakaoRequestFacadeTest {

	@DisplayName("정기 결제 요청일 경우 정기 결제 요청 서비스로 위임한다.")
	@Test
	void 구분_테스트() throws Exception{
		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("isSubscription", true)
			.set("orderPrice", new OrderPrice(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("sid", "sid")
			.sample();

		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		KakaoNormalRequestService kakaoNormalRequestService = mock(KakaoNormalRequestService.class);
		KakaoSubsRequestService kakaoSubsRequestService = mock(KakaoSubsRequestService.class);


		when(orderFindHelper.findOrder(anyLong())).thenReturn(order);
		when(kakaoNormalRequestService.requestOneTime(any())).thenReturn(new KakaoRequestResponse());
		when(kakaoSubsRequestService.requestSubscription(any())).thenReturn(new KakaoRequestResponse());

		RequestFacade<KakaoRequestResponse> facade = new KakaoRequestFacade(
			orderFindHelper,
			kakaoNormalRequestService,
			kakaoSubsRequestService
		);

		//when
		KakaoRequestResponse request = facade.request(1L);

		//then
		verify(orderFindHelper, times(1)).findOrder(1L);
		verify(kakaoNormalRequestService, times(0)).requestOneTime(any(Order.class));
		verify(kakaoSubsRequestService, times(1)).requestSubscription(any(Order.class));

		assertThat(request).isNotNull();
	}

	@DisplayName("단건 결제 요청의 경우 단건 결제 요청 서비스에 위임한다.")
	@Test
	void 구분2() throws Exception{
		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("isSubscription", false)
			.set("orderPrice", new OrderPrice(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.sample();

		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		KakaoNormalRequestService kakaoNormalRequestService = mock(KakaoNormalRequestService.class);
		KakaoSubsRequestService kakaoSubsRequestService = mock(KakaoSubsRequestService.class);


		when(orderFindHelper.findOrder(anyLong())).thenReturn(order);
		when(kakaoNormalRequestService.requestOneTime(any())).thenReturn(new KakaoRequestResponse());
		when(kakaoSubsRequestService.requestSubscription(any())).thenReturn(new KakaoRequestResponse());

		RequestFacade<KakaoRequestResponse> facade = new KakaoRequestFacade(
			orderFindHelper,
			kakaoNormalRequestService,
			kakaoSubsRequestService
		);

		//when
		KakaoRequestResponse request = facade.request(1L);


		//then
		verify(orderFindHelper, times(1)).findOrder(1L);
		verify(kakaoNormalRequestService, times(1)).requestOneTime(any(Order.class));
		verify(kakaoSubsRequestService, times(0)).requestSubscription(any(Order.class));

		assertThat(request).isNotNull();

	}

}