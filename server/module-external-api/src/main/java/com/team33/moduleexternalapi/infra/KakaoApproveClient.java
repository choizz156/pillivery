package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.team33.moduleexternalapi.application.ClientSender;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Component
public class KakaoApproveClient implements PaymentClient<KakaoApproveResponse> {

	private final ClientSender clientSender;

	@SneakyThrows
	@Override
	public KakaoApproveResponse send(Map<String, String> params, String url) {

		return clientSender.send(params, url, KakaoApproveResponse.class);
	}

}
