package com.team33.moduleexternalapi.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	public static final String PAYMENT_API = "payment-api-";

	private final WebClientErrorHandler errorHandler;

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
			.filter(errorHandler.errorResponseFilter())
			.filter(errorHandler.networkExceptionFilter())
			.build();
	}
}
