package com.team33.modulebatch.infra;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.team33.modulebatch.step.SubscriptionOrderVO;

class PaymentApiDispatcherTest {

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
		when(mockOrderVO.getIdempotencyKey()).thenReturn("key-100");

		//when
		paymentApiDispatcher.dispatch(List.of(mockOrderVO));

		//then
		ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
		verify(mockRestTemplateSender, times(1)).sendToPost(anyString(), urlCaptor.capture(), eq(null));
	}

	@DisplayName("list의 요소의 개수가 여러 개일 경우 그 수 만큼 요청을 보낸다.")
	@Test
	void test3() {

		RestTemplateSender mockRestTemplateSender = mock(RestTemplateSender.class);
		PaymentApiDispatcher paymentApiDispatcher = new PaymentApiDispatcher(mockRestTemplateSender);

		SubscriptionOrderVO mockOrderVO1 = mock(SubscriptionOrderVO.class);
		SubscriptionOrderVO mockOrderVO2 = mock(SubscriptionOrderVO.class);

		when(mockOrderVO1.getSubscriptionOrderId()).thenReturn(101L);
		when(mockOrderVO2.getSubscriptionOrderId()).thenReturn(102L);
		when(mockOrderVO1.getIdempotencyKey()).thenReturn("key-101");
		when(mockOrderVO2.getIdempotencyKey()).thenReturn("key-102");

		paymentApiDispatcher.dispatch(List.of(mockOrderVO1, mockOrderVO2));

		ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
		verify(mockRestTemplateSender, times(2)).sendToPost(anyString(), urlCaptor.capture(), eq(null));
	}

    @DisplayName("이미 처리된 주문은 다시 처리하지 않는다")
    @Test
    void test4() {
        // given
        RestTemplateSender mockRestTemplateSender = mock(RestTemplateSender.class);
        PaymentApiDispatcher paymentApiDispatcher = new PaymentApiDispatcher(mockRestTemplateSender);

        SubscriptionOrderVO mockOrderVO = mock(SubscriptionOrderVO.class);
        when(mockOrderVO.getSubscriptionOrderId()).thenReturn(100L);
        when(mockOrderVO.getIdempotencyKey()).thenReturn("key-100");

        // when
        paymentApiDispatcher.dispatch(List.of(mockOrderVO)); 
        paymentApiDispatcher.dispatch(List.of(mockOrderVO)); 

        // then
        verify(mockRestTemplateSender, times(1)).sendToPost(anyString(), anyString(), eq(null));
    }

    @DisplayName("여러 주문 중 처리되지 않은 주문만 처리한다")
    @Test
    void test5() {
        // given
        RestTemplateSender mockRestTemplateSender = mock(RestTemplateSender.class);
        PaymentApiDispatcher paymentApiDispatcher = new PaymentApiDispatcher(mockRestTemplateSender);

        SubscriptionOrderVO firstOrder = mock(SubscriptionOrderVO.class);
        SubscriptionOrderVO secondOrder = mock(SubscriptionOrderVO.class);
        when(firstOrder.getSubscriptionOrderId()).thenReturn(101L);
        when(secondOrder.getSubscriptionOrderId()).thenReturn(102L);
        when(firstOrder.getIdempotencyKey()).thenReturn("key-101");
        when(secondOrder.getIdempotencyKey()).thenReturn("key-102");

        // when
        paymentApiDispatcher.dispatch(List.of(firstOrder)); 
        paymentApiDispatcher.dispatch(List.of(firstOrder, secondOrder)); 

        // then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockRestTemplateSender, times(2)).sendToPost(anyString(), urlCaptor.capture(), eq(null));
    }
}