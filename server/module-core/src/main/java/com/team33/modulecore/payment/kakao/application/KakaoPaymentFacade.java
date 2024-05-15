package com.team33.modulecore.payment.kakao.application;

import java.net.URI;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.payment.application.PayApprove;
import com.team33.modulecore.payment.application.PayRequest;
import com.team33.modulecore.payment.application.PaymentFacade;
import com.team33.modulecore.payment.kakao.dto.KaKaoPayRequestDto;
import com.team33.modulecore.payment.kakao.dto.KaKaoResponseApproveDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoPaymentFacade implements PaymentFacade {

    private final PayRequest<KaKaoPayRequestDto> kakaoPayRequest;
    private final PayApprove<KaKaoResponseApproveDto> kakaoPayApprove;
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

	@SuppressWarnings("unchecked")
	@Override
    public KaKaoPayRequestDto request(long orderId) {
        Order order = findOrder(orderId);
        if(order.isSubscription()){
            return kakaoPayRequest.requestSubscription(order);
        }

        KaKaoPayRequestDto request = kakaoPayRequest.requestOneTime(order);
        order.addTid(request.getTid());
        return request;
    }

    @SuppressWarnings("unchecked")
	@Override
    public KaKaoResponseApproveDto approve(String pgToken, Long orderId) {
        Order order = findOrder(orderId);
        String tid = order.getTid();
        if (order.isSubscription()) {
            KaKaoResponseApproveDto approve =
                kakaoPayApprove.approveFirstSubscription(tid, pgToken, orderId);

            order.addSid(approve.getSid());
            orderService.changeOrderStatusToSubscribe(orderId);
            doKakaoScheduling(orderId);
            return approve;
        }

        KaKaoResponseApproveDto approve = kakaoPayApprove.approveOneTime(tid, pgToken, orderId);
        orderService.changeOrderStatusToComplete(orderId);
        return approve;
    }

    @Override
    public KaKaoResponseApproveDto approveSubscription(final Long orderId) {
        Order order = findOrder(orderId);
        String sid = order.getSid();

        return kakaoPayApprove.approveSubscription(sid, order);
    }

    private void doKakaoScheduling(Long orderId) {

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();

        queryParam.add("orderId", String.valueOf(orderId));

        URI uri = UriComponentsBuilder.newInstance().scheme("http")
            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
            .port(8080)
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
