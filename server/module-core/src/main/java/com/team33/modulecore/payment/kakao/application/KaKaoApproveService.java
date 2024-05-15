// package com.team33.modulecore.payment.kakao.application;
//
// import org.springframework.http.HttpEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.client.RestTemplate;
//
// import com.team33.modulecore.order.domain.entity.Order;
// import com.team33.modulecore.payment.application.PayApprove;
// import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
// import com.team33.modulecore.payment.kakao.infra.ParameterProvider;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Service
// public class KaKaoApproveService extends KaKaoApproveTemplate implements PayApprove<KaKaoApproveResponse> {
//
//     private final ParameterProvider parameterProvider;
//     private final RestTemplate restTemplate;
//     private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";
//     private static final String KAKAO_SUBSCRIPTION_URL = "https://kapi.kakao.com/v1/payment/subscription";
//
//     @Override
//     public KaKaoApproveResponse approveOneTime(String tid, String pgToken, Long orderId) {
//         var oneTimeApproveParams = parameterProvider.getOneTimeApproveParams(tid, pgToken, orderId);
//
//         return getResponseDtoAboutApprove(oneTimeApproveParams, KAKAO_APPROVE_URL);
//     }
//
//     @Override
//     public KaKaoApproveResponse approveFirstSubscription(
//         String tid,
//         String pgToken,
//         Long orderId
//     ) {
//         var firstSubscriptionApproveParams =
//             parameterProvider.getSubscriptionFirstApproveParams(tid, pgToken, orderId);
//
//         return getResponseDtoAboutApprove(firstSubscriptionApproveParams, KAKAO_APPROVE_URL);
//     }
//
//     @Override
//     public KaKaoApproveResponse approveSubscription(String sid, Order order) {
//         var subscriptionApproveParams = parameterProvider.getSubscriptionApproveParams(sid, order);
//         return getResponseDtoAboutApprove(subscriptionApproveParams, KAKAO_SUBSCRIPTION_URL);
//     }
//
//     private KaKaoApproveResponse getResponseDtoAboutApprove(
//         MultiValueMap<String, String> params,
//         String url
//     ) {
//         var entity = new HttpEntity<>(params, super.getHeaders());
//
//         return restTemplate.postForObject(
//             url,
//             entity,
//             KaKaoApproveResponse.class
//         );
//     }
// }
