package com.team33.moduleexternalapi.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    HttpClient httpClient() {
        return HttpClientBuilder.create()
            .setMaxConnTotal(100)
            .setMaxConnPerRoute(5)
            .build();
    }

    @Bean
    HttpComponentsClientHttpRequestFactory factory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(3000);
        factory.setConnectTimeout(3000);
        factory.setHttpClient(httpClient);

        return factory;
    }

    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));

        return restTemplate;
    }
}
