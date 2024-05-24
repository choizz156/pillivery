package com.team33.modulecore.payment.kakao.application;

import static com.team33.modulecore.payment.kakao.application.ParamsConst.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.entity.Order;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParameterProviderTest {

	private Order order;

	@BeforeAll
	void beforeAll() {
		order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("orderPrice", new OrderPrice(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("paymentCode.sid", "sid")
			.sample();
	}

	@DisplayName("단건 결제 요청 파라미터를 생성할 수 있다.")
	@Test
	void 단건_결제_요청() throws Exception {
		//given
		ParameterProvider parameterProvider = new ParameterProvider();

		//when
		Map<String, String> oneTimeReqsParams = parameterProvider.getOneTimeReqsParams(order);

		//then
		assertThat(oneTimeReqsParams).hasSize(10)
			.extractingFromEntries(Map.Entry::getKey, Map.Entry::getValue)
			.containsExactlyInAnyOrder(
				tuple(PARTNER_ORDER_ID, "1"),
				tuple(PARTNER_USER_ID, PARTNER),
				tuple(ITEM_NAME, "test 그 외 2개"),
				tuple(QUANTITY, "3"),
				tuple(TOTAL_AMOUNT, "3000"),
				tuple(TAX_FREE_AMOUNT, "0"),
				tuple(CANCEL_URL, CANCEL_URI),
				tuple(FAIL_URL, FAIL_URI),
				tuple(CID, ONE_TIME_CID),
				tuple(APPROVAL_URL, ONE_TIME_APPROVAL_URL + "/1")
			);
	}

	@DisplayName("정기 결제 요청 시, 파라미터를 생성할 수 있다.")
	@Test
	void 정기_결제_요청() throws Exception {
		//given
		ParameterProvider parameterProvider = new ParameterProvider();

		//when
		Map<String, String> subscriptionReqsParams = parameterProvider.getSubscriptionReqsParams(order);

		//then
		assertThat(subscriptionReqsParams).hasSize(10)
			.extractingFromEntries(Map.Entry::getKey, Map.Entry::getValue)
			.containsExactlyInAnyOrder(
				tuple(PARTNER_ORDER_ID, "1"),
				tuple(PARTNER_USER_ID, PARTNER),
				tuple(ITEM_NAME, "test 그 외 2개"),
				tuple(QUANTITY, "3"),
				tuple(TOTAL_AMOUNT, "3000"),
				tuple(TAX_FREE_AMOUNT, "0"),
				tuple(CANCEL_URL, CANCEL_URI),
				tuple(FAIL_URL, FAIL_URI),
				tuple(CID, SUBSCRIP_CID),
				tuple(APPROVAL_URL, SUBSCRIPTION_APPROVAL_URI + "/1")
			);
	}

	@DisplayName("단건 승인 시 파라미터를 생성할 수 있다.")
	@Test
	void 단건_승인() throws Exception{
		//given
		ParameterProvider parameterProvider = new ParameterProvider();

		//when
		Map<String, String> subscriptionReqsParams = parameterProvider.getOneTimeApproveParams("tid", "pgToken", 1L);

		//then
		assertThat(subscriptionReqsParams).hasSize(5)
			.extractingFromEntries(Map.Entry::getKey, Map.Entry::getValue)
			.containsExactlyInAnyOrder(
				tuple(PARTNER_ORDER_ID, "1"),
				tuple(PARTNER_USER_ID, PARTNER),
				tuple(CID, ONE_TIME_CID),
				tuple(PG_TOKEN, "pgToken"),
				tuple(TID, "tid")
			);
	}

	@DisplayName("정기 결제 최초 승인 시 파라미터를 생성할 수 있다.")
	@Test
	void 정기_결제_최초_승인() throws Exception{
		//given
		ParameterProvider parameterProvider = new ParameterProvider();

		//when
		Map<String, String> subscriptionReqsParams = parameterProvider.getSubscriptionFirstApproveParams("tid", "pgToken", 1L);

		//then
		assertThat(subscriptionReqsParams).hasSize(5)
			.extractingFromEntries(Map.Entry::getKey, Map.Entry::getValue)
			.containsExactlyInAnyOrder(
				tuple(PARTNER_ORDER_ID, "1"),
				tuple(PARTNER_USER_ID, PARTNER),
				tuple(CID, SUBSCRIP_CID),
				tuple(PG_TOKEN, "pgToken"),
				tuple(TID, "tid")
			);
	}

	@DisplayName("정기 결제 승인 시 파라미터를 생성할 수 있다.")
	@Test
	void 정기_결제_승인() throws Exception{
		//given
		ParameterProvider parameterProvider = new ParameterProvider();

		//when
		Map<String, Object> subscriptionReqsParams = parameterProvider.getSubscriptionApproveParams(order);

		//then
		assertThat(subscriptionReqsParams).hasSize(10)
			.extractingFromEntries(Map.Entry::getKey, Map.Entry::getValue)
			.containsExactlyInAnyOrder(
				tuple(ITEM_NAME, "test 그 외 2개"),
				tuple(QUANTITY, "3"),
				tuple(TOTAL_AMOUNT, "3000"),
				tuple(TAX_FREE_AMOUNT, "0"),
				tuple(CANCEL_URL, CANCEL_URI),
				tuple(FAIL_URL, FAIL_URI),
				tuple(PARTNER_ORDER_ID, "1"),
				tuple(PARTNER_USER_ID, PARTNER),
				tuple(CID, SUBSCRIP_CID),
				tuple(SID, "sid")
			);
	}
}