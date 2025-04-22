package com.team33.moduleapi.api.scheduler;

import static io.restassured.RestAssured.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.team33.moduleapi.docs.WebRestDocsSupport;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;

@AutoConfigureMockMvc
public class SubscriptionControllerTest extends WebRestDocsSupport {

    @MockBean
    private SubscriptionOrderService subscriptionOrderService;

    @UserAccount({ "test", "010-0000-0120" })
    @Test
    public void changePeriodTest() throws Exception {
        doNothing().when(subscriptionOrderService).changeItemPeriod(1, 1L);

        given(spec)
                .header("Authorization", getToken())
                .param("period", 60)
                .param("itemOrderId", 1)
                .filter(document("subscription-change-period",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰")),
                        requestParameters(
                                parameterWithName("period").description("변경할 구독 주기"),
                                parameterWithName("itemOrderId").description("주문 상품 ID"))))
                .when()
                .patch("/api/subscriptions")
                .then()
                .log().all()
                .statusCode(204);
    }

    @UserAccount({ "test", "010-0000-0490" })
    @Test
    public void deleteTest() throws Exception {
        doNothing().when(subscriptionOrderService).cancelSubscription(1L);

        given(spec)
                .header("Authorization", getToken())
                .param("itemOrderId", 1)
                .filter(document("subscription-cancel",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰")),
                        requestParameters(
                                parameterWithName("itemOrderId").description("주문 상품 ID"))))
                .when()
                .delete("/api/subscriptions")
                .then()
                .log().all()
                .statusCode(200);
    }
}