package com.team33.moduleexternalapi.infra.kakao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.infra.WebClientSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveClient implements PaymentClient<KakaoApiApproveResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final WebClientSender webClientSender;

	@Override
	public KakaoApiApproveResponse send(Map<String, Object> params, String url) {
		return approve(params, url);
	}

	private KakaoApiApproveResponse approve(Map<String, Object> params, String url)  {

		try {
			return webClientSender.sendToPostSync(
				params,
				url,
				KakaoHeader.HTTP_HEADERS.getHeaders(),
				KakaoApiApproveResponse.class
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
