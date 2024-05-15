package com.team33.moduleapi.restdocs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import com.team33.moduleapi.ui.payment.PayController;
import com.team33.modulecore.payment.application.PaymentFacade;
import com.team33.modulecore.payment.kakao.application.KakaoPaymentFacade;
import com.team33.modulecore.payment.kakao.dto.KaKaoPayRequestDto;
import com.team33.modulecore.payment.kakao.dto.KaKaoResponseApproveDto;

import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.response.ExtractableResponse;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;


class PaymentApiDocs extends MockRestDocsSupport {

    private final PaymentFacade paymentFacade = mock(KakaoPaymentFacade.class);


    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @DisplayName("카카오 단건, 정기 결제 요청 테스트")
    @WithMockUser
    @Test
    void test1() throws Exception {

        KaKaoPayRequestDto request = fixtureMonkey.giveMeBuilder(KaKaoPayRequestDto.class)
            .set("tid", "testTid")
            .set("next_redirect_pc_url", "url")
            .sample();

        given(paymentFacade.request(anyLong())).willReturn(request);

        //@formatter:off
        ExtractableResponse<MockMvcResponse> response =
            super.givenSpec
            .when()
                    .post("/payments/{orderId}",1)
            .then()
                    .assertThat()
                    .apply(document("payments",
                        preprocessRequest(modifyUris().scheme("http")
                            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                            .removePort()
                        ),
                        pathParameters(
                            parameterWithName("orderId").description("주문 아이디")
                        ))
                    )
                    .statusCode(HttpStatus.OK.value())
                    .extract();

        //@formatter:on

        String tid = response.jsonPath().get("tid").toString();
        String next_redirect_pc_url = response.jsonPath().get("next_redirect_pc_url").toString();
        String createAt = response.jsonPath().get("createAt").toString();

        assertThat(tid).isNotNull();
        assertThat(next_redirect_pc_url).isNotNull();
        assertThat(createAt).isNotNull();
    }

    @DisplayName("카카오 단건, 정기 결제 승인(최초 시도) 테스트")
    @WithMockUser
    @Test
    void test2() throws Exception {

        KaKaoResponseApproveDto approve = fixtureMonkey.giveMeOne(KaKaoResponseApproveDto.class);

        given(paymentFacade.approve(anyString(), anyLong()))
            .willReturn(approve);

        //@formatter:off
        ExtractableResponse<MockMvcResponse> response =

            super.givenSpec
                    .param("pg_token", "pgtoken")
            .when()
                    .get("/payments/approve/{orderId}", 1)
            .then()
                    .assertThat()
                    .apply(document("payments-approve",
                            preprocessRequest(modifyUris().scheme("http")
                                .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                                .removePort()),
                            pathParameters(
                                parameterWithName("orderId").description("주문 아이디")
                            ),
                            requestParameters(
                                parameterWithName("pg_token").description("pg 토큰")
                            )
                        )
                    )
                    .statusCode(HttpStatus.OK.value())
                    .log().everything()
                    .extract();
        //@formatter:on

        String aid = response.jsonPath().get("aid").toString();
        String approvedAt = response.jsonPath().get("approved_at").toString();

        assertThat(aid).isNotNull();
        assertThat(approvedAt).isNotNull();
    }

    @DisplayName("정기 결제 승인(두 번째 이후) 테스트")
    @WithMockUser
    @Test
    void test3() throws Exception {
        KaKaoResponseApproveDto approve = fixtureMonkey.giveMeOne(KaKaoResponseApproveDto.class);

        given(paymentFacade.approveSubscription(anyLong()))
            .willReturn(approve);

        ExtractableResponse<MockMvcResponse> response =
            super.givenSpec
                .queryParam("orderId", 1).
                when()
                .post("/payments/kakao/subscription")
                .then()
                .assertThat()
                .apply(
                    document("payments-approve-subs",
                        preprocessRequest(modifyUris().scheme("http")
                            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                            .removePort()),
                        requestParameters(
                            parameterWithName("orderId").description("주문 아이디")
                        )
                    )
                )
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract();
        //@formatter:on

        String approvedAt = response.jsonPath().get("approved_at").toString();
        assertThat(approvedAt).isNotNull();
    }


    @Override
    protected Object testController() {
        return new PayController(paymentFacade);
    }
}
