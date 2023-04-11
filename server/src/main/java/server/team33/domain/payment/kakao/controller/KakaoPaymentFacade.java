package server.team33.domain.payment.kakao.controller;


import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import server.team33.domain.order.entity.Order;
import server.team33.domain.order.service.OrderService;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto.Approve;
import server.team33.domain.payment.kakao.service.KakaoPayApprove;
import server.team33.domain.payment.kakao.service.KakaoPayRequest;

@RequiredArgsConstructor
@Component
public class KakaoPaymentFacade {

    private final KakaoPayRequest kakaoPayRequestImpl;
    private final KakaoPayApprove kakaoPayApproveImpl;
    private final OrderService orderService;
    private final RestTemplate restTemplate;

    public KakaoResponseDto.Request request(Order order) {
        return order.isSubscription()
            ? kakaoPayRequestImpl.requestSubscription(order)
            : kakaoPayRequestImpl.requestOneTime(order);
    }

    public KakaoResponseDto.Approve approve(String tid, String pgToken, Order order) {
        if (order.isSubscription()) {
            Approve approve =
                kakaoPayApproveImpl.approveSubscription(tid, pgToken, order.getOrderId());
            
            order.addSid(approve.getSid());
            orderService.subsOrder(order.getOrderId());
            doKakaoScheduling(order.getOrderId());
            return approve;
        }

        Approve approve = kakaoPayApproveImpl.approveOneTime(tid, pgToken, order.getOrderId());
        orderService.completeOrder(order.getOrderId());
        return approve;
    }

    private void doKakaoScheduling(Long orderId) {

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();

        queryParam.add("orderId", String.valueOf(orderId));

        URI uri = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(9090)
            .path("/schedule/kakao")
            .queryParams(queryParam).build().toUri();

        restTemplate.getForObject(uri, String.class);
    }
}
