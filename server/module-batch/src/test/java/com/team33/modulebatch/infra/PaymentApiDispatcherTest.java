package com.team33.modulebatch.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.team33.modulebatch.step.SubscriptionOrderVO;

class PaymentApiDispatcherTest {

	private static final String URL = "http://localhost:8080/api/payments/approve/subscriptions/";

	@DisplayName("빈 리스트는 dispatch 메서드가 작동하지 않는다.")
	@Test
	void test1() {
		//given
		RestTemplateSender mockRestTemplateSender = mock(RestTemplateSender.class);
		PaymentApiDispatcher paymentApiDispatcher = new PaymentApiDispatcher(mockRestTemplateSender);

		//when
		paymentApiDispatcher.dispatch(Collections.emptyList());

		//then
		verifyNoInteractions(mockRestTemplateSender);
	}

	@DisplayName("list의 요소가 한 개일 경우, 한 번만 요청을 보낸다. ")
	@Test
	void test2() {
		//given
		RestTemplateSender mockRestTemplateSender = mock(RestTemplateSender.class);
		PaymentApiDispatcher paymentApiDispatcher = new PaymentApiDispatcher(mockRestTemplateSender);

		SubscriptionOrderVO mockOrderVO = mock(SubscriptionOrderVO.class);
		when(mockOrderVO.getSubscriptionOrderId()).thenReturn(100L);

		//when
		paymentApiDispatcher.dispatch(List.of(mockOrderVO));

		//then
		ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
		verify(mockRestTemplateSender, times(1)).sendToPost(urlCaptor.capture(), eq(null), eq(null), eq(String.class));
		assertThat(urlCaptor.getValue()).isEqualTo(URL + mockOrderVO.getSubscriptionOrderId());
	}

	@DisplayName("list의 요소의 개수가 여러 개일 경우 그 수 만큼 요청을 보낸다.")
	@Test
	void testDispatch_WhenListHasMultipleOrders_ExecutesSendForEachOrder() {

		RestTemplateSender mockRestTemplateSender = mock(RestTemplateSender.class);
		PaymentApiDispatcher paymentApiDispatcher = new PaymentApiDispatcher(mockRestTemplateSender);

		SubscriptionOrderVO mockOrderVO1 = mock(SubscriptionOrderVO.class);
		SubscriptionOrderVO mockOrderVO2 = mock(SubscriptionOrderVO.class);

		when(mockOrderVO1.getSubscriptionOrderId()).thenReturn(101L);
		when(mockOrderVO2.getSubscriptionOrderId()).thenReturn(102L);

		paymentApiDispatcher.dispatch(List.of(mockOrderVO1, mockOrderVO2));

		ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
		verify(mockRestTemplateSender, times(2)).sendToPost(urlCaptor.capture(), eq(null), eq(null), eq(String.class));
		List<String> capturedUrls = urlCaptor.getAllValues();
		assertThat(capturedUrls.get(0)).isEqualTo(URL + mockOrderVO1.getSubscriptionOrderId());
		assertThat(capturedUrls.get(1)).isEqualTo(URL + mockOrderVO2.getSubscriptionOrderId());
	}
}