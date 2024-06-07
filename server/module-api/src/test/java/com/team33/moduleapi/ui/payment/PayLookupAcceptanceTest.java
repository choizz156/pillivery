package com.team33.moduleapi.ui.payment;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.payment.kakao.application.lookup.KakaoPayLookupService;
import com.team33.modulecore.payment.kakao.dto.KakaoLookupResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;

class PayLookupAcceptanceTest extends ApiTest {

	private Order order;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderFindHelper orderFindHelper;

	@BeforeEach
	void setUp() {
		order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.setNull("user")
			.set("paymentCode.tid", "tid")
			.sample();

		orderRepository.save(order);
	}


	@DisplayName("상품 결제 내역을 조회할 수 있다.")
	@Test
	void 결제_조회() throws Exception {

		//given
		KakaoPayLookupService kakaoPayLookupService = mock(KakaoPayLookupService.class);
		KakaoLookupResponse kakaoLookupResponse = FixtureMonkeyFactory.get().giveMeOne(KakaoLookupResponse.class);
		given(kakaoPayLookupService.request(any(Order.class))).willReturn(kakaoLookupResponse);

		MockMvcRequestSpecification given = RestAssuredMockMvc.given()
			.mockMvc(standaloneSetup(
					new PayLookupController(
						kakaoPayLookupService,
						orderFindHelper
					)
				)
					.build()
			)
			.log().all();

		//when
		given
		.when()
			.get("/payments/lookup/{orderId}", 1)
		.then()
			.log().all()
		.statusCode(HttpStatus.OK.value())
			.expect(jsonPath("$.data.tid").exists())
			.expect(jsonPath("$.data.cid").exists())
			.expect(jsonPath("$.data.status").exists())
			.expect(jsonPath("$.data.partner_order_id").exists())
			.expect(jsonPath("$.data.partner_user_id").exists())
			.expect(jsonPath("$.data.payment_method_type").exists())
			.expect(jsonPath("$.data.item_name").exists())
			.expect(jsonPath("$.data.quantity").exists())
			.expect(jsonPath("$.data.approvedCancelAmount").exists())
			.expect(jsonPath("$.data.approvedCancelAmount.total").exists())
			.expect(jsonPath("$.data.approvedCancelAmount.taxFree").exists())
			.expect(jsonPath("$.data.approvedCancelAmount.vat").exists())
			.expect(jsonPath("$.data.approvedCancelAmount.point").exists())
			.expect(jsonPath("$.data.approvedCancelAmount.discount").exists())
			.expect(jsonPath("$.data.approvedCancelAmount.green_deposit").exists())
			.expect(jsonPath("$.data.canceledAmount").exists())
			.expect(jsonPath("$.data.canceledAmount.total").exists())
			.expect(jsonPath("$.data.canceledAmount.taxFree").exists())
			.expect(jsonPath("$.data.canceledAmount.vat").exists())
			.expect(jsonPath("$.data.canceledAmount.point").exists())
			.expect(jsonPath("$.data.canceledAmount.discount").exists())
			.expect(jsonPath("$.data.canceledAmount.green_deposit").exists())
			.expect(jsonPath("$.data.cancelAvailableAmount").exists())
			.expect(jsonPath("$.data.cancelAvailableAmount.total").exists())
			.expect(jsonPath("$.data.cancelAvailableAmount.taxFree").exists())
			.expect(jsonPath("$.data.cancelAvailableAmount.vat").exists())
			.expect(jsonPath("$.data.cancelAvailableAmount.point").exists())
			.expect(jsonPath("$.data.cancelAvailableAmount.discount").exists())
			.expect(jsonPath("$.data.cancelAvailableAmount.green_deposit").exists())
			.expect(jsonPath("$.data.created_at").exists())
			.expect(jsonPath("$.data.approved_at").exists())
			.expect(jsonPath("$.data.payment_action_details").exists());
	}

}