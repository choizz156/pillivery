package com.team33.modulebatch.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulebatch.exception.ClientPaymentException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ObjectMapper objectMapper;

	@Override
	public void handleError(final ClientHttpResponse response) throws IOException {

		if (response.getStatusCode().is5xxServerError()) {
			throw HttpServerErrorException.create(
				response.getStatusCode().getReasonPhrase(),
				response.getStatusCode(),
				response.getStatusText(),
				response.getHeaders(),
				response.getBody().readAllBytes(),
				StandardCharsets.UTF_8
			);
		}

		String responseBody = new BufferedReader(
			new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
			.lines()
			.collect(Collectors.joining("\n"));

		String methodResultMessage = "알 수 없는 에러가 발생했습니다.";

		methodResultMessage = getMessage(response, responseBody, methodResultMessage);

		throw new ClientPaymentException(methodResultMessage);
	}

	private String getMessage(ClientHttpResponse response, String responseBody, String methodResultMessage) {

		try {
			JsonNode rootNode = objectMapper.readTree(responseBody);
			if (rootNode.get("extras").has("method_result_message")) {
				methodResultMessage = rootNode.get("extras").get("method_result_message").asText();
				logging(response);
			}
		} catch (Exception e) {
			LOGGER.warn("응답 본문을 파싱할 수 없습니다: message = {},  e = {}", responseBody, e.getMessage());
			return methodResultMessage;
		}
		return methodResultMessage;
	}

	private void logging(ClientHttpResponse response) throws IOException {

		String body = getErrorAsString(response);
		String logMessage = String.format(
			"======payment error=====\nHeaders: %s\nResponse Status: %s\nResponse body: %s\n================",
			response.getHeaders(), response.getRawStatusCode(), body);

		if (response.getStatusCode().is4xxClientError()) {
			LOGGER.info(logMessage);
		} else if (response.getStatusCode().is5xxServerError()) {
			LOGGER.warn(logMessage);
		} else {
			LOGGER.info(logMessage);
		}
	}

	private String getErrorAsString(final ClientHttpResponse response) throws IOException {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getBody()))) {
			return br.readLine();
		}
	}
}
