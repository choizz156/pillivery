package com.team33.moduleexternalapi.infra.kakao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
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

	private KakaoApiApproveResponse approve(Map<String, Object> params, String url) {

		try {
			return webClientSender.sendToPostSync(
				params,
				url,
				KakaoHeader.HTTP_HEADERS.getHeaders(),
				KakaoApiApproveResponse.class
			);
		} catch (JsonProcessingException e) {
			LOGGER.info("카카오페이 승인 요청 중 오류: {}", e.getMessage());
			throw new PaymentApiException("카카오페이 승인 요청 중 오류가 발생했습니다.");
		} catch (Exception e) {
			LOGGER.info("카카오페이 승인 처리 중 오류: {}", e.getMessage());
			throw new PaymentApiException("카카오페이 승인 처리 중 오류가 발생했습니다.");
		}
	}
}
