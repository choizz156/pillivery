package com.team33.moduleexternalapi.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.team33.moduleexternalapi.exception.PaymentApiException;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {

	@Qualifier("kakao")
	@Bean
	public WebClient webClient() {

		HttpClient httpClient = HttpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
			.doOnConnected(conn ->
				conn.addHandlerLast(new ReadTimeoutHandler(3, TimeUnit.SECONDS))
					.addHandlerLast(new WriteTimeoutHandler(3, TimeUnit.SECONDS))
			)
			.responseTimeout(Duration.ofSeconds(10));

		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.filter(errorLog())
			.build();
	}

	private ExchangeFilterFunction errorLog() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			if (!response.statusCode().is2xxSuccessful()) {
				 return response.bodyToMono(String.class)
					.flatMap(body -> {
						log.error("======payment error=====");
						log.error("Headers: {}", response.headers());
						log.error("Response Status : {}", response.statusCode());
						log.error("Response body: {}", body);
						log.error("================");
						return Mono.error(new PaymentApiException("kakao api fail"));
					});
			}
			return Mono.just(response);
		});
	}
}
