package com.team33.moduleapi.ui.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.ui.payment.dto.KaKaoApproveResponseDto;
import com.team33.moduleapi.ui.payment.dto.KaKaoPayNextUrlDto;
import com.team33.moduleapi.ui.payment.dto.PaymentData;
import com.team33.moduleapi.ui.payment.mapper.PaymentDataService;
import com.team33.moduleapi.ui.payment.mapper.PaymentMapper;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.payment.application.approve.ApproveFacade;
import com.team33.modulecore.payment.application.request.RequestFacade;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PayController {

	private final ApproveFacade<KaKaoApproveResponse, KakaoApproveOneTimeRequest> approveFacade;
	private final RequestFacade<KakaoRequestResponse> requestFacade;
	private final PaymentMapper paymentMapper;
	private final PaymentDataService paymentDataSerivce;
	private final OrderService orderService;

	@PostMapping("/{orderId}")
	public KaKaoPayNextUrlDto request(
		@PathVariable Long orderId
	) {
		KakaoRequestResponse requestResponse = requestFacade.request(orderId);
		paymentDataSerivce.addData(orderId, requestResponse.getTid());

		return KaKaoPayNextUrlDto.from(requestResponse);
	}

	@GetMapping("/approve/subscription/{orderId}")
	public KaKaoApproveResponseDto subscription(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long orderId
	) {
		PaymentData data = paymentDataSerivce.getData(orderId);

		KakaoApproveOneTimeRequest approveOneTimeRequest = paymentMapper.toApproveOneTime(data.getTid(), pgToken,
			data.getOrderId());
		KaKaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);

		orderService.changeOrderStatusToSubscribe(Long.valueOf(approve.getPartner_order_id()), approve.getSid());

		return KaKaoApproveResponseDto.from(approve);
	}

	@GetMapping("/approve/{orderId}")
	public KaKaoApproveResponseDto success(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long orderId
	) {
		PaymentData data = paymentDataSerivce.getData(orderId);

		KakaoApproveOneTimeRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KaKaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);

		orderService.changeOrderStatusToComplete(Long.valueOf(approve.getPartner_order_id()));

		return KaKaoApproveResponseDto.from(approve);
	}

	@PostMapping("/approve/subscription")
	public KaKaoApproveResponseDto subscription(
		@RequestParam("orderId") Long orderId
	) {
		KaKaoApproveResponse kaKaoApproveResponse = approveFacade.approveSubscription(orderId);

		return KaKaoApproveResponseDto.from(kaKaoApproveResponse);
	}

	@GetMapping("/cancel")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void cancel() {
	}

	@GetMapping("/fail")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void fail() {
	}
}
