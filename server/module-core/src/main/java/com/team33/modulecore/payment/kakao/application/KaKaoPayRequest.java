package com.team33.modulecore.payment.kakao.application;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.payment.kakao.dto.KakaoResponseDto.Request;
import com.team33.modulecore.payment.kakao.infra.ParameterProvider;

@RequiredArgsConstructor
@Service
public class KaKaoPayRequest extends KaKaoHeader implements PayRequest {

    private final ParameterProvider parameterProvider;
    private final RestTemplate restTemplate;
    private static final String READY_URL = "https//kapi.kakao.com/v1/payment/ready";

    @Override
    public Request requestOneTime(Order order) {
        MultiValueMap<String, String> oneTimeReqsParams
            = parameterProvider.getOneTimeReqsParams(order);

        return getResponseDtoAboutRequest(oneTimeReqsParams);
    }

    @Override
    public Request requestSubscription(Order order) {
        MultiValueMap<String, String> subscriptionReqsParams
            = parameterProvider.getSubscriptionReqsParams(order);

        return getResponseDtoAboutRequest(subscriptionReqsParams);
    }

    private Request getResponseDtoAboutRequest(
        MultiValueMap<String, String> params
    ) {
        HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity
            = new HttpEntity<>(params, super.getHeaders());

        return restTemplate.postForObject(READY_URL, kakaoRequestEntity, Request.class);
    }
}
