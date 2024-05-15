// package com.team33.moduleapi.controller.payment;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.test.context.support.WithMockUser;
//
// import com.team33.moduleapi.ui.payment.KaKaoPayRequestDto;
// import com.team33.moduleapi.ui.payment.KaKaoResponseApproveDto;
// import com.team33.moduleapi.ui.payment.KakaoFailResponse;
// import com.team33.moduleapi.ui.payment.PayController;
// import com.team33.modulecore.payment.kakao.application.KakaoPaymentFacade;
//
// import io.restassured.module.mockmvc.RestAssuredMockMvc;
// import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
//
// import com.navercorp.fixturemonkey.FixtureMonkey;
// import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
//
// class PaymentApiTest {
//
//     private MockMvcRequestSpecification given;
//
//     private final KakaoPaymentFacade paymentFacade = mock(KakaoPaymentFacade.class);
//
//     private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
//         .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
//         .defaultNotNull(true)
//         .build();
//
//     @BeforeEach
//     void setUp() {
//         given = RestAssuredMockMvc.given()
//             .mockMvc(standaloneSetup(new PayController(paymentFacade)).build())
//             .log().all();
//     }
//
//     @DisplayName("카카오 단건, 정기 결제 요청 테스트")
//     @WithMockUser
//     @Test
//     void test1() throws Exception {
//         //given
//         KaKaoPayRequestDto request = fixtureMonkey.giveMeBuilder(KaKaoPayRequestDto.class)
//             .set("tid", "testTid")
//             .set("next_redirect_pc_url", "url")
//             .sample();
//
//         given(paymentFacade.request(anyLong())).willReturn(request);
//
//         //@formatter:off
//             given
//             .when()
//                     .post("/payments/{orderId}",1)
//             .then()
//                     .statusCode(HttpStatus.OK.value())
//                     .expect(jsonPath("$.tid").value("testTid"))
//                     .expect(jsonPath("$.next_redirect_pc_url").value("url"))
//                     .expect(jsonPath("$.createAt").isNotEmpty())
//                     .log().all();
//         //@formatter:on
//     }
//
//     @DisplayName("카카오 단건, 정기 결제 승인(최초 시도) 테스트")
//     @Test
//     void test2() throws Exception {
//         KaKaoResponseApproveDto approve = fixtureMonkey.giveMeOne(KaKaoResponseApproveDto.class);
//
//         given(paymentFacade.approve(anyString(), anyLong())).willReturn(approve);
//
//         //@formatter:off
//             given
//                     .param("pg_token", "pgtoken")
//             .when()
//                     .get("/payments/approve/{orderId}",1)
//             .then()
//                     .log().all()
//                     .statusCode(HttpStatus.OK.value())
//                     .expect(jsonPath("$.aid").isNotEmpty())
//                     .expect(jsonPath("$.approved_at").isNotEmpty());
//             //@formatter:on
//     }
//
//     @DisplayName("정기 결제 승인(두 번째 이후) 테스트")
//     @Test
//     void test3() throws Exception {
//         KaKaoResponseApproveDto approve = fixtureMonkey
//             .giveMeBuilder(KaKaoResponseApproveDto.class)
//             .set("approved_at", "test_approve")
//             .sample();
//
//         given(paymentFacade.approveSubscription(anyLong()))
//             .willReturn(approve);
//
//         //@formatter:off
//             given
//                     .queryParam("orderId", 1)
//             .when()
//                     .post("/payments/kakao/subscription")
//             .then()
//                     .statusCode(HttpStatus.OK.value())
//                     .expect(jsonPath("$.approved_at").isNotEmpty())
//                     .log().all();
//             //@formatter:on
//     }
//
//     @DisplayName("결제 승인 취소")
//     @Test
//     void test4() throws Exception {
//
//         KakaoFailResponse kakaoFailResponse = fixtureMonkey.giveMeBuilder(KakaoFailResponse.class)
//             .set("code", -780)
//             .set("msg", "approval failure!").sample();
//
//         //@formatter:off
//             given
//                     .body(kakaoFailResponse)
//             .when()
//                     .get("/payments/kakao/cancel")
//             .then()
//                     .statusCode(HttpStatus.BAD_REQUEST.value())
//                     .expect(jsonPath("$.code").value(-780))
//                     .expect(jsonPath("$.msg").value("approval failure!"))
//                     .log().all();
//         //@formatter:on
//     }
//
//     @DisplayName("정기 결제 승인 실패")
//     @Test
//     void test5() throws Exception {
//
//         KakaoFailResponse kakaoFailResponse = fixtureMonkey.giveMeBuilder(KakaoFailResponse.class)
//             .set("code", -780)
//             .set("msg", "approval failure!").sample();
//
//         //@formatter:off
//             given
//                     .body(kakaoFailResponse)
//             .when()
//                     .get("/payments/kakao/fail")
//             .then()
//                      .statusCode(HttpStatus.BAD_REQUEST.value())
//                      .expect(jsonPath("$.code").value(-780))
//                      .expect(jsonPath("$.msg").value("approval failure!"))
//                      .log().all();
//         //@formatter:on
//     }
// }
