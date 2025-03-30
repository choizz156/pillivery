package com.team33.modulebatch.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.team33.modulebatch.exception.BatchInternalApiException;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	@Override
	public boolean hasError(final ClientHttpResponse response) throws IOException {

		final HttpStatus statusCode = response.getStatusCode();
		return !statusCode.is2xxSuccessful();
	}

	@Override
	public void handleError(final ClientHttpResponse response) throws IOException {

		if (response.getStatusCode().is4xxClientError()) {
			logging(response);
			throw new BatchInternalApiException(response.getStatusCode().getReasonPhrase());
		}

		if (response.getStatusCode().is5xxServerError()) {
			logging(response);
			throw new BatchInternalApiException(response.getStatusCode().getReasonPhrase());
		}
	}

	private void logging(ClientHttpResponse response) throws IOException {
		String body = getErrorAsString(response);
		String logMessage = String.format(
			"======payment error=====\nHeaders: %s\nResponse Status: %s\nResponse body: %s\n================",
			response.getHeaders(), response.getRawStatusCode(), body
		);

		if (response.getStatusCode().is4xxClientError()) {
			LOGGER.info(logMessage);
		} else if (response.getStatusCode().is5xxServerError()) {
			LOGGER.warn(logMessage);
		}else{
			LOGGER.info(logMessage);
		}
	}

	private String getErrorAsString(final ClientHttpResponse response) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getBody()))) {
			return br.readLine();
		}
	}
}
