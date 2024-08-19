package com.team33.moduleapi.ui.scheduler;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.UserAccount;
import com.team33.moduleapi.ui.order.dto.OrderPostDto;
import com.team33.moduleapi.ui.order.dto.OrderPostListDto;
import com.team33.moduleapi.ui.order.mapper.OrderItemMapper;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Price;
import com.team33.modulecore.core.item.domain.Statistic;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.application.OrderCreateService;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduleControllerTest extends ApiTest {

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private OrderCreateService orderCreateService;

	@BeforeEach
	void beforeAll() {
		주문_저장(주문_정보(),OrderStatus.SUBSCRIBE);
	}

	private OrderItem orderItem;
	private final ZonedDateTime now = ZonedDateTime.of(2028, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));

	@DisplayName("요청 시 스케쥴러가 설정된다.")
	@Test
	void test1() throws Exception {

		//@formatter:off
        given()
            .log().all()
            .when()
            .post("/schedules/{orderId}", 1L)
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .log().all();
        //@formatter:on
	}

	@DisplayName("스케쥴을 수정할 수 있다.(30 -> 60)")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void test2() throws Exception {

		String token = super.getToken();

		ExtractableResponse<Response> response =
			//@formatter:off
            given()
                .log().all()
                .header("Authorization", token)
                .param("period", 60)
                .param("orderId", 1L)
                .param("itemOrderId", 1L)
                .when()
                .patch("/schedules")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        //@formatter:on

		String paymentDay = response.jsonPath().get("data.nextPaymentDay");
		String year = paymentDay.substring(0, 4);
		String month = paymentDay.substring(6, 7);
		String day = paymentDay.substring(9, 10);

		assertThat(year).isEqualTo(String.valueOf(orderItem.getPaymentDay().getYear()));
		assertThat(month).isEqualTo(
			String.valueOf(orderItem.getPaymentDay().getMonth().getValue())
		);
		assertThat(day).isEqualTo(
			String.valueOf(orderItem.getPaymentDay().getDayOfMonth())
		);
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
                .delete("/schedules")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        //@formatter:on

		assertThat(response.body().asString()).isNotBlank();
	}

	private void 주문_저장(OrderPostListDto postListDto, OrderStatus orderStatus) {
		아이템_저장();

		List<OrderItemServiceDto> orderItemPostDto =
			orderItemMapper.toOrderItemPostDto(postListDto.getOrderPostDtoList());
		OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
		List<OrderItem> orderItems = orderItemService.toOrderItems(orderItemPostDto);

		orderItems.forEach(orderItem -> orderItem.addPaymentDay(now));

		Order order = orderCreateService.callOrder(orderItems, orderContext);

		order.changeOrderStatus(orderStatus);
		Order saveOrder = orderCommandRepository.save(order);
		orderItem = saveOrder.getOrderItems().get(0);
	}

	private OrderPostListDto 주문_정보() {

		return OrderPostListDto.builder()
			.userId(1L)
			.subscription(true)
			.orderedAtCart(false)
			.city("서울")
			.detailAddress("101 번지")
			.realName("홍길동")
			.phoneNumber("010-1111-1111")
			.orderPostDtoList(List.of(
					OrderPostDto.builder()
						.itemId(1L)
						.period(30)
						.quantity(1)
						.subscription(true)
						.build()
				)
			)
			.build();
	}

	private void 아이템_저장(
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
			.price(new Price(10000, 0))
			.build();

		Item item = Item.builder()
			.information(information)
			.statistics(new Statistic())
			.build();

		item.addIncludedCategories(Set.of(CategoryName.INTESTINE));
		item.getItemCategory().add(CategoryName.INTESTINE);

		itemCommandRepository.save(item);
	}
}