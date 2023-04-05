package server.team33.domain.payment.service;

import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import server.team33.domain.order.entity.Order;
import server.team33.domain.payment.dto.KakaoResponseDto;
import server.team33.domain.payment.dto.KakaoResponseDto.Request;

@Service
public class KaKaoPayRequestImpl extends KaKaoPayService implements KakaoPayRequest {

    private final ParameterProvider parameterProvider;
    private final RestTemplate restTemplate;
    private static final String READY_URL = "https//kapi.kakao.com/v1/payment/ready";

    public KaKaoPayRequestImpl(
        HttpHeaders httpHeaders,
        ParameterProvider parameterProvider,
        RestTemplate restTemplate
    ) {
        super(httpHeaders);
        this.parameterProvider = parameterProvider;
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @Override
    public KakaoResponseDto.Request requestOneTime(Order order) {
        MultiValueMap<String, String> oneTimeReqsParams =
            parameterProvider.getOneTimeReqsParams(order);

        return getResponseDtoAboutRequest(oneTimeReqsParams);
    }

    @SneakyThrows
    @Override
    public KakaoResponseDto.Request requestSubscription(Order order) {
        MultiValueMap<String, String> subscriptionReqsParams =
            parameterProvider.getSubscriptionReqsParams(order);

        return getResponseDtoAboutRequest(subscriptionReqsParams);
    }

    private KakaoResponseDto.Request getResponseDtoAboutRequest(
        MultiValueMap<String, String> params
    ) {
        HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity
            = new HttpEntity<>(params, super.getHeaders());

        return restTemplate.postForObject(READY_URL, kakaoRequestEntity, Request.class);
    }
}
