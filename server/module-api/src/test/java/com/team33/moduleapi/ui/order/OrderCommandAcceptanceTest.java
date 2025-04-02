package com.team33.moduleapi.ui.order;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;

class OrderCommandAcceptanceTest extends ApiTest {

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private OrderCreateService orderCreateService;

	@DisplayName("주문 객체를 생성하여 api 응답을 보낼 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 주문_생성() throws Exception {
		//given
		아이템_저장("test", 10000, 0.0, CategoryName.INTESTINE, "brand");

		OrderPostListDto postListDto = 주문_정보(false, 0);

		//when
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(postListDto)
			.header("Authorization", getToken())
			.log().all()
			.when()
			.post("/api/orders")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body("data.orderId", equalTo(1))
			.body("data.totalItems", equalTo(1))
			.body("data.totalPrice", equalTo(10000))
			.body("data.totalDiscountPrice", equalTo(0))
			.body("data.expectPrice", equalTo(10000))
			.body("data.subscription", equalTo(false))

			.body("data.itemOrders[0].orderItemId", equalTo(1))
			.body("data.itemOrders[0].quantity", equalTo(1))
			.body("data.itemOrders[0].period", equalTo(0))
			.body("data.itemOrders[0].subscription", equalTo(false))

			.body("data.itemOrders[0].item.itemId", equalTo(1))
			.body("data.itemOrders[0].item.enterprise", equalTo("brand"))
			.body("data.itemOrders[0].item.thumbnail", equalTo("thumbnailUrl"))
			.body("data.itemOrders[0].item.product", equalTo("test"))
			.body("data.itemOrders[0].item.originPrice", equalTo(10000))
			.body("data.itemOrders[0].item.realPrice", equalTo(10000))
			.body("data.itemOrders[0].item.discountRate", equalTo(0.0f))
			.body("data.itemOrders[0].item.discountPrice", equalTo(0))

			.body("data.orderStatus", equalTo("REQUEST"))
			.body("data.totalQuantity", equalTo(1))

			.body("data.receiver.realName", equalTo("홍길동"))
			.body("data.receiver.phone", equalTo("010-1111-1111"))
			.body("data.receiver.address.city", equalTo("서울"))
			.body("data.receiver.address.detailAddress", equalTo("101 번지"));
	}

	@DisplayName("정기 결제 진행 중인 아이템의 수량을 변경할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 정기_주문_수량_변경() throws Exception {
		//given
		아이템_저장("test", 10000, 0.0D, CategoryName.INTESTINE, "brand");
		주문_저장();

		//when
		given()
			.queryParam("orderItemId", 1)
			.queryParam("quantity", 2)
			.header("Authorization", getToken())
			.when()
			.patch("/api/orders/subscriptions/1")
			.then()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	private void 주문_저장() {
		OrderPostListDto postListDto = 주문_정보(true, 30);

		List<OrderItemServiceDto> orderItemPostDto = orderItemMapper.toOrderItemPostDto(
			postListDto.getOrderPostDtoList());
		OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
		List<OrderItem> orderItems = orderItemService.convertToOrderItems(orderItemPostDto);

		Order order = orderCreateService.callOrder(orderItems, orderContext);
	}

	private OrderPostListDto 주문_정보(boolean subscription, int period) {
		OrderPostListDto postListDto = OrderPostListDto.builder()
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
						.build()
				)
			)
			.build();

		return postListDto;
	}

	private void 아이템_저장(
		String productName,
		int originPrice,
		double discountRate,
		CategoryName categoryName,
		String enterprise
	) {
		Information information = Information.builder()
			.enterprise(enterprise)
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
			.price(new Price(originPrice, discountRate))
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