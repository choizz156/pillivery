package com.team33.moduleapi.controller.payment;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.moduleapi.controller.ApiTest;
import com.team33.moduleapi.controller.UserAccount;
import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto.Approve;
import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto.Request;
import com.team33.modulecore.domain.payment.kakao.service.KakaoPaymentFacade;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

class PaymentApiDocs extends ApiTest {

    private String token;

    @MockBean
    private KakaoPaymentFacade paymentFacade;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @BeforeEach
    void setUp() {
        token = getToken();
    }

    @DisplayName("카카오 단건, 정기 결제 요청 테스트")
    @UserAccount({"test", "010-0000-0000"})
    @Test
    void test1() throws Exception {
        Request request = fixtureMonkey.giveMeOne(Request.class);

        given(paymentFacade.request(anyLong())).willReturn(request);

        ExtractableResponse<Response> response =
            //@formatter:off
            given(super.spec)
                    .log().all()
                    .header("Authorization", token)
                    .pathParam("orderId",1)
                    .filter(
                        document("payments",
                            preprocessRequest(modifyUris().scheme("http")
                                .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                                .removePort(), prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                parameterWithName("orderId").description("주문 아이디")
                            )
                        )
                    )
            .when()
                    .get("/payments/{orderId}")
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
    @UserAccount({"test", "010-0000-0000"})
    @Test
    void test2() throws Exception {
        Approve approve = fixtureMonkey.giveMeOne(Approve.class);

        given(paymentFacade.approve(anyString(), anyLong()))
            .willReturn(approve);

        //@formatter:off
        ExtractableResponse<Response> response =
            given(super.spec)
                        .log().all()
                        .pathParam("orderId",1)
                        .param("pg_token", "pgtoken")
                        .filter(
                            document("payments-approve",
                                preprocessRequest(modifyUris().scheme("http")
                                    .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                                    .removePort(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                    parameterWithName("orderId").description("주문 아이디")
                                ),
                                requestParameters(
                                    parameterWithName("pg_token").description("pg 토큰")
                                )
                            )
                        )
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
    @UserAccount({"test", "010-0000-0000"})
    @Test
    void test3() throws Exception {
        Approve approve = fixtureMonkey.giveMeOne(Approve.class);

        given(paymentFacade.approveSubscription(anyLong()))
            .willReturn(approve);

        ExtractableResponse<Response> response =
            //@formatter:off
            given(super.spec)
                        .log().all()
                        .pathParam("orderId", 1)
                        .filter(
                            document("payments-approve-subs",
                                preprocessRequest(modifyUris().scheme("http")
                                    .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                                    .removePort(), prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                    parameterWithName("orderId").description("주문 아이디")
                                )
                            )
                        )
                .when()
                        .get("/payments/subscription/{orderId}")
                .then()
                        .statusCode(HttpStatus.ACCEPTED.value())
                        .log().all()
                        .extract();
        //@formatter:on

        String approvedAt = response.jsonPath().get("approved_at").toString();
        assertThat(approvedAt).isNotNull();
    }
}
