package com.team33.moduleapi.exception.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;

class ExceptionControllerTest extends ApiTest {

	@DisplayName("MethodArgumentNotValidException 처리 테스트")
	@Test
	void methodArgumentNotValidExceptionTest() {

		TestRequest request = new TestRequest("", -1);

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(request)
			.when()
			.post("/test/validation")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("customFieldErrors", hasSize(2))
			.body("customFieldErrors.find { it.field == 'age' }.field", equalTo("age"))
			.body("customFieldErrors.find { it.field == 'age' }.rejectedValue", equalTo("-1"))
			.body("customFieldErrors.find { it.field == 'age' }.reason", containsString("1 이상이어야 합니다"))
			.body("customFieldErrors.find { it.field == 'name' }.field", equalTo("name"))
			.body("customFieldErrors.find { it.field == 'name' }.rejectedValue", equalTo(""))
			.body("customFieldErrors.find { it.field == 'name' }.reason", containsString("공백일 수 없습니다"));
	}

	@DisplayName("ConstraintViolationException 처리 테스트")
	@Test
	void constraintViolationExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/constraint/0")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", equalTo("constraintTest.id: 1 이상이어야 합니다"))
			.body("customFieldErrors", hasSize(1))
			.body("customFieldErrors.find { it.field == 'constraintTest.id' }.field", equalTo("constraintTest.id"))
			.body("customFieldErrors.find { it.field == 'constraintTest.id' }.rejectedValue", nullValue())
			.body("customFieldErrors.find { it.field == 'constraintTest.id' }.reason", containsString("1 이상이어야 합니다"));
	}

	@DisplayName("BusinessLogicException 처리 테스트")
	@Test
	void businessLogicExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/business-logic")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", containsString("비즈니스 로직 예외 발생"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("MethodArgumentTypeMismatchException 처리 테스트")
	@Test
	void methodArgumentTypeMismatchExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/type-mismatch/invalid")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", containsString("잘못된 파라미터 타입입니다."))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("MissingServletRequestParameterException 처리 테스트")
	@Test
	void missingServletRequestParameterExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/missing-param")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", containsString("requiredParam"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("IllegalArgumentException 처리 테스트")
	@Test
	void illegalArgumentExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/illegal-argument")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", containsString("잘못된 인자가 전달되었습니다"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("DataSaveException 처리 테스트")
	@Test
	void dataSaveExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/data-save")
			.then()
			.log().all()
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
			.body("message", containsString("알 수 없는 오류가 발생했습니다."))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("SubscriptionPaymentException 처리 테스트")
	@Test
	void subscriptionPaymentExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/subscription-payment")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", containsString("구독 결제 오류"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("PaymentApiException 처리 테스트")
	@Test
	void paymentApiExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/payment-api")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", containsString("결제 API 오류"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("RuntimeException 처리 테스트")
	@Test
	void runtimeExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/test/runtime")
			.then()
			.log().all()
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
			.body("message", contains("알 수 없는 오류가 발생했습니다."))
			.body("customFieldErrors", nullValue());
	}

}