package server.team33.domain.payment.kakao.service;


import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import server.team33.domain.order.entity.Order;
import server.team33.domain.order.reposiroty.OrderRepository;
import server.team33.domain.order.service.OrderService;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto.Approve;
import server.team33.global.exception.BusinessLogicException;
import server.team33.global.exception.ExceptionCode;

@RequiredArgsConstructor
@Component
public class KakaoPaymentFacade implements PaymentTypeFacade {

    private final PayRequest kakaoPayRequest;
    private final PayApprove kakaoPayApprove;
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    @Override
    public KakaoResponseDto.Request request(long orderId) {
        Order order = findOrder(orderId);
        return order.isSubscription()
            ? kakaoPayRequest.requestSubscription(order)
            : kakaoPayRequest.requestOneTime(order);
    }

    @Override
    public KakaoResponseDto.Approve approve(String tid, String pgToken, Long orderId) {
        Order order = findOrder(orderId);
        if (order.isSubscription()) {
            Approve approve =
                kakaoPayApprove.approveFirstSubscription(tid, pgToken, orderId);

            order.addSid(approve.getSid());
            orderService.subsOrder(orderId);
            doKakaoScheduling(orderId);
            return approve;
        }

        Approve approve = kakaoPayApprove.approveOneTime(tid, pgToken, orderId);
        orderService.completeOrder(orderId);
        return approve;
    }

    @Override
    public Approve approveSubscription(final Long orderId) {
        Order order = findOrder(orderId);

        String sid = order.getSid();
        return kakaoPayApprove.approveSubscription(sid, order);
    }

    private void doKakaoScheduling(Long orderId) {

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();

        queryParam.add("orderId", String.valueOf(orderId));

        URI uri = UriComponentsBuilder.newInstance().scheme("http")
            .host("localhost")
            .port(9090)
            .path("/schedule")
            .queryParams(queryParam).build().toUri();

        restTemplate.getForObject(uri, String.class);
    }

    private Order findOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        return orderOptional.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND)
        );
    }
}
