package com.team33.modulebatch.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulebatch.exception.BatchApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestTemplateErrorHandler implements ResponseErrorHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ObjectMapper objectMapper;

	@Override
	public boolean hasError(final ClientHttpResponse response) throws IOException {

		final HttpStatus statusCode = response.getStatusCode();
		return !statusCode.is2xxSuccessful();
	}

	@Override
	public void handleError(final ClientHttpResponse response) throws IOException {
		String responseBody = new BufferedReader(
				new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
				.lines()
				.collect(Collectors.joining("\n"));

				String methodResultMessage = "알 수 없는 에러가 발생했습니다.";

		try {
			JsonNode rootNode = objectMapper.readTree(responseBody);

			if (rootNode.get("extras").has("method_result_message")) {
				methodResultMessage = rootNode.get("extras").get("method_result_message").asText();

				logging(response);
			}
		} catch (Exception e) {
			LOGGER.warn("응답 본문을 파싱할 수 없습니다: {}", e.getMessage());
		}

		throw new BatchApiException(methodResultMessage);
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
