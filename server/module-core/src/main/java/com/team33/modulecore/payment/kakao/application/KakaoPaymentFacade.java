// package com.team33.modulecore.payment.kakao.application;
//
// import java.net.URI;
// import java.util.Optional;
//
// import org.springframework.stereotype.Component;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.web.util.UriComponentsBuilder;
//
// import com.team33.modulecore.exception.BusinessLogicException;
// import com.team33.modulecore.exception.ExceptionCode;
// import com.team33.modulecore.order.application.OrderService;
// import com.team33.modulecore.order.domain.entity.Order;
// import com.team33.modulecore.order.domain.repository.OrderRepository;
// import com.team33.modulecore.payment.application.PayApprove;
// import com.team33.modulecore.payment.application.NormalApprove;
// import com.team33.modulecore.payment.application.NormalRequest;
// import com.team33.modulecore.payment.application.SubscriptionApprove;
// import com.team33.modulecore.payment.application.SubscriptionRequest;
// import com.team33.modulecore.payment.application.PaymentFacade;
// import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
// import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
// import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Component
// public class KakaoPaymentFacade implements PaymentFacade {
//
//     private final NormalRequest<KakaoRequestResponse> normalRequest;
//     private final NormalApprove<KaKaoApproveResponse> normalApprove;
//     private final SubscriptionRequest<KakaoRequestResponse> subscriptionRequest;
//     private final SubscriptionApprove<KaKaoApproveResponse> subscriptionApprove;
//     private final PayApprove<KaKaoApproveResponse> kakaoPayApprove;
//     private final OrderService orderService;
//     private final RestTemplate restTemplate;
//     private final OrderRepository orderRepository;
//
// 	@Override
//     public KakaoRequestResponse request(long orderId) {
//         Order order = findOrder(orderId);
//         if(order.isSubscription()){
//             return subscriptionRequest.requestSubscription(order);
//         }
//
//         KakaoRequestResponse request = normalRequest.requestOneTime(order);
//         order.addTid(request.getTid());
//         return request;
//     }
//
//     public KaKaoApproveResponse approve(KakaoApproveOneTimeRequest approveRequest) {
//         Long orderId = approveRequest.getOrderId();
//         String pgToken = approveRequest.getPgtoken();
//         Order order = findOrder(approveRequest.getOrderId());
//         String tid = order.getTid();
//         if (order.isSubscription()) {
//             KaKaoApproveResponse approve =
//                 kakaoPayApprove.approveFirstSubscription(tid, pgToken, orderId);
//
//             order.addSid(approve.getSid());
//             orderService.changeOrderStatusToSubscribe(orderId);
//             doKakaoScheduling(orderId);
//             return approve;
//         }
//
//         KaKaoApproveResponse approve = kakaoPayApprove.approveOneTime(tid, pgToken, orderId);
//         orderService.changeOrderStatusToComplete(orderId);
//         return approve;
//     }
//
//     @Override
//     public KaKaoApproveResponse approveSubscription(final Long orderId) {
//         Order order = findOrder(orderId);
//         String sid = order.getSid();
//
//         return kakaoPayApprove.approveSubscription(sid, order);
//     }
//
//     private void doKakaoScheduling(Long orderId) {
//
//         MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
//
//         queryParam.add("orderId", String.valueOf(orderId));
//
//         URI uri = UriComponentsBuilder.newInstance().scheme("http")
//             .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
//             .port(8080)
//             .path("/schedule")
//             .queryParams(queryParam).build().toUri();
//
//         restTemplate.getForObject(uri, String.class);
//     }
//
//     private Order findOrder(Long orderId) {
//         Optional<Order> orderOptional = orderRepository.findById(orderId);
//         return orderOptional.orElseThrow(
//             () -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND)
//         );
//     }
//
// }
