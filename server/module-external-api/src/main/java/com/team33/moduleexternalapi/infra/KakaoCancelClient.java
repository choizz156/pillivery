package com.team33.moduleexternalapi.infra;

import org.springframework.stereotype.Component;

import com.team33.moduleexternalapi.application.CancelClient;
import com.team33.moduleexternalapi.dto.KakaoApiCancelResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoCancelClient implements CancelClient {

	private final KakaoClientSender clientSender;

	@Override
	public void send(String params, String url) {
		clientSender.send(params, url, KakaoApiCancelResponse.class);
	}
}
