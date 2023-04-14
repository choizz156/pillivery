package server.team33.domain.payment.kakao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto;


@RequiredArgsConstructor
@Service
public class KakaoPayApproveImpl extends KaKaoPayService implements KakaoPayApprove {

    private final ParameterProvider parameterProvider;
    private final RestTemplate restTemplate;
    private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";

    @Override
    public KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId) {
        MultiValueMap<String, String> oneTimeApproveParams =
            parameterProvider.getOneTimeApproveParams(tid, pgToken, orderId);

        return getResponseDtoAboutApprove(oneTimeApproveParams);
    }

    @Override
    public KakaoResponseDto.Approve approveSubscription(String tid, String pgToken, Long orderId) {
        MultiValueMap<String, String> subscriptionApproveParams =
            parameterProvider.getSubscriptionApproveParams(tid, pgToken, orderId);

        return getResponseDtoAboutApprove(subscriptionApproveParams);
    }

    private KakaoResponseDto.Approve getResponseDtoAboutApprove(
        MultiValueMap<String, String> params
    ) {
        HttpEntity<MultiValueMap<String, String>> entity
            = new HttpEntity<>(params, super.getHeaders());

        return restTemplate.postForObject(
            KAKAO_APPROVE_URL,
            entity,
            KakaoResponseDto.Approve.class
        );
    }
}