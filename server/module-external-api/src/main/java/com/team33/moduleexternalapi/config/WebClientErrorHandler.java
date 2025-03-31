package com.team33.moduleexternalapi.config;

import java.net.ConnectException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class WebClientErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	private final ObjectMapper objectMapper;

	public ExchangeFilterFunction errorResponseFilter() {

		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {
				return response.bodyToMono(String.class)
					.flatMap(body -> handleClientResponse(response, body));
			}
			return Mono.just(response);
		});
	}

	public ExchangeFilterFunction networkExceptionFilter() {

		return ExchangeFilterFunction.ofResponseProcessor(Mono::just)
			.andThen(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> Mono.just(clientRequest)
				.onErrorResume(ReadTimeoutException.class, ex -> {
					LOGGER.warn("읽기 타임아웃: {}", ex.getMessage());
					return Mono.error(
						new WebClientRequestException(
							ex,
							clientRequest.method(),
							clientRequest.url(),
							HttpHeaders.EMPTY
						));
				})
				.onErrorResume(WriteTimeoutException.class, ex -> {
					LOGGER.warn("쓰기 타임아웃: {}", ex.getMessage());
					return Mono.error(
						new WebClientRequestException(
							ex,
							clientRequest.method(),
							clientRequest.url(),
							HttpHeaders.EMPTY
						));
				})
				.onErrorResume(UnknownHostException.class, ex -> {
					LOGGER.warn("알 수 없는 호스트: {}", ex.getMessage());
					return Mono.error(
						new WebClientRequestException(
							ex,
							clientRequest.method(),
							clientRequest.url(),
							HttpHeaders.EMPTY
						));
				})
				.onErrorResume(Exception.class, ex -> {
					LOGGER.warn("기타 네트워크 오류: {}", ex.getMessage());
					return Mono.error(new PaymentApiException("API 통신 중 오류 발생", ex));
				})
				.onErrorResume(ConnectException.class, ex -> {
					LOGGER.warn("연결 타임아웃: {}", ex.getMessage());
					return Mono.error(
						new WebClientRequestException(
							ex,
							clientRequest.method(),
							clientRequest.url(),
							HttpHeaders.EMPTY
						));
				})));
	}

	private Mono<ClientResponse> handleClientResponse(ClientResponse response, String body) {

		if (response.statusCode().is4xxClientError()) {
			return handleClientError(response, body);
		}

		if (response.statusCode().is5xxServerError()) {
			return handleServerError(response, body);
		}

		return Mono.error(new PaymentApiException("API 오류: " + response.statusCode() + " - " + body));
	}

	private Mono<ClientResponse> handleClientError(ClientResponse response, String body) {

		int errorCode = parseKakaoErrorResponse(body);

		if (errorCode == -782) {
			return Mono.error(new SubscriptionPaymentException(
				response.statusCode().value(),
				response.statusCode().getReasonPhrase(),
				body)
			);
		}

		if (errorCode == -780) {
			return Mono.error(new PaymentApiException("결제 승인 오류: " + response.statusCode() + " - " + body));
		}

		return Mono.error(new PaymentApiException("클라이언트 오류: " + response.statusCode() + " - " + body));
	}

	private Mono<ClientResponse> handleServerError(ClientResponse response, String body) {

		LOGGER.warn("서버 오류: {} - {}", response.statusCode(), body);
		return Mono.error(new RuntimeException("서버 오류: " + response.statusCode() + " - " + body));
	}

	private int parseKakaoErrorResponse(String body) {

		int errorCode = 0;
		try {
			JsonNode rootNode = objectMapper.readTree(body);

			if (isValidKakaoErrorFormat(rootNode)) {
				errorCode = rootNode.get("error_code").asInt();
				String errorMessage = rootNode.get("error_message").asText();
				String methodResultCode = rootNode.get("extras").get("method_result_code").asText();
				String methodResultMessage = rootNode.get("extras").get("method_result_message").asText();

				logKakaoError(errorCode, errorMessage, methodResultCode, methodResultMessage);
			}
		} catch (Exception e) {
			LOGGER.warn("응답 본문을 파싱할 수 없습니다: {}", e.getMessage());
			throw new PaymentApiException("응답 본문을 파싱할 수 없습니다: " + e.getMessage(), e);
		}

		return errorCode;
	}

	private boolean isValidKakaoErrorFormat(JsonNode rootNode) {

		return rootNode.has("error_code") &&
			rootNode.has("error_message") &&
			rootNode.has("extras") &&
			rootNode.get("extras").has("method_result_code") &&
			rootNode.get("extras").has("method_result_message");
	}

	private void logKakaoError(int errorCode, String errorMessage, String methodResultCode,
		String methodResultMessage) {

		String errorLog = String.format(
			"Kakao Payment API Error - Code: %d, Message: %s, MethodCode: %s, MethodMessage: %s",
			errorCode,
			errorMessage,
			methodResultCode,
			methodResultMessage);

		LOGGER.error(errorLog);
	}
}
