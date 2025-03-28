package com.team33.moduleexternalapi.infra.kakao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.WebClientSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component("KakaoRequestClient")
public class KakaoRequestClient implements PaymentClient<KakaoApiRequestResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final WebClientSender webClientSender;

	@Override
	public KakaoApiRequestResponse send(Map<String, Object> params, String url) {
		return request(params, url);
	}

	private KakaoApiRequestResponse request(Map<String, Object> params, String url) {

		try {
			return webClientSender.sendToPostSync(
				params,
				url,
				KakaoHeader.HTTP_HEADERS.getHeaders(),
				KakaoApiRequestResponse.class
			);
		} catch (JsonProcessingException e) {
			LOGGER.info("카카오페이 결제 요청 중 오류: {}", e.getMessage());
			throw new PaymentApiException("카카오페이 결제 요청 중 오류가 발생했습니다.");
		} catch (Exception e) {
			LOGGER.info("카카오페이 결제 처리 중 오류: {}", e.getMessage());
			throw new PaymentApiException("카카오페이 결제 처리 중 오류가 발생했습니다.");
		}
	}
}
