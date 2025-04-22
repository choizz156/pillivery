package com.team33.moduleapi.docs;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.team33.moduleapi.api.payment.RefundController;
import com.team33.moduleapi.api.payment.dto.RefundDto;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.payment.domain.refund.RefundService;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class RefundControllerTest {

	private MockMvc mockMvc;

	@Mock
	private RefundService refundService;

	@Mock
	private PaymentMapper paymentMapper;

	@Mock
	private OrderStatusService orderStatusService;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {

		RefundController refundController = new RefundController(paymentMapper, refundService, orderStatusService);
		RestAssuredMockMvc.mockMvc(MockMvcBuilders.standaloneSetup(refundController)
			.apply(documentationConfiguration(restDocumentation)
				.operationPreprocessors()
				.withResponseDefaults(prettyPrint())
				.withRequestDefaults(prettyPrint())
			)
			.build());
	}

	@DisplayName("환불을 요청할 수 있다.")
	@Test
	void 환불_요청() throws Exception {
		// given


		RefundContext refundContext = RefundContext.builder()
			.cancelAmount(10000)
			.cancelTaxFreeAmount(0)
			.build();

		when(paymentMapper.toRefundContext(any())).thenReturn(refundContext);
		doNothing().when(refundService).refund(anyLong(), any(RefundContext.class));
		doNothing().when(orderStatusService).processCancel(anyLong());

		RestAssuredMockMvc
			.given()
			.header("Authorization", "Bearer token")
			.contentType("application/json")
			.body(new RefundDto())
			.when()
			.post("/api/payments/refund/{orderId}", 1L)
			.then()
			.log().all()
			.statusCode(201)
			.apply(
				document("refund-request",
					requestHeaders(
						headerWithName("Authorization").description("Bearer 인증 토큰")),
					requestFields(
						fieldWithPath("cancelAmount").type(JsonFieldType.NUMBER).description("환불 금액"),
						fieldWithPath("cancelTaxFreeAmount").type(JsonFieldType.NUMBER).description("면세 값"),
						fieldWithPath("cancelAvailableAmount").type(JsonFieldType.NUMBER).description("환불 가능 금액")),
					pathParameters(
						parameterWithName("orderId").description("주문 ID")
					),
					responseFields(
						fieldWithPath("data").type(JsonFieldType.STRING).description("반품 요청 완료"),
						fieldWithPath("createTime").type(JsonFieldType.STRING)
							.description("응답 생성 시간 ")))
			);
	}
}