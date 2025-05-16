package com.team33.modulebatch.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

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


	}
}
