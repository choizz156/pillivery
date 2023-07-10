package com.team33.moduleapi.controller.payment.kakao;

import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto;
import com.team33.modulecore.domain.payment.kakao.service.PaymentTypeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class kakaoPayController {

    private final PaymentTypeFacade kakaoPaymentFacade;

    @GetMapping("/kakao/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Request request(@PathVariable("orderId") Long orderId) {
        return kakaoPaymentFacade.request(orderId);
    }

    @GetMapping("/kakao/approve/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Approve approve(
        @RequestParam("pg_token") String pgToken,
        @PathVariable("orderId") Long orderId
    ) {
        return kakaoPaymentFacade.approve(pgToken, orderId);
    }

    @GetMapping("/kakao/subscription")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Approve subscription(@RequestParam Long orderId) {
       return kakaoPaymentFacade.approveSubscription(orderId);
    }
}