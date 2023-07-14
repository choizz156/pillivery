package com.team33.moduleapi.controller.payment;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.domain.payment.kakao.dto.FailResponse;
import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto;
import com.team33.modulecore.domain.payment.kakao.service.PaymentFacade;
import com.team33.modulecore.global.exception.BusinessLogicException;
import com.team33.modulecore.global.exception.ExceptionCode;
import com.team33.modulecore.global.util.Mapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PayController {

    private final PaymentFacade paymentFacade;
    private final ObjectMapper objectMapper;

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Request request(@PathVariable("orderId") Long orderId) {
        return ofNullable(paymentFacade.request(orderId))
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAYMENT_FAIL));
    }

    @GetMapping("/approve/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Approve approve(
        @RequestParam("pg_token") String pgToken,
        @PathVariable("orderId") Long orderId
    ) {
        return ofNullable(paymentFacade.approve(pgToken, orderId))
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAYMENT_FAIL));
    }

    //    @CrossOrigin(origins = "pillivery.s3-website.ap-northeast-2.amazonaws.com")
    @GetMapping("/subscription/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public KakaoResponseDto.Approve subscription(@PathVariable Long orderId)
        throws JobExecutionException {
        return ofNullable(paymentFacade.approveSubscription(orderId))
            .orElseThrow(JobExecutionException::new);
    }

    @GetMapping("/kakao/cancel")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FailResponse cancel(@RequestBody String cancel) throws JsonProcessingException {
      return Mapper.getInstance().readValue(cancel, FailResponse.class);
    }

    @GetMapping("/kakao/fail")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FailResponse fail(@RequestBody String fail) throws JsonProcessingException {
        return objectMapper.readValue(fail, FailResponse.class);
    }
}
