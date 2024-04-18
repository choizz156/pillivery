package com.team33.modulecore.payment.kakao.application;


import com.team33.modulecore.payment.kakao.infra.ParameterProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.payment.kakao.dto.KakaoResponseDto;

@RequiredArgsConstructor
@Service
public class KaKaoPayApproveService extends KaKaoHeader implements PayApprove {

    private final ParameterProvider parameterProvider;
    private final RestTemplate restTemplate;
    private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";
    private static final String KAKAO_SUBSCRIPTION_URL = "https://kapi.kakao.com/v1/payment/subscription";

    @Override
    public KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId) {
        var oneTimeApproveParams = parameterProvider.getOneTimeApproveParams(tid, pgToken, orderId);

        return getResponseDtoAboutApprove(oneTimeApproveParams, KAKAO_APPROVE_URL);
    }

    @Override
    public KakaoResponseDto.Approve approveFirstSubscription(
        String tid,
        String pgToken,
        Long orderId
    ) {
        var firstSubscriptionApproveParams =
            parameterProvider.getSubscriptionFirstApproveParams(tid, pgToken, orderId);

        return getResponseDtoAboutApprove(firstSubscriptionApproveParams, KAKAO_APPROVE_URL);
    }

    @Override
    public KakaoResponseDto.Approve approveSubscription(String sid, Order order) {
        var subscriptionApproveParams = parameterProvider.getSubscriptionApproveParams(sid, order);
        return getResponseDtoAboutApprove(subscriptionApproveParams, KAKAO_SUBSCRIPTION_URL);
    }

    private KakaoResponseDto.Approve getResponseDtoAboutApprove(
        MultiValueMap<String, String> params,
        String url
    ) {
        var entity = new HttpEntity<>(params, super.getHeaders());

        return restTemplate.postForObject(
            url,
            entity,
            KakaoResponseDto.Approve.class
        );
    }
}
