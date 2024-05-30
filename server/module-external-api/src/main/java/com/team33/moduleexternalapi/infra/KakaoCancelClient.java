package com.team33.moduleexternalapi.infra;

import org.springframework.stereotype.Component;

import com.team33.moduleexternalapi.application.CancelClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoCancelClient implements CancelClient {

	private final KakaoClientSender kakaoClientSender;

	@Override
	public void send(String params, String url) {
		kakaoClientSender.send(params, url);
	}
}
