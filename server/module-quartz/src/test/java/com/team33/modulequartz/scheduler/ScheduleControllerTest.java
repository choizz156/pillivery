package com.team33.modulequartz.scheduler;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;

import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Statistic;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.application.OrderCreateService;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.PaymentToken;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulequartz.ApiTest;
import com.team33.modulequartz.FixtureMonkeyFactory;
import com.team33.modulequartz.mockuser.UserAccount;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@EnableJpaRepositories(basePackages = "com.team33.modulecore.core")
@EntityScan("com.team33.modulecore.core")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduleControllerTest extends ApiTest {

	private final ZonedDateTime now = ZonedDateTime.of(2028, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
	private OrderItem orderItem;
	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderCreateService orderCreateService;

	private void 주문_저장(OrderStatus orderStatus) {
		Item item = 아이템_저장();

		OrderItem orderItem = FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.setNull("id")
			.set("subscriptionInfo", SubscriptionInfo.of(true, 30))
			.set("item", item)
			.setNull("order")
			.set("quantity", 1)
			.sample();

		orderItem.updateSubscriptionPaymentDay(now);

		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.set("orderItems", List.of(orderItem))
			.set("orderStatus", orderStatus)
			.set("userId", 1L)
			.set("isOrderedAtCart", false)
			.set("mainItemName", item.getProductName())
			.set("isSubscription", true)
			.set("orderPrice", new Price(List.of(orderItem)))
			.set("totalItemsCount", 1)
			.set("totalQuantity", 1)
			.set("paymentCode", new PaymentToken())
			.set("receiver", null)
			.sample();

		Order saveOrder = orderCommandRepository.save(order);
		this.orderItem = saveOrder.getOrderItems().get(0);
	}

	private Item 아이템_저장(
	) {
		Information information = Information.builder()
			.enterprise("(주)씨티씨바이오")
			.productName("16종혼합유산균 디에스")
			.statementNumber("20040017059225")
			.registeredDate("20220225")
			.distributionPeriod("제조일로부터 24개월까지")
			.sungsang("고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말")
			.servingUse("건강기능식품 원료로 사용")
			.preservePeriod("냉장보관(10℃이하)")
			.intake("- 질환이 있거나 의약품 복용 시 전문가와 상담할 것\n" +
				"- 알레르기 체질 등은 개인에 따라 과민반응을 나타낼 수 있음\n" +
				"- 어린이가 함부로 섭취하지 않도록 일일섭취량 방법을 지도할 것\n" +
				"- 이상사례 발생 시 섭취를 중단하고 전문가와 상담할 것\n" +
				"- 원료로 사용 시 개봉 후 오염 우려가 있으니 신속하게 사용하고 남은 것은 밀봉 후 냉장보관할 것")
			.mainFunction("[프로바이오틱스] 유산균 증식 및 유해균 억제･배변활동 원활･장 건강에 도움을 줄 수 있음")
			.baseStandard("1. 성상 : 고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말\n" +
				"2. 프로바이오틱스 수 : 200,000,000,000(2000억) CFU/g 이상\n" +
				"3. 대장균군 : 음성\n" +
				"4. 납(mg/kg) : 1.0 이하\n" +
				"5. 카드뮴(mg/kg) : 0.3 이하")
			.image(new Image("thumbnailUrl", "descriptionImage"))
			.price(new com.team33.modulecore.core.item.domain.Price(10000, 0))
			.build();

		Item item = Item.builder()
			.information(information)
			.statistics(new Statistic())
			.build();

		item.addIncludedCategories(Set.of(CategoryName.INTESTINE));
		item.getItemCategory().add(CategoryName.INTESTINE);

		return itemCommandRepository.save(item);
	}

	@BeforeEach
	void beforeAll() {
		주문_저장(OrderStatus.SUBSCRIBE);
	}

	@DisplayName("요청 시 스케쥴러가 설정된다.")
	@Test
	void test1() throws Exception {

		//@formatter:off
        given()
            .log().all()
            .when()
            .post("/api/schedules/{orderId}", 1L)
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .log().all();
        //@formatter:on
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("스케쥴을 주기를 수정할 수 있다.")
	@Test
	void test2() throws Exception {

		String token = super.getToken();

		ExtractableResponse<Response> response =
			//@formatter:off
            given()
                .log().all()
                .header("Authorization", token)
                .param("orderId", 1L)
                .param("itemOrderId", 1L)
                .when()
                .post("/api/schedules/update")
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
        //@formatter:on

	}

	@DisplayName("스케쥴을 취소할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void test3() throws Exception {

		String token = super.getToken();

		ExtractableResponse<Response> response =
			//@formatter:off
            given()
                .log().all()
                .header("Authorization", token)
                .param("orderId", 1L)
                .param("itemOrderId", 1L)
                .when()
                .delete("/api/schedules")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        //@formatter:on

		assertThat(response.body().asString()).isNotBlank();
	}
}