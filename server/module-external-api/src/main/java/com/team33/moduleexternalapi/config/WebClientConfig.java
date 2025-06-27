package com.team33.moduleexternalapi.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {


	private final WebClientErrorHandler errorHandler;

	@Bean
	public WebClient webClient() {

		HttpClient httpClient = HttpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
			.responseTimeout(Duration.ofSeconds(30))
			.doOnConnected(conn ->
				conn.addHandlerLast(new ReadTimeoutHandler(25, TimeUnit.SECONDS))
					.addHandlerLast(new WriteTimeoutHandler(25, TimeUnit.SECONDS))
			);

		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.filter(errorHandler.errorResponseFilter())
			.filter(errorHandler.networkExceptionFilter())
			.build();
	}
}
