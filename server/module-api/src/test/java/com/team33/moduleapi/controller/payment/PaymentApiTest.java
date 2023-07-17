package com.team33.moduleapi.controller.payment;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.moduleapi.controller.ApiTest;
import com.team33.moduleapi.controller.UserAccount;
import com.team33.modulecore.domain.payment.kakao.dto.FailResponse;
import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto.Approve;
import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto.Request;
import com.team33.modulecore.domain.payment.kakao.service.KakaoPaymentFacade;
import com.team33.modulecore.global.util.Mapper;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

class PaymentApiTest extends ApiTest {

    @MockBean
    private KakaoPaymentFacade paymentFacade;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @DisplayName("카카오 단건, 정기 결제 요청 테스트")
    @UserAccount({"test", "010-0000-0000"})
    @Test
    void test1() throws Exception {
        Request request = fixtureMonkey.giveMeBuilder(Request.class)
            .set("tid", "test tid")
            .set("next_redirect_pc_url", "url")
            .sample();

        String token = getToken();

        given(paymentFacade.request(anyLong())).willReturn(request);

        ExtractableResponse<Response> response =
            //@formatter:off
            given()
                    .log().all()
                    .header("Authorization", token)
                    .pathParam("orderId",1)
            .when()
                    .post("/payments/{orderId}")
            .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .log().all().extract();
        //@formatter:on

        String tid = response.jsonPath().get("tid").toString();
        String next_redirect_pc_url = response.jsonPath().get("next_redirect_pc_url").toString();
        String createAt = response.jsonPath().get("createAt").toString();

        assertThat(tid).isNotNull();
        assertThat(next_redirect_pc_url).isNotNull();
        assertThat(createAt).isNotNull();
    }

    @DisplayName("카카오 단건, 정기 결제 승인(최초 시도) 테스트")
    @Test
    void test2() throws Exception {
        Approve approve = fixtureMonkey.giveMeOne(Approve.class);

        given(paymentFacade.approve(anyString(), anyLong()))
            .willReturn(approve);

        //@formatter:off
        ExtractableResponse<Response> response =
            given()
                    .log().all()
                    .pathParam("orderId",1)
                    .param("pg_token", "pgtoken")
            .when()
                    .get("/payments/approve/{orderId}")
            .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .log().all()
                    .extract();
            //@formatter:on

        String aid = response.jsonPath().get("aid").toString();
        String approvedAt = response.jsonPath().get("approved_at").toString();

        assertThat(aid).isNotNull();
        assertThat(approvedAt).isNotNull();
    }

    @DisplayName("정기 결제 승인(두 번째 이후) 테스트")
    @Test
    void test3() throws Exception {
        Approve approve = fixtureMonkey.giveMeOne(Approve.class);

        given(paymentFacade.approveSubscription(anyLong()))
            .willReturn(approve);

        ExtractableResponse<Response> response =
            //@formatter:off
            given()
                    .log().all()
                    .queryParam("orderId", 1)
            .when()
                    .get("/payments/kakao/subscription")
            .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .log().all()
                    .extract();
            //@formatter:on

        String approvedAt = response.jsonPath().get("approved_at").toString();
        assertThat(approvedAt).isNotNull();
    }

    @DisplayName("결제 승인 취소")
    @Test
    void test4() throws Exception {

        FailResponse failResponse = fixtureMonkey.giveMeBuilder(FailResponse.class)
            .set("code", -780)
            .set("msg", "approval failure!").sample();

        String error = Mapper.getInstance().writeValueAsString(failResponse);

        ExtractableResponse<Response> response =
            //@formatter:off
            given()
                .log().all()
                .body(error)
                .when()
                .get("/payments/kakao/cancel")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract();
        //@formatter:on

        int code = response.jsonPath().get("code");
        String msg = response.jsonPath().get("msg").toString();

        assertThat(code).isEqualTo(failResponse.getCode());
        assertThat(msg).isEqualTo(failResponse.getMsg());

    }

    @DisplayName("정기 결제 승인 실패")
    @Test
    void test5() throws Exception {

        FailResponse failResponse = fixtureMonkey.giveMeBuilder(FailResponse.class)
            .set("code", -780)
            .set("msg", "approval failure!").sample();

        String error = Mapper.getInstance().writeValueAsString(failResponse);

        ExtractableResponse<Response> response =
            //@formatter:off
            given()
                .log().all()
                .body(error)
                .when()
                .get("/payments/kakao/fail")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract();
        //@formatter:on

        int code = response.jsonPath().get("code");
        String msg = response.jsonPath().get("msg").toString();

        assertThat(code).isEqualTo(failResponse.getCode());
        assertThat(msg).isEqualTo(failResponse.getMsg());
    }
}
