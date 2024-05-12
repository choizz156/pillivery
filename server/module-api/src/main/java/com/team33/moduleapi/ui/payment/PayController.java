package com.team33.moduleapi.ui.payment;

import static java.util.Optional.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.payment.kakao.application.PaymentFacade;
import com.team33.modulecore.payment.kakao.dto.KakaoResponseDto;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PayController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/{orderId}")
    public KakaoResponseDto.Request request(@PathVariable("orderId") Long orderId) {
        return ofNullable(paymentFacade.request(orderId))
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAYMENT_FAIL));
    }

    @GetMapping("/approve/{orderId}")
    public KakaoResponseDto.Approve approve(
        @RequestParam("pg_token") String pgToken,
        @PathVariable("orderId") Long orderId
    ) {
        return ofNullable(paymentFacade.approve(pgToken, orderId))
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAYMENT_FAIL));
    }

    //    @CrossOrigin(origins = "pillivery.s3-website.ap-northeast-2.amazonaws.com")
    @PostMapping("/kakao/subscription")
    public KakaoResponseDto.Approve subscription(@RequestParam Long orderId) {
        return paymentFacade.approveSubscription(orderId);
    }

//    @GetMapping("/kakao/cancel")
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public FailResponse cancel(@RequestBody String cancel) throws JsonProcessingException {
//        return Mapper.getInstance().readValue(cancel, FailResponse.class);
//    }
//
//    @GetMapping("/kakao/fail")
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public FailResponse fail(@RequestBody String fail) throws JsonProcessingException {
//        return Mapper.getInstance().readValue(fail, FailResponse.class);
//    }
}
