package com.team33.moduleexternalapi.infra;

import static com.team33.moduleexternalapi.infra.KakaoHeader.*;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;
import com.team33.moduleexternalapi.domain.PaymentClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveClient implements PaymentClient<KaKaoApproveResponse> {

	private final RestTemplate restTemplate;

	@Override
	public KaKaoApproveResponse send(MultiValueMap<String, String> params, String url) {
		var entity = new HttpEntity<>(params, HTTP_HEADERS.getHeaders());

		return restTemplate.postForObject(
			url,
			entity,
			KaKaoApproveResponse.class
		);
	}
}
