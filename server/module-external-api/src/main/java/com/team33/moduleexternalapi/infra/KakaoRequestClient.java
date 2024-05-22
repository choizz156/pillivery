package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.moduleexternalapi.application.ClientSender;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoRequestClient implements PaymentClient<KakaoRequestResponse> {

	private final ClientSender sender;

	@SneakyThrows
	@Override
	public KakaoRequestResponse send(Map<String, String> params, String url) {

		return sender.send(params, url, KakaoRequestResponse.class);
	}
}
