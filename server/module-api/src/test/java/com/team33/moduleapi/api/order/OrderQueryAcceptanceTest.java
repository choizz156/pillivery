package com.team33.moduleapi.api.order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.api.order.dto.OrderPostDto;
import com.team33.moduleapi.api.order.dto.OrderPostListDto;
import com.team33.moduleapi.api.order.mapper.OrderItemMapper;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Price;
import com.team33.modulecore.core.item.domain.Statistic;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.application.OrderCreateService;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


class OrderQueryAcceptanceTest extends ApiTest {

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private SubscriptionOrderService subscriptionOrderService;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private OrderCreateService orderCreateService;


	@DisplayName("일반 주문 정보를 조회하여 api 응답을 보낼 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 주문_조회() throws Exception {
		//given
		주문_저장(주문_정보(false, 0), OrderStatus.COMPLETE);
		주문_저장(주문_정보(false, 0), OrderStatus.COMPLETE);

		given()
			.queryParam("userId", 1)
			.header("Authorization", getToken())
			.log().all()
			.when()
			.get("/api/orders")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data", hasSize(4))
			.body("data[0].orderId", equalTo(2))
			.body("data[0].orderItemId", equalTo(3))
			.body("data[0].quantity", equalTo(1))
			.body("data[0].itemId", equalTo(1))
			.body("data[0].itemName", equalTo("16종혼합유산균 디에스"))
			.body("data[0].price", equalTo(10000))
			.body("data[0].description", startsWith("[프로바이오틱스]"))
			.body("data[0].imageUrl", equalTo("thumbnailUrl"))
			.body("data[0].category.categoryNameSet[0]", equalTo("INTESTINE"))
			.body("data[1].orderId", equalTo(2))
			.body("data[1].itemName", equalTo("종혼합유산균 디에스2"))
			.body("data[1].category.categoryNameSet[0]", equalTo("EYE"))
			.body("data[2].orderId", equalTo(1))
			.body("data[2].itemId", equalTo(1))
			.body("data[2].itemName", equalTo("16종혼합유산균 디에스"))
			.body("data[2].category.categoryNameSet[0]", equalTo("INTESTINE"))
			.body("data[3].orderId", equalTo(1))
			.body("data[3].itemId", equalTo(2))
			.body("data[3].itemName", equalTo("종혼합유산균 디에스2"))
			.body("data[3].category.categoryNameSet[0]", equalTo("EYE"))
			.body("pageInfo.page", equalTo(1))
			.body("pageInfo.size", equalTo(8))
			.body("pageInfo.totalElements", equalTo(4))
			.body("pageInfo.totalPages", equalTo(1));
	}


	@DisplayName("구독 주문 정보를 가지고 올 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 구독_주문_조회() throws Exception {
		//given
		주문_저장(주문_정보(true, 30), OrderStatus.SUBSCRIPTION);
		주문_저장(주문_정보(true, 60), OrderStatus.SUBSCRIPTION);

		given()
			.queryParam("userId", 1L)
			.header("Authorization", getToken())
			.log().all()
			.when()
			.get("/api/orders/subscriptions")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("time", notNullValue())
			.body("data.size()", is(4))
			.body("data[0].subscriptionOrderId", equalTo(4))
			.body("data[0].orderItemId", equalTo(4))
			.body("data[0].quantity", equalTo(1))
			.body("data[0].itemId", equalTo(2))
			.body("data[0].itemName", equalTo("종혼합유산균 디에스2"))
			.body("data[0].price", equalTo(10000))
			.body("data[0].description", equalTo("descriptionImage"))
			.body("data[0].imageUrl", equalTo("thumbnailUrl"))
			.body("data[0].category", equalTo("[EYE]"))
			.body("pageInfo.page", equalTo(1))
			.body("pageInfo.size", equalTo(8))
			.body("pageInfo.totalElements", equalTo(4))
			.body("pageInfo.totalPages", equalTo(1));
	}

	@DisplayName("특정 주문을 조회하여 api 응답을 받을 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 특정_주문_조회() throws Exception {
		//given
		주문_저장(주문_정보(false, 0), OrderStatus.COMPLETE);

		given()
			.header("Authorization", getToken())
			.log().all()
			.when()
			.get("/api/orders/1")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.orderId", equalTo(1))
			.body("data.totalItems", equalTo(2))
			.body("data.totalPrice", equalTo(20000))
			.body("data.totalDiscountPrice", equalTo(0))
			.body("data.expectPrice", equalTo(20000))
			.body("data.orderStatus", equalTo("COMPLETE"))
			.body("data.totalQuantity", equalTo(2))
			.body("data.receiver.realName", equalTo("홍길동"))
			.body("data.receiver.phone", equalTo("010-1111-1111"))
			.body("data.receiver.address.city", equalTo("서울"))
			.body("data.receiver.address.detailAddress", equalTo("101 번지"))
			.body("data.orderItems[0].orderItemId", equalTo(1))
			.body("data.orderItems[0].quantity", equalTo(1))
			.body("data.orderItems[0].itemId", equalTo(1))
			.body("data.orderItems[0].enterprise", equalTo("(주)씨티씨바이오"))
			.body("data.orderItems[0].product", equalTo("16종혼합유산균 디에스"))
			.body("data.orderItems[0].originPrice", equalTo(10000))
			.body("data.orderItems[0].realPrice", equalTo(10000))
			.body("data.orderItems[0].discountRate", equalTo(0.0f))
			.body("data.orderItems[0].discountPrice", equalTo(0))
			.body("data.orderItems[1].orderItemId", equalTo(2))
			.body("data.orderItems[1].quantity", equalTo(1))
			.body("data.orderItems[1].itemId", equalTo(2))
			.body("data.orderItems[1].enterprise", equalTo("(주)씨티씨바이오"))
			.body("data.orderItems[1].product", equalTo("종혼합유산균 디에스2"))
			.body("data.orderItems[1].originPrice", equalTo(10000))
			.body("data.orderItems[1].realPrice", equalTo(10000))
			.body("data.orderItems[1].discountRate", equalTo(0.0f))
			.body("data.orderItems[1].discountPrice", equalTo(0));
	}

	private void 주문_저장(OrderPostListDto postListDto, OrderStatus orderStatus) {
		아이템_저장("16종혼합유산균 디에스", CategoryName.INTESTINE);
		아이템_저장("종혼합유산균 디에스2", CategoryName.EYE);

		List<OrderItemServiceDto> orderItemPostDto =
			orderItemMapper.toOrderItemPostDto(postListDto.getOrderPostDtoList());
		OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
		List<OrderItem> orderItems = orderItemService.convertToOrderItems(orderItemPostDto);

		Order order = orderCreateService.callOrder(orderItems, orderContext);
		order.changeOrderStatus(orderStatus);
		orderCommandRepository.save(order);



		if (postListDto.isSubscription()) {
			subscriptionOrderService.create(order);
		}
	}

	private OrderPostListDto 주문_정보(boolean subscription, int period) {

		return OrderPostListDto.builder()
			.userId(1L)
			.subscription(subscription)
			.orderedAtCart(false)
			.city("서울")
			.detailAddress("101 번지")
			.realName("홍길동")
			.phoneNumber("010-1111-1111")
			.orderPostDtoList(List.of(
					OrderPostDto.builder()
						.itemId(1L)
						.period(period)
						.quantity(1)
						.subscription(subscription)
						.build(),
					OrderPostDto.builder()
						.itemId(2L)
						.period(period)
						.quantity(1)
						.subscription(subscription)
						.build()
				)
			)
			.build();
	}

	private void 아이템_저장(
		String productName,
		CategoryName categoryName
	) {

		Information information = Information.builder()
			.enterprise("(주)씨티씨바이오")
			.productName(productName)
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
			.price(new Price(10000, 0.0))
			.build();

		Item item = Item.builder()
			.information(information)
			.statistics(new Statistic())
			.build();

		item.addIncludedCategories(Set.of(categoryName));
		item.getItemCategory().add(categoryName);

		itemCommandRepository.save(item);
	}
}