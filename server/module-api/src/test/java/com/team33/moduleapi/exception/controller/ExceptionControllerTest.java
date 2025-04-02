package com.team33.moduleapi.exception.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.ApiTest;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.DataSaveException;
import com.team33.moduleexternalapi.config.SubscriptionPaymentException;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.AllArgsConstructor;
import lombok.Data;

class ExceptionControllerTest extends ApiTest {

	@BeforeEach
	void setUp() {

		basePath = "/test";
	}

	@DisplayName("MethodArgumentNotValidException 처리 테스트")
	@Test
	void methodArgumentNotValidExceptionTest() {

		TestRequest request = new TestRequest("", -1);

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(request)
			.when()
			.post("/validation")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("customFieldErrors", hasSize(2))
			.body("customFieldErrors.find { it.field == 'age' }.field", equalTo("age"))
			.body("customFieldErrors.find { it.field == 'age' }.rejectedValue", equalTo("-1"))
			.body("customFieldErrors.find { it.field == 'age' }.reason", equalTo("1 이상이어야 합니다"))
			.body("customFieldErrors.find { it.field == 'name' }.field", equalTo("name"))
			.body("customFieldErrors.find { it.field == 'name' }.rejectedValue", equalTo(""))
			.body("customFieldErrors.find { it.field == 'name' }.reason", equalTo("공백일 수 없습니다"));
	}

	@DisplayName("ConstraintViolationException 처리 테스트")
	@Test
	void constraintViolationExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/constraint/0")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", equalTo("constraintTest.id: 1 이상이어야 합니다"))
			.body("customFieldErrors", hasSize(1))
			.body("customFieldErrors.find { it.field == 'constraintTest.id' }.field", equalTo("constraintTest.id"))
			.body("customFieldErrors.find { it.field == 'constraintTest.id' }.rejectedValue", nullValue())
			.body("customFieldErrors.find { it.field == 'constraintTest.id' }.reason", equalTo("1 이상이어야 합니다"));
	}

	@DisplayName("BusinessLogicException 처리 테스트")
	@Test
	void businessLogicExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/business-logic")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", equalTo("비즈니스 로직 예외 발생"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("MethodArgumentTypeMismatchException 처리 테스트")
	@Test
	void methodArgumentTypeMismatchExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/type-mismatch/invalid")
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
			.get("/missing-param")
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
			.get("/illegal-argument")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", equalTo("잘못된 인자가 전달되었습니다"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("DataSaveException 처리 테스트")
	@Test
	void dataSaveExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/data-save")
			.then()
			.log().all()
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
			.body("message", equalTo("알 수 없는 오류가 발생했습니다."))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("SubscriptionPaymentException 처리 테스트")
	@Test
	void subscriptionPaymentExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/subscription-payment")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", equalTo("구독 결제 오류"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("PaymentApiException 처리 테스트")
	@Test
	void paymentApiExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/payment-api")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
			.body("message", equalTo("결제 API 오류"))
			.body("customFieldErrors", nullValue());
	}

	@DisplayName("RuntimeException 처리 테스트")
	@Test
	void runtimeExceptionTest() {

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/runtime")
			.then()
			.log().all()
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
			.body("message", equalTo("알 수 없는 오류가 발생했습니다."))
			.body("customFieldErrors", nullValue());
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		public TestExceptionController testExceptionController() {
			return new TestExceptionController();
		}
	}


	@Data
	@AllArgsConstructor
	static class TestRequest {

		@NotBlank(message = "공백일 수 없습니다")
		private String name;

		@Min(value = 1, message = "1 이상이어야 합니다")
		private int age;

	}

	@Validated
	@RestController
	@RequestMapping("/test")
	static class TestExceptionController {

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
			// SubscriptionPaymentException 모의 객체 생성
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
} 