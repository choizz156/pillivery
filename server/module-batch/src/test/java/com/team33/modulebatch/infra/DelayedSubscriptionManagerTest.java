package com.team33.modulebatch.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.ErrorStatus;
import com.team33.modulebatch.domain.entity.DelayedItem;

@ExtendWith(MockitoExtension.class)
class DelayedSubscriptionManagerTest {

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private DelayedItemRepository delayedItemRepository;

	@InjectMocks
	private DelayedSubscriptionManager delayedSubscriptionManager;

	private long subscriptionOrderId;
	private String responseBodyWithResultMessage;
	private String responseBodyWithoutResultMessage;
	private String responseBodyInvalidJson;

	@BeforeEach
	void setUp() {
		subscriptionOrderId = 1L;
		responseBodyWithResultMessage = "{\"extras\":{\"method_result_message\":\"Test error message\"}}";
		responseBodyWithoutResultMessage = "{\"extras\":{}}";
		responseBodyInvalidJson = "invalid json";
	}

	@DisplayName("새로운 결제 지연 아이템을 저장할 수 있다.")
	@Test
	void test1() throws Exception {
		// given
		given(delayedItemRepository.existsDelayedItemBySubscriptionOrderIdAndStatus(subscriptionOrderId, ErrorStatus.DELAYED))
			.willReturn(false);

		JsonNode mockRootNode = objectMapper.readTree(responseBodyWithResultMessage);
		given(objectMapper.readTree(responseBodyWithResultMessage)).willReturn(mockRootNode);

		// when
		delayedSubscriptionManager.add(subscriptionOrderId, responseBodyWithResultMessage);

		// then
		ArgumentCaptor<DelayedItem> captor = ArgumentCaptor.forClass(DelayedItem.class);
		verify(delayedItemRepository, times(1)).save(captor.capture());
		DelayedItem savedItem = captor.getValue();

		assertThat(savedItem.getSubscriptionOrderId()).isEqualTo(subscriptionOrderId);
		assertThat(savedItem.getStatus()).isEqualTo(ErrorStatus.DELAYED);
	}

	@DisplayName("이미 존재하는 결제 지연 아이템은 추가하지 않는다.")
	@Test
	void test2() {
		// given
		given(delayedItemRepository.existsDelayedItemBySubscriptionOrderIdAndStatus(subscriptionOrderId,
			ErrorStatus.DELAYED))
			.willReturn(true);

		// when
		delayedSubscriptionManager.add(subscriptionOrderId, responseBodyWithResultMessage);

		// then
		verify(delayedItemRepository, never()).save(any(DelayedItem.class));
	}


	@DisplayName("응답 본문 파싱 실패 시 기본 메시지로 결제 지연 아이템을 추가한다.")
	@Test
	void test3() throws Exception {
		// given
		given(delayedItemRepository.existsDelayedItemBySubscriptionOrderIdAndStatus(subscriptionOrderId,
			ErrorStatus.DELAYED))
			.willReturn(false);
		given(objectMapper.readTree(responseBodyInvalidJson)).willThrow(new RuntimeException("parsing error"));

		// when
		delayedSubscriptionManager.add(subscriptionOrderId, responseBodyInvalidJson);

		// then
		ArgumentCaptor<DelayedItem> captor = ArgumentCaptor.forClass(DelayedItem.class);
		verify(delayedItemRepository, times(1)).save(captor.capture());
		DelayedItem savedItem = captor.getValue();

		assertThat(savedItem.getSubscriptionOrderId()).isEqualTo(subscriptionOrderId);
		assertThat(savedItem.getStatus()).isEqualTo(ErrorStatus.DELAYED);
	}

}