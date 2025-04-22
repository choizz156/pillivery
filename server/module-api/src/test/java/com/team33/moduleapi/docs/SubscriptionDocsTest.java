package com.team33.moduleapi.docs;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.team33.moduleapi.api.scheduler.SubscriptionController;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class SubscriptionDocsTest {

	private static final String BASE_URL = "/api/subscriptions";

	@Mock
	private SubscriptionOrderService subscriptionOrderService;

	@UserAccount({"test", "010-0000-0120"})
	@Test
	public void 구독_주기_변화() throws Exception {

		int newPeriod = 60;
		long itemOrderId = 1L;
		doNothing().when(subscriptionOrderService).changeItemPeriod(newPeriod, itemOrderId);

		RestAssuredMockMvc.given()
			.header("Authorization", "Bearer token")
			.param("period", newPeriod)
			.param("itemOrderId", 1)
			.when()
			.patch(BASE_URL)
			.then()
			.log()
			.all()
			.statusCode(204)
			.apply(document("subscription-change-period",
				requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
				requestParameters(parameterWithName("period").description("변경할 구독 주기"),
					parameterWithName("itemOrderId").description("주문 상품 ID")))
			);

	}

	@UserAccount({"test", "010-0000-0490"})
	@Test
	public void 구독_취소() throws Exception {

		doNothing().when(subscriptionOrderService).cancelSubscription(1L);

		RestAssuredMockMvc.given()
			.header("Authorization", "Bearer token")
			.param("itemOrderId", 1)
			.when()
			.delete(BASE_URL)
			.then()
			.log().all()
			.statusCode(200)
			.apply(document("subscription-cancel",
					requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
					requestParameters(parameterWithName("itemOrderId").description("주문 상품 ID")),
				responseFields(
					fieldWithPath("createTime").type(JsonFieldType.STRING).description("응답 생성 시간"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 없음"))
				)
			);
	}

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {

		var subscriptionController = new SubscriptionController(subscriptionOrderService);

		RestAssuredMockMvc.mockMvc(MockMvcBuilders.standaloneSetup(subscriptionController)
			.apply(documentationConfiguration(restDocumentation)
				.operationPreprocessors()
				.withResponseDefaults(prettyPrint())
				.withRequestDefaults(prettyPrint())
			)
			.build());
	}

}
