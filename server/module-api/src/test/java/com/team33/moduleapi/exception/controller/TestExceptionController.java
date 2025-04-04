package com.team33.moduleapi.exception.controller;

import static org.mockito.Mockito.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.DataSaveException;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.exception.SubscriptionPaymentException;

@Validated
@RestController
@RequestMapping("/test")
public class TestExceptionController {

	@PostMapping("/validation")
	public String validationTest(@Valid @RequestBody TestRequest request) {

		return "성공";
	}

	@GetMapping("/constraint/{id}")
	public String constraintTest(@PathVariable @Min(1) int id) {

		return "성공";
	}

	@GetMapping("/business-logic")
	public String businessLogicTest() {

		throw new BusinessLogicException("비즈니스 로직 예외 발생");
	}

	@GetMapping("/type-mismatch/{id}")
	public String typeMismatchTest(@PathVariable int id) {

		return "성공";
	}

	@GetMapping("/missing-param")
	public String missingParamTest(@RequestParam String requiredParam) {

		return "성공";
	}

	@GetMapping("/illegal-argument")
	public String illegalArgumentTest() {

		throw new IllegalArgumentException("잘못된 인자가 전달되었습니다");
	}

	@GetMapping("/data-save")
	public String dataSaveTest() {

		throw new DataSaveException("데이터 저장 오류");
	}

	@GetMapping("/subscription-payment")
	public String subscriptionPaymentTest() {

		SubscriptionPaymentException exception = mock(SubscriptionPaymentException.class);
		when(exception.getMessage()).thenReturn("구독 결제 실패");
		when(exception.getErrorBody()).thenReturn("구독 결제 오류");
		throw exception;
	}

	@GetMapping("/payment-api")
	public String paymentApiTest() {

		throw new PaymentApiException("결제 API 오류");
	}

	@GetMapping("/runtime")
	public String runtimeTest() {

		throw new RuntimeException("런타임 예외 발생");
	}
}
