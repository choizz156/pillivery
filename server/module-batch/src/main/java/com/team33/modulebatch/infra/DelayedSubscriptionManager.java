package com.team33.modulebatch.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulebatch.exception.ClientPaymentException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DelayedSubscriptionManager {

	private final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ObjectMapper objectMapper;

	public void add(long subscriptionOrderId, String responseBody) {

		LOGGER.info("Adding delayed subscription response: {}, id = {}", responseBody, subscriptionOrderId);
		String methodResultMessage = "알 수 없는 에러가 발생했습니다.";
		methodResultMessage = getMessage(responseBody, methodResultMessage);
		throw new ClientPaymentException(methodResultMessage);
	}

	private String getMessage(String responseBody, String methodResultMessage) {

		try {
			JsonNode rootNode = objectMapper.readTree(responseBody);
			if (rootNode.get("extras").has("method_result_message")) {
				methodResultMessage = rootNode.get("extras").get("method_result_message").asText();
			}
		} catch (Exception e) {
			LOGGER.warn("응답 본문을 파싱할 수 없습니다: message = {},  e = {}", responseBody, e.getMessage());
			return methodResultMessage;
		}
		return methodResultMessage;
	}
}
