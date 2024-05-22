package com.team33.moduleapi.ui.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.payment.dto.KaKaoApproveResponseDto;
import com.team33.moduleapi.ui.payment.dto.KaKaoPayNextUrlDto;
import com.team33.moduleapi.ui.payment.dto.PaymentData;
import com.team33.moduleapi.ui.payment.mapper.PaymentDataService;
import com.team33.moduleapi.ui.payment.mapper.PaymentMapper;
import com.team33.modulecore.order.application.OrderPaymentService;
import com.team33.modulecore.order.application.OrderCreateService;
import com.team33.modulecore.payment.application.approve.ApproveFacade;
import com.team33.modulecore.payment.application.request.RequestFacade;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PayController {

	private final ApproveFacade<KakaoApproveResponse, KakaoApproveOneTimeRequest> approveFacade;
	private final RequestFacade<KakaoRequestResponse> requestFacade;
	private final PaymentMapper paymentMapper;
	private final PaymentDataService paymentDataService;
	private final OrderPaymentService orderPaymentService;

	@PostMapping("/{orderId}")
	public SingleResponseDto<?> request(
		@PathVariable Long orderId
	) {
		KakaoRequestResponse requestResponse = requestFacade.request(orderId);

		paymentDataService.addData(orderId, requestResponse.getTid());
		orderPaymentService.addTid(orderId, requestResponse.getTid());

		return new SingleResponseDto<>(KaKaoPayNextUrlDto.from(requestResponse));
	}

	@GetMapping("/approve/subscription/{orderId}")
	public SingleResponseDto<?> subscription(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long orderId
	) {
		PaymentData data = paymentDataService.getData(orderId);

		KakaoApproveOneTimeRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());
		KakaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);

		orderPaymentService.changeOrderStatusToSubscribe(Long.valueOf(approve.getPartner_order_id()), approve.getSid());

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approve));
	}

	@GetMapping("/approve/{orderId}")
	public SingleResponseDto<?> success(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long orderId
	) {
		PaymentData data = paymentDataService.getData(orderId);

		KakaoApproveOneTimeRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KakaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);

		orderPaymentService.changeOrderStatusToComplete(Long.valueOf(approve.getPartner_order_id()));

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approve));
	}

	@GetMapping("/approve/subscription")
	public SingleResponseDto<?> subscription(
		@RequestParam("orderId") Long orderId
	) {
		KakaoApproveResponse kaKaoApproveResponse = approveFacade.approveSubscription(orderId);

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(kaKaoApproveResponse));
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
