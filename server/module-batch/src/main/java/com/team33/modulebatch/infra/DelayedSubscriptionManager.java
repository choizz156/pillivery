package com.team33.modulebatch.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.entity.DelayedItem;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DelayedSubscriptionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	private static final String EXTRAS = "extras";
	private static final String METHOD_RESULT_MESSAGE = "method_result_message";

	private final ObjectMapper objectMapper;
	private final DelayedItemRepository delayedItemRepository;

	public void add(long subscriptionOrderId, String responseBody) {

		String errorMessage = getMessage(responseBody);
		LOGGER.info("Adding delayed subscription response: {}, id = {}", errorMessage, subscriptionOrderId);

		DelayedItem delayedItem = DelayedItem.of(subscriptionOrderId, errorMessage);
		delayedItemRepository.save(delayedItem);
	}

	private String getMessage(String responseBody) {

		String message = null;
		try {
			JsonNode rootNode = objectMapper.readTree(responseBody);
			if (rootNode.get(EXTRAS).has(METHOD_RESULT_MESSAGE)) {
				message = rootNode.get(EXTRAS).get(METHOD_RESULT_MESSAGE).asText();
			}
		} catch (Exception e) {
			LOGGER.warn("응답 본문을 파싱할 수 없습니다: message = {},  e = {}", responseBody, e.getMessage());
			message = "클라이언트 오류 발생";
		}
		return message;
	}
}
