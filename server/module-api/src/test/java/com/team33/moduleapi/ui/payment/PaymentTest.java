package com.team33.moduleapi.ui.payment;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.controller.ApiTest;
import com.team33.moduleapi.controller.FixtureMonkeyFactory;
import com.team33.moduleapi.ui.payment.mapper.PaymentDataService;
import com.team33.moduleapi.ui.payment.mapper.PaymentMapper;
import com.team33.modulecore.order.application.OrderPaymentCodeService;
import com.team33.modulecore.order.application.OrderStatusService;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.payment.kakao.application.approve.KakaoApproveFacade;
import com.team33.modulecore.payment.kakao.application.request.KakaoRequestFacade;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentTest extends ApiTest {

	private MockMvcRequestSpecification given;
	private KakaoRequestFacade requestFacade;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentDataService paymentDataService;

	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private OrderPaymentCodeService paymentCodeService;
	private Order order;
	private KakaoApproveFacade approveFacade;

	@BeforeAll
	void beforeAll() {
		requestFacade = mock(KakaoRequestFacade.class);
		approveFacade = mock(KakaoApproveFacade.class);
		given = RestAssuredMockMvc.given()
			.mockMvc(
				standaloneSetup(
					new PayController(
						approveFacade,
						requestFacade,
						paymentMapper,
						paymentDataService,
						orderStatusService,
						paymentCodeService
					)
				).build()
			).log().all();

		order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.setNull("user")
			.sample();

		orderRepository.save(order);
	}

	@AfterAll
	void tearDown() {
		orderRepository.delete(order);
	}

	@DisplayName("카카오 단건, 정기 결제 요청을 보내면 tid, redirect_url 등을 받을 수 있다.")
	@Test
	void 결제_요청() throws Exception {

		//given
		KakaoRequestResponse sample = FixtureMonkeyFactory.get()
			.giveMeBuilder(KakaoRequestResponse.class)
			.set("tid", "tid")
			.set("next_redirect_pc_url", "url")
			.sample();

		given(requestFacade.request(anyLong())).willReturn(sample);

		//@formatter:off
            given
            .when()
                    .post("/payments/{orderId}",1)
            .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .expect(jsonPath("$.data.tid").value("tid"))
                    .expect(jsonPath("$.data.next_redirect_pc_url").value("url"))
                    .expect(jsonPath("$.data.createAt").isNotEmpty())
                    .log().all();
        //@formatter:on
	}

	@DisplayName("카카오 단건, 정기 결제(최초) 승인 시 결제 승인 정보를 받을 수 있다.")
	@Test
	void 결제_승인() throws Exception {
		//given
		KakaoApproveResponse kakaoApproveResponse = FixtureMonkeyFactory.get().giveMeOne(KakaoApproveResponse.class);

		given(approveFacade.approveFirst(any(KakaoApproveOneTimeRequest.class))).willReturn(kakaoApproveResponse);

		paymentDataService.addData(1L,"tid");

		//@formatter:off
            given
                    .param("pg_token", "pgtoken")
            .when()
                    .get("/payments/approve/{orderId}",1)
            .then()
                    .log().all()
				.expect(jsonPath("$.data.item_name").isNotEmpty())
				.expect(jsonPath("$.data.item_code").isNotEmpty())
				.expect(jsonPath("$.data.created_at").isNotEmpty())
				.expect(jsonPath("$.data.approved_at").isNotEmpty())
				.expect(jsonPath("$.data.payload").isNotEmpty())
				.expect(jsonPath("$.data.amount.total").isNotEmpty())
				.expect(jsonPath("$.data.amount.tax_free").isNotEmpty())
				.expect(jsonPath("$.data.amount.vat").isNotEmpty())
				.expect(jsonPath("$.data.amount.discount").isNotEmpty())
				.expect(jsonPath("$.data.quantity").isNotEmpty());
            //@formatter:on
	}

	@DisplayName("정기 결제 승인(두 번째 이후) 응답을 받을 수 있다.")
	@Test
	void test3() throws Exception {
		KakaoApproveResponse kakaoApproveResponse = FixtureMonkeyFactory.get().giveMeOne(KakaoApproveResponse.class);

	    given(approveFacade.approveSubscription(anyLong())).willReturn(kakaoApproveResponse);

	    //@formatter:off
            given
                    .queryParam("orderId", 1)
            .when()
                    .get("/payments/approve/subscriptions")
            .then()
					.log().all()
				.statusCode(HttpStatus.OK.value())
				.expect(jsonPath("$.data.item_name").isNotEmpty())
				.expect(jsonPath("$.data.item_code").isNotEmpty())
				.expect(jsonPath("$.data.created_at").isNotEmpty())
				.expect(jsonPath("$.data.approved_at").isNotEmpty())
				.expect(jsonPath("$.data.payload").isNotEmpty())
				.expect(jsonPath("$.data.amount.total").isNotEmpty())
				.expect(jsonPath("$.data.amount.tax_free").isNotEmpty())
				.expect(jsonPath("$.data.amount.vat").isNotEmpty())
				.expect(jsonPath("$.data.amount.discount").isNotEmpty())
				.expect(jsonPath("$.data.quantity").isNotEmpty());

		//@formatter:on
	}
	//
	// @DisplayName("결제 승인 취소")
	// @Test
	// void test4() throws Exception {
	//
	//     KakaoFailResponseDto kakaoFailResponse = fixtureMonkey.giveMeBuilder(KakaoFailResponseDto.class)
	//         .set("code", -780)
	//         .set("msg", "approval failure!").sample();
	//
	//     //@formatter:off
    //         given
    //                 .body(kakaoFailResponse)
    //         .when()
    //                 .get("/payments/kakao/cancel")
    //         .then()
    //                 .statusCode(HttpStatus.BAD_REQUEST.value())
    //                 .expect(jsonPath("$.code").value(-780))
    //                 .expect(jsonPath("$.msg").value("approval failure!"))
    //                 .log().all();
    //     //@formatter:on
	// }
	//
	// @DisplayName("정기 결제 승인 실패")
	// @Test
	// void test5() throws Exception {
	//
	//     KakaoFailResponseDto kakaoFailResponse = fixtureMonkey.giveMeBuilder(KakaoFailResponseDto.class)
	//         .set("code", -780)
	//         .set("msg", "approval failure!").sample();
	//
	//     //@formatter:off
    //         given
    //                 .body(kakaoFailResponse)
    //         .when()
    //                 .get("/payments/kakao/fail")
    //         .then()
    //                  .statusCode(HttpStatus.BAD_REQUEST.value())
    //                  .expect(jsonPath("$.code").value(-780))
    //                  .expect(jsonPath("$.msg").value("approval failure!"))
    //                  .log().all();
    //     //@formatter:on
	// }
}
