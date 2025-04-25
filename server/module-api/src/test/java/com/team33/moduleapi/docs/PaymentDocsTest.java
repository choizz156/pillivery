package com.team33.moduleapi.docs;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.payment.PayController;
import com.team33.moduleapi.api.payment.mapper.PaymentData;
import com.team33.moduleapi.api.payment.mapper.PaymentDataMapper;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.payment.kakao.application.KakaoPaymentFacade;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@ActiveProfiles("test")
@ExtendWith({ RestDocumentationExtension.class, MockitoExtension.class })
public class PaymentDocsTest {


	@Mock
	private KakaoPaymentFacade kakaoPaymentFacade;

	@Mock
	private PaymentMapper paymentMapper;

	@Mock
	private PaymentDataMapper paymentDataMapper;

	@Mock
	private OrderStatusService orderStatusService;


	@Mock
	private OrderPaymentCodeService paymentCodeService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {

		PayController payController = new PayController(
			kakaoPaymentFacade,
			paymentMapper,
			paymentDataMapper,
			orderStatusService,
			paymentCodeService
		);
		
		mockMvc = MockMvcBuilders.standaloneSetup(payController)
			.apply(documentationConfiguration(restDocumentation)
				.operationPreprocessors()
				.withResponseDefaults(prettyPrint())
				.withRequestDefaults(prettyPrint())
			)
			.build();

		RestAssuredMockMvc.mockMvc(mockMvc);
	}

	@Test
	void 결제_요청_문서화() {

		KakaoRequestResponse response = FixtureMonkeyFactory.get()
				.giveMeBuilder(KakaoRequestResponse.class)
				.set("tid", "test_tid")
				.set("next_redirect_pc_url", "http://test.url")
				.sample();

		given(kakaoPaymentFacade.request(anyLong())).willReturn(response);

		doNothing().when(paymentDataMapper).addData(anyLong(), anyString());
		doNothing().when(paymentCodeService).addTid(anyLong(), anyString());

		RestAssuredMockMvc
				.given()
				.header("Authorization", "Bearer token")
				.when()
				.post("/api/payments/{orderId}", 1L)
				.then()
				.statusCode(201)
				.log().all()
				.apply(document("payment-request",
						requestHeaders(
								headerWithName("Authorization").description("Bearer 인증 토큰")),
						pathParameters(
								parameterWithName("orderId").description("주문 ID")),
						responseFields(
								fieldWithPath("data.tid").type(JsonFieldType.STRING).description("결제 고유 번호"),
								fieldWithPath("data.next_redirect_pc_url").type(JsonFieldType.STRING)
										.description("PC 결제 페이지 URL"),
								fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
								fieldWithPath("createTime").type(JsonFieldType.STRING).description("응답 생성 시간"))));
	}

	@Test
	void 정기결제_첫결제_요청_문서화() {

		KakaoRequestResponse response = FixtureMonkeyFactory.get()
				.giveMeBuilder(KakaoRequestResponse.class)
				.set("tid", "subscription_tid")
				.set("next_redirect_pc_url", "http://subscription.url")
				.sample();

		given(kakaoPaymentFacade.requestSubscription(anyLong())).willReturn(response);

		RestAssuredMockMvc
				.given()
				.header("Authorization", "Bearer token")
				.when()
				.post("/api/payments/subscriptionsFirst/{subscriptionOrderId}", 2L)
				.then()
				.statusCode(201)
				.apply(document("subscription-first-request",
					requestHeaders(
						headerWithName("Authorization").description("Bearer 인증 토큰")),
						pathParameters(
								parameterWithName("subscriptionOrderId").description("구독 주문 ID")),
						responseFields(
								fieldWithPath("data.tid").type(JsonFieldType.STRING).description("결제 고유 번호"),
								fieldWithPath("data.next_redirect_pc_url").type(JsonFieldType.STRING)
										.description("PC 결제 페이지 URL"),
								fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
								fieldWithPath("createTime").type(JsonFieldType.STRING).description("응답 생성 시간"))));
	}

	@Test
	void 단건결제_승인_문서화() {

		PaymentData paymentData = PaymentData.builder().tid("test_tid").targetId(1L).build();
		when(paymentDataMapper.getData(anyLong())).thenReturn(paymentData);

		KakaoApproveRequest kakaoApproveRequest = KakaoApproveRequest.of("test_tid", "test_pg_token", 1L);
		when(paymentMapper.toApproveOneTime(anyString(), anyString(), anyLong())).thenReturn(kakaoApproveRequest);

		KakaoApproveResponse kakaoApproveResponse = KakaoApproveResponse.builder()
				.aid("test_aid")
				.tid("test_tid")
				.item_code("test_item_code")
				.item_name("test_item_name")
				.created_at("2021-01-01T00:00:00")
				.approved_at("2021-01-01T00:00:00")
				.payload("test_payload")
				.amount(KakaoApproveResponse.Amount.builder().total(10000).vat(0).discount(0).tax_free(0).build())
				.build();

		when(kakaoPaymentFacade.approveInitially(any(KakaoApproveRequest.class))).thenReturn(kakaoApproveResponse);

		RestAssuredMockMvc
				.given()
				.param("pg_token", "test_pg_token")
				.when()
				.get("/api/payments/approve/{orderId}", 1L)
				.then()
				.statusCode(200)
				.log().all()
				.apply(
						document("approve-onetime",
								pathParameters(
										parameterWithName("orderId").description("주문 ID")),
								requestParameters(
										parameterWithName("pg_token").description("PG사 승인 토큰")),
								responseFields(
										fieldWithPath("data.item_name").type(JsonFieldType.STRING).optional()
												.description("상품명"),
										fieldWithPath("data.item_code").type(JsonFieldType.STRING).optional()
												.description("상품 코드"),
										fieldWithPath("data.created_at").type(JsonFieldType.STRING)
												.description("생성 시간"),
										fieldWithPath("data.approved_at").type(JsonFieldType.STRING)
												.description("승인 시간 "),
										fieldWithPath("data.payload").type(JsonFieldType.VARIES).optional()
												.description("추가 데이터"),
										fieldWithPath("data.amount.total").type(JsonFieldType.NUMBER)
												.description("총 결제 금액"),
										fieldWithPath("data.amount.tax_free").type(JsonFieldType.NUMBER)
												.description("비과세 금액"),
										fieldWithPath("data.amount.vat").type(JsonFieldType.NUMBER)
												.description("부가세 금액"),
										fieldWithPath("data.amount.discount").type(JsonFieldType.NUMBER)
												.description("할인 금액"),
										fieldWithPath("data.quantity").type(JsonFieldType.NUMBER).description("수량"),
										fieldWithPath("createTime").type(JsonFieldType.STRING)
												.description("응답 생성 시간 "))));
	}

	@Test
	void 최초_구독결제_승인_문서화() {

		PaymentData paymentData = PaymentData.builder().tid("test_tid").targetId(1L).build();
		when(paymentDataMapper.getData(anyLong())).thenReturn(paymentData);

		KakaoApproveRequest kakaoApproveRequest = KakaoApproveRequest.of("test_tid", "test_pg_token", 1L);
		when(paymentMapper.toApproveSubscribe(anyString(), anyString(), anyLong())).thenReturn(kakaoApproveRequest);

		KakaoApproveResponse kakaoApproveResponse = KakaoApproveResponse.builder()
				.aid("test_aid")
				.tid("test_tid")
				.sid("test_sid")
				.item_code("test_item_code")
				.item_name("test_item_name")
				.created_at("2021-01-01T00:00:00")
				.approved_at("2021-01-01T00:00:00")
				.payload("test_payload")
				.amount(KakaoApproveResponse.Amount.builder().total(10000).vat(0).discount(0).tax_free(0).build())
				.build();

		when(kakaoPaymentFacade.approveSid(any(KakaoApproveRequest.class))).thenReturn(kakaoApproveResponse);
		doNothing().when(paymentCodeService).addSid(anyLong(), anyString());

		RestAssuredMockMvc
				.given()
				.param("pg_token", "subscription_token")
				.when()
				.get("/api/payments/approve/subscriptionsFirst/{subscriptionOrderId}", 2L)
				.then()
				.log().all()
				.statusCode(200)
				.apply(
						document("subscription-first-approve",
								pathParameters(
										parameterWithName("subscriptionOrderId").description("주문 ID")),
								requestParameters(
										parameterWithName("pg_token").description("PG사 승인 토큰")),
								responseFields(
										fieldWithPath("data.item_name").type(JsonFieldType.STRING).optional()
												.description("상품명"),
										fieldWithPath("data.item_code").type(JsonFieldType.STRING).optional()
												.description("상품 코드"),
										fieldWithPath("data.created_at").type(JsonFieldType.STRING)
												.description("생성 시간"),
										fieldWithPath("data.approved_at").type(JsonFieldType.STRING)
												.description("승인 시간 "),
										fieldWithPath("data.payload").type(JsonFieldType.VARIES).optional()
												.description("추가 데이터"),
										fieldWithPath("data.amount.total").type(JsonFieldType.NUMBER)
												.description("총 결제 금액"),
										fieldWithPath("data.amount.tax_free").type(JsonFieldType.NUMBER)
												.description("비과세 금액"),
										fieldWithPath("data.amount.vat").type(JsonFieldType.NUMBER)
												.description("부가세 금액"),
										fieldWithPath("data.amount.discount").type(JsonFieldType.NUMBER)
												.description("할인 금액"),
										fieldWithPath("data.quantity").type(JsonFieldType.NUMBER).description("수량"),
										fieldWithPath("createTime").type(JsonFieldType.STRING)
												.description("응답 생성 시간 "))));
	}

	@Test
	void 구독_결제_승인_문서화() {

		KakaoApproveResponse kakaoApproveResponse = KakaoApproveResponse.builder()
				.aid("test_aid")
				.tid("test_tid")
				.sid("test_sid")
				.item_code("test_item_code")
				.item_name("test_item_name")
				.created_at("2021-01-01T00:00:00")
				.approved_at("2021-01-01T00:00:00")
				.payload("test_payload")
				.amount(KakaoApproveResponse.Amount.builder().total(10000).vat(0).discount(0).tax_free(0).build())
				.build();
		given(kakaoPaymentFacade.approveSubscription(anyLong())).willReturn(kakaoApproveResponse);

		RestAssuredMockMvc
				.when()
				.post("/api/payments/approve/subscriptions/{subscriptionOrderId}", 3L)
				.then()
				.log().all()
				.statusCode(200)
				.apply(document("subscription-approve",
						pathParameters(
								parameterWithName("subscriptionOrderId").description("주문 ID")),
						responseFields(
								fieldWithPath("data.item_name").type(JsonFieldType.STRING).optional()
										.description("상품명"),
								fieldWithPath("data.item_code").type(JsonFieldType.STRING).optional()
										.description("상품 코드"),
								fieldWithPath("data.created_at").type(JsonFieldType.STRING)
										.description("생성 시간"),
								fieldWithPath("data.approved_at").type(JsonFieldType.STRING)
										.description("승인 시간 "),
								fieldWithPath("data.payload").type(JsonFieldType.VARIES).optional()
										.description("추가 데이터"),
								fieldWithPath("data.amount.total").type(JsonFieldType.NUMBER).description("총 결제 금액"),
								fieldWithPath("data.amount.tax_free").type(JsonFieldType.NUMBER).description("비과세 금액"),
								fieldWithPath("data.amount.vat").type(JsonFieldType.NUMBER).description("부가세 금액"),
								fieldWithPath("data.amount.discount").type(JsonFieldType.NUMBER).description("할인 금액"),
								fieldWithPath("data.quantity").type(JsonFieldType.NUMBER).description("수량"),
								fieldWithPath("createTime").type(JsonFieldType.STRING)
										.description("응답 생성 시간 "))));
	}
}