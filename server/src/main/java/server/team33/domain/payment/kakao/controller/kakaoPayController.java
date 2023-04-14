package server.team33.domain.payment.kakao.controller;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import server.team33.domain.order.entity.Order;
import server.team33.domain.order.reposiroty.OrderRepository;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto.Request;
import server.team33.global.exception.bussiness.BusinessLogicException;
import server.team33.global.exception.bussiness.ExceptionCode;


@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class kakaoPayController {

    private final KakaoPaymentFacade kakaoPaymentFacade;
    private final OrderRepository orderRepository;

    public static final ExpiringMap<Long, String> tidStore =
        ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(3, TimeUnit.MINUTES)
            .build();

    @GetMapping("/kakao/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Request requestPayment(@PathVariable("orderId") Long orderId) {
        Order order = findOrder(orderId);
        Request response = kakaoPaymentFacade.request(order);
        saveTid(response, orderId);

        return response;
    }

    @GetMapping("/kakao/approve/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Approve kakaoApprove(
        @RequestParam("pg_token") String pgToken,
        @PathVariable("orderId") Long orderId
    ) {
        Order order = findOrder(orderId);
        String tid = getTid(orderId);
        return kakaoPaymentFacade.approve(tid, pgToken, order);
    }


    //TODO: 정기결제
//    @GetMapping("/kakao/subscription")
//    public ResponseEntity subscriptionByKaKao(@RequestParam Long orderId) {
//        KakaoPayApproveDto kakaoPayApproveDto = getKakaoPayApproveDto(orderId);
//        orderService.subsOrder(orderId);
//
//        return new ResponseEntity<>(kakaoPayApproveDto, HttpStatus.CREATED);
//    }

    @GetMapping("/cancel")
    public ResponseEntity cancel() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/fail")
    public ResponseEntity fail() {
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    private void saveTid(Request requestResponse, Long orderId) {
        tidStore.put(orderId, requestResponse.getTid());
    }

    private Order findOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        return orderOptional.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND)
        );
    }

    private String getTid(Long orderId) {
        String tid = tidStore.get(orderId);
        if (tid == null) {
            throw new BusinessLogicException(ExceptionCode.EXPIRED_TID);
        }
        tidStore.remove(orderId);
        return tid;
    }
}