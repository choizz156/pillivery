package com.team33.moduleexternalapi.infra.kakao;

import org.springframework.stereotype.Component;

import com.team33.moduleexternalapi.application.CancelClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiCancelResponse;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoCancelClient implements CancelClient {

	private final RestTemplateSender clientSender;

	@Override
	public void send(String params, String url) {
		clientSender.send(params, url, KakaoApiCancelResponse.class);
	}
}
