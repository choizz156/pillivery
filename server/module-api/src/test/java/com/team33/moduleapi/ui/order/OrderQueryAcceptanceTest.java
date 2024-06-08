package com.team33.moduleapi.ui.order;

import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.UserAccount;
import com.team33.moduleapi.ui.order.dto.OrderPostDto;
import com.team33.moduleapi.ui.order.dto.OrderPostListDto;
import com.team33.moduleapi.ui.order.mapper.OrderItemMapper;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Image;
import com.team33.modulecore.item.domain.Information;
import com.team33.modulecore.item.domain.Price;
import com.team33.modulecore.item.domain.Statistic;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.order.application.OrderCreateService;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.order.dto.OrderContext;
import com.team33.modulecore.order.dto.OrderItemServiceDto;
import com.team33.modulecore.order.dto.OrderPage;

import io.restassured.RestAssured;

class OrderQueryAcceptanceTest extends ApiTest {
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

	@DisplayName("주문 정보를 조회하여 api 응답을 보낼 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 주문_조회() throws Exception {
		//given
		주문_저장(주문_정보(false, 0));
		주문_저장(주문_정보(false, 0));

		OrderPage page = new OrderPage();
		page.setPage(1);
		page.setSize(10);

		RestAssured.given()
			.queryParam("userId", 1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(page)
			.header("Authorization", getToken())
			.log().all()
			.when()
			.get("/orders")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.size()", is(2))
			.body("data[0].orderId", equalTo(2))
			.body("data[0].orderStatus", equalTo("COMPLETE"))
			.body("data[0].totalItems", equalTo(2))
			.body("data[0].expectPrice", equalTo(20000))
			.body("data[0].subscription", equalTo(false))
			.body("data[0].firstItem.itemId", equalTo(1))
			.body("data[0].firstItem.enterprise", equalTo("(주)씨티씨바이오"))
			.body("data[0].firstItem.thumbnail", equalTo("thumbnailUrl"))
			.body("data[0].firstItem.product", equalTo("16종혼합유산균 디에스"))
			.body("data[0].firstItem.originPrice", equalTo(10000))
			.body("data[0].firstItem.realPrice", equalTo(0))
			.body("data[0].firstItem.discountRate", equalTo(0.0f))
			.body("data[0].firstItem.discountPrice", equalTo(0))

			.body("data[1].orderId", equalTo(1))

			.body("pageInfo.page", equalTo(1))
			.body("pageInfo.size", equalTo(10))
			.body("pageInfo.totalElements", equalTo(2))
			.body("pageInfo.totalPages", equalTo(1));
	}

	public void 주문_저장(OrderPostListDto postListDto) {
		아이템_저장("16종혼합유산균 디에스", 10000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
		아이템_저장("종혼합유산균 디에스2", 10000, 0, CategoryName.EYE, "(주)씨티씨바이오");

		List<OrderItemServiceDto> orderItemPostDto = orderItemMapper.toOrderItemPostDto(
			postListDto.getOrderPostDtoList());
		OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
		List<OrderItem> orderItems = orderItemService.toOrderItems(orderItemPostDto);

		Order order = orderCreateService.callOrder(orderItems, orderContext);

		order.changeOrderStatus(OrderStatus.COMPLETE);
		orderCommandRepository.save(order);
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

		item.addIncludedCategory(Set.of(categoryName));
		item.getItemCategory().add(categoryName);

		itemCommandRepository.save(item);
	}
}