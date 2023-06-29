package server.team33.domain.payment.kakao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import server.team33.domain.order.entity.Order;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto;


@RequiredArgsConstructor
@Service
public class KaKaoPayApprove extends KaKaoPayService implements PayApprove {

    private final ParameterProvider parameterProvider;
    private final RestTemplate restTemplate;
    private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";
    private static final String KAKAO_SUBSCRIPTION_URL = "https://kapi.kakao.com/v1/payment/subscription";

    @Override
    public KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId) {
        var oneTimeApproveParams = parameterProvider.getOneTimeApproveParams(tid, pgToken, orderId);

        return getResponseDtoAboutApprove(oneTimeApproveParams);
    }

    @Override
    public KakaoResponseDto.Approve approveFirstSubscription(String tid, String pgToken,
        Long orderId) {
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
        MultiValueMap<String, String> params, String url
    ) {
        var entity = new HttpEntity<>(params, super.getHeaders());

        return restTemplate.postForObject(
            url,
            entity,
            KakaoResponseDto.Approve.class
        );
    }
}
