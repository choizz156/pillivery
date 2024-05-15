package com.team33.moduleapi.ui.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.modulecore.payment.application.ApproveFacade;
import com.team33.modulecore.payment.application.RequestFacade;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PayController {

    private final ApproveFacade<KaKaoApproveResponse, KakaoApproveOneTimeRequest> approveFacade;
    private final RequestFacade<KakaoRequestResponse> requestFacade;
    private final PaymentMapper paymentMapper;

    @PostMapping("/{orderId}")
    public KaKaoPayNextUrl request(@PathVariable("orderId") Long orderId) {
        KakaoRequestResponse requestResponse = requestFacade.request(orderId);

        return KaKaoPayNextUrl.from(requestResponse);
    }

    @GetMapping("/approve/{orderId}")
    public KaKaoApproveResponseDto approve(
        @RequestParam(name = "tid", required = false) String tid,
        @RequestParam("pg_token") String pgToken,
        @PathVariable("orderId") Long orderId
    ) {
        KakaoApproveOneTimeRequest approveOneTimeRequest = paymentMapper.toApproveOneTimeRequest(tid, pgToken, orderId);
        KaKaoApproveResponse approveResponse =  approveFacade.approve(approveOneTimeRequest);

        return KaKaoApproveResponseDto.from(approveResponse);
    }

    //    @CrossOrigin(origins = "pillivery.s3-website.ap-northeast-2.amazonaws.com")
    // @PostMapping("/kakao/subscription")
    // public KaKaoApproveResponseDto subscription(@RequestParam Long orderId) {
    //     return paymentFacade.approveSubscription(orderId);
    // }

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
