package com.team33.moduleapi.ui.payment;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.ui.payment.dto.KaKaoApproveResponseDto;
import com.team33.moduleapi.ui.payment.dto.KaKaoPayNextUrlDto;
import com.team33.moduleapi.ui.payment.mapper.PaymentMapper;
import com.team33.modulecore.payment.application.approve.ApproveFacade;
import com.team33.modulecore.payment.application.request.RequestFacade;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PayController {

	private final ApproveFacade<KaKaoApproveResponse, KakaoApproveOneTimeRequest> approveFacade;
	private final RequestFacade<KakaoRequestResponse> requestFacade;
	private final PaymentMapper paymentMapper;

	@PostMapping("/{orderId}")
	public KaKaoPayNextUrlDto request(
		@PathVariable Long orderId
	) {
		KakaoRequestResponse requestResponse = requestFacade.request(orderId);

		return KaKaoPayNextUrlDto.from(requestResponse);
	}

	@PostMapping("/{orderId}/approve")
	public KaKaoApproveResponseDto approve(
		@PathVariable Long orderId,
		@RequestParam String tid,
		@RequestParam("pg_token") String pgToken
	) {
		KakaoApproveOneTimeRequest approveOneTimeRequest = paymentMapper.toApproveOneTimeRequest(tid, pgToken, orderId);
		KaKaoApproveResponse approveResponse = approveFacade.approveFirstTime(approveOneTimeRequest);

		return KaKaoApproveResponseDto.from(approveResponse);
	}

	@PostMapping("/{orderId}/approve/subscription")
	public KaKaoApproveResponseDto subscription(
		@PathVariable Long orderId
	) {
		KaKaoApproveResponse approveResponse = approveFacade.approveSubscription(orderId);

		return KaKaoApproveResponseDto.from(approveResponse);
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
