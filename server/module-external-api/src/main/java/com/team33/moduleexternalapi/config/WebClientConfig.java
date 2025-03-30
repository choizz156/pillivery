package com.team33.moduleexternalapi.config;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.team33.moduleexternalapi.exception.PaymentApiException;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

@Configuration
public class WebClientConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	public static final String PAYMENT_API = "payment-api-";

	@Bean
	public WebClient webClient() {

		int optimalThreadCount = Runtime.getRuntime().availableProcessors() * 2;

		LoopResources loopResources = LoopResources.create(PAYMENT_API, optimalThreadCount, false);

		ConnectionProvider provider = ConnectionProvider.builder("external-api-pool")
			.maxConnections(100)
			.pendingAcquireTimeout(Duration.ofSeconds(3))
			.maxIdleTime(Duration.ofSeconds(30))
			.maxLifeTime(Duration.ofMinutes(5))
			.evictInBackground(Duration.ofSeconds(30))
			.build();

		HttpClient httpClient = HttpClient.create(provider)
			.runOn(loopResources)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
			.responseTimeout(Duration.ofSeconds(5))
			.doOnConnected(conn ->
				conn.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
					.addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))
			);

		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.filter(errorLog())
			.filter(networkErrorHandler())
			.build();
	}

	private ExchangeFilterFunction errorLog() {

		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			if (!response.statusCode().is2xxSuccessful()) {
				return response.bodyToMono(String.class)
					.flatMap(body -> handleClientResponse(response, body));
			}
			return Mono.just(response);
		});
	}

	private Mono<ClientResponse> handleClientResponse(ClientResponse response, String body) {

		if (response.statusCode().is4xxClientError()) {
			logging(response, body);
			return Mono.error(new PaymentApiException(
				"클라이언트 오류: " + response.statusCode() + " - " + body));
		}

		if (response.statusCode().is5xxServerError()) {
			logging(response, body);
			return Mono.error(new RuntimeException("서버 오류: " + response.statusCode() + " - " + body));
		}

		return Mono.error(new PaymentApiException("API 오류: " + response.statusCode() + " - " + body));
	}

	private void logging(ClientResponse response, String body) {

		String logMessage = String.format(
			"======payment error=====\nHeaders: %s\nResponse Status: %s\nResponse body: %s\n================",
			response.headers(), response.statusCode(), body
		);

		if (response.statusCode().is4xxClientError()) {
			LOGGER.info(logMessage);
		} else if (response.statusCode().is5xxServerError()) {
			LOGGER.warn(logMessage);
		}else{
			LOGGER.info(logMessage);
		}
	}

	private ExchangeFilterFunction networkErrorHandler() {

		return ExchangeFilterFunction.ofResponseProcessor(Mono::just)
			.andThen(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> Mono.just(clientRequest)
				.onErrorResume(TimeoutException.class, ex -> {
					LOGGER.error("요청 타임아웃 발생: {}", ex.getMessage());
					return Mono.error(new RuntimeException("결제 API 요청 타임아웃: " + ex.getMessage(), ex));
				})
				.onErrorResume(ConnectException.class, ex -> {
					LOGGER.error("연결 오류 발생: {}", ex.getMessage());
					return Mono.error(new RuntimeException("결제 API 연결 실패: " + ex.getMessage(), ex));
				})
				.onErrorResume(IOException.class, ex -> {
					LOGGER.error("IO 오류 발생: {}", ex.getMessage());
					return Mono.error(new RuntimeException("결제 API IO 오류: " + ex.getMessage(), ex));
				})
				.onErrorResume(Exception.class, ex -> {
					LOGGER.error("기타 예외 발생: {}", ex.getMessage());
					return Mono.error(new PaymentApiException("결제 API 오류: " + ex.getMessage(), ex));
				})));
	}
}
