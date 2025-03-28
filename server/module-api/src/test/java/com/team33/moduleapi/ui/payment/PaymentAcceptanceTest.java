package com.team33.moduleapi.ui.payment;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.payment.PayController;
import com.team33.moduleapi.api.payment.mapper.PaymentDataService;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoApproveFacade;
import com.team33.modulecore.core.payment.kakao.application.events.ScheduleRegisteredEvent;
import com.team33.modulecore.core.payment.kakao.application.request.KakaoRequestFacade;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.moduleevent.handler.ScheduleRegisterHandler;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentAcceptanceTest extends ApiTest {

	private MockMvcRequestSpecification given;
	private KakaoRequestFacade requestFacade;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentDataService paymentDataService;

	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private OrderPaymentCodeService paymentCodeService;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@MockBean
	private ScheduleRegisterHandler scheduleRegisterHandler;


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

	}

	@BeforeEach
	void setUp() {
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.sample();

		orderCommandRepository.save(order);
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
                    .post("/api/payments/{orderId}",1)
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

		given(approveFacade.approveInitially(any(KakaoApproveRequest.class))).willReturn(kakaoApproveResponse);

		paymentDataService.addData(1L, "tid");

		//@formatter:off
            given
                    .param("pg_token", "pgtoken")
            .when()
                    .get("/api/payments/approve/{orderId}",1)
            .then()
                    .log().all()
				.expect(jsonPath("$.data.item_name").exists())
				.expect(jsonPath("$.data.item_code").exists())
				.expect(jsonPath("$.data.created_at").exists())
				.expect(jsonPath("$.data.approved_at").exists())
				.expect(jsonPath("$.data.payload").exists())
				.expect(jsonPath("$.data.amount.total").exists())
				.expect(jsonPath("$.data.amount.tax_free").exists())
				.expect(jsonPath("$.data.amount.vat").exists())
				.expect(jsonPath("$.data.amount.discount").exists())
				.expect(jsonPath("$.data.quantity").exists());
            //@formatter:on
	}

	@DisplayName("정기 결제 승인(두 번째 이후) 응답을 받을 수 있다.")
	@Test
	void test3() throws Exception {
		//given
		KakaoApproveResponse kakaoApproveResponse = FixtureMonkeyFactory.get().giveMeOne(KakaoApproveResponse.class);

		given(approveFacade.approveSubscription(anyLong())).willReturn(kakaoApproveResponse);

		//@formatter:off
            given
            .when()
                    .post("/api/payments/approve/subscriptions/{orderId}", 1)
            .then()
					.log().all()
				.statusCode(HttpStatus.OK.value())
				.expect(jsonPath("$.data.item_name").exists())
				.expect(jsonPath("$.data.item_code").exists())
				.expect(jsonPath("$.data.created_at").exists())
				.expect(jsonPath("$.data.approved_at").exists())
				.expect(jsonPath("$.data.payload").exists())
				.expect(jsonPath("$.data.amount.total").exists())
				.expect(jsonPath("$.data.amount.tax_free").exists())
				.expect(jsonPath("$.data.amount.vat").exists())
				.expect(jsonPath("$.data.amount.discount").exists())
				.expect(jsonPath("$.data.quantity").exists());

		//@formatter:on
	}

	@DisplayName("스케쥴 등록 이벤트가 발행되면, 이벤트 핸들로가 동작한다.")
	@Test
	void 스케쥴_등록_이벤트() throws Exception {
		//given
		ScheduleRegisteredEvent scheduleRegisteredEvent = new ScheduleRegisteredEvent(1L);

		//when
		applicationEventPublisher.publishEvent(scheduleRegisteredEvent);

		//then
		verify(scheduleRegisterHandler, times(1)).onEventSet(scheduleRegisteredEvent);
	}
}
