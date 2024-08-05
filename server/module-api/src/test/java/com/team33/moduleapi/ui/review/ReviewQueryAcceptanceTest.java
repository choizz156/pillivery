package com.team33.moduleapi.ui.review;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;
import com.team33.modulecore.core.review.application.ReviewCommandService;
import com.team33.modulecore.core.review.domain.ReviewContext;

@DisplayName("리뷰 조회 api 인수 테스트")
class ReviewQueryAcceptanceTest extends ApiTest {

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

	@Autowired
	private ReviewCommandService reviewCommandService;

	@DisplayName("특정 리뷰를 조회하여 api 응답을 할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 특정_리뷰_조회() throws Exception {

		//given
		주문_저장(주문_정보());
		리뷰_생성(1L, "content", 5.0);

		//when
		given()
			.log().all()
			.when()
			.get("/reviews/1")
			.then()
			.statusCode(HttpStatus.OK.value())
			.log().all()
			.body("data.reviewId", is(1))
			.body("data.itemId", is(1))
			.body("data.userId", is(1))
			.body("data.content", is("content"))
			.body("data.displayName", is("test"))
			.body("data.star", is(5.0f))
			.body("data.reviewStatus", is("ACTIVE"));
	}

	@DisplayName("아이템의 리뷰들을 조회하여 api 응답을 할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 아이템_리뷰_조회() throws Exception {

		//given
		주문_저장(주문_정보());
		리뷰_생성(1L, "content", 5.0);

		//when
		given()
			.log().all()
			.queryParam("size", 8)
			.queryParam("page", 1)
			.queryParam("sort", "NEWEST")
			.when()
			.get("/reviews/items/1")
			.then()
			.statusCode(HttpStatus.OK.value())
			.body("data[0].reviewId", is(1))
			.body("data[0].itemId", is(1))
			.body("data[0].userId", is(1))
			.body("data[0].content", is("content"))
			.body("data[0].displayName", is("test"))
			.body("data[0].star", is(5.0f))
			.body("data[0].createdAt", notNullValue())
			.body("data[0].updatedAt", notNullValue())
			.body("data[0].reviewStatus", is("ACTIVE"))
			.body("pageInfo.page", is(1))
			.body("pageInfo.size", is(8))
			.body("pageInfo.totalElements", is(1))
			.body("pageInfo.totalPages", is(1))
			.log().all();
	}

	@DisplayName("특정 유저가 작성한 리뷰들을 조회하여 api 응답을 할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 유저_리뷰_조회() throws Exception {

		//given
		주문_저장(주문_정보());
		리뷰_생성(1L, "content1", 5.0);
		리뷰_생성(2L, "content2", 4.0);
		리뷰_생성(3L, "content3", 3.0);

		//when
		given()
			.log().all()
			.queryParam("size", 8)
			.queryParam("page", 1)
			.queryParam("sort", "NEWEST")
			.when()
			.get("/reviews/users/1")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.size()", is(3))
			.body("data.reviewId", contains(3, 2, 1))
			.body("data.itemId", contains(3, 2, 1))
			.body("data.userId", contains(1, 1, 1))
			.body("data.star", contains(3.0f, 4.0f, 5.0f))
			.body("data.content", contains("content3", "content2", "content1"))
			.body("data.reviewStatus", contains("ACTIVE", "ACTIVE", "ACTIVE"))
			.body("pageInfo.page", is(1))
			.body("pageInfo.size", is(8))
			.body("pageInfo.totalElements", is(3))
			.body("pageInfo.totalPages", is(1));
	}

	@Nested
	@DisplayName("리뷰 정렬 테스트")
	class ReviewSortTest {

		@DisplayName("오래된 순")
		@UserAccount({"test", "010-0000-0000"})
		@Test
		void 오래된_순() throws Exception {

			//given
			주문_저장(주문_정보());
			리뷰_생성(1L, "content1", 5.0);
			리뷰_생성(2L, "content2", 4.0);
			리뷰_생성(3L, "content3", 3.0);

			//when
			given()
				.log().all()
				.queryParam("size", 8)
				.queryParam("page", 1)
				.queryParam("sort", "OLDEST")
				.when()
				.get("/reviews/users/1")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(3))
				.body("data.reviewId", contains(1, 2, 3))
				.body("data.itemId", contains(1, 2, 3))
				.body("data.userId", contains(1, 1, 1))
				.body("data.star", contains(5.0f, 4.0f, 3.0f))
				.body("data.content", contains("content1", "content2", "content3"))
				.body("data.reviewStatus", contains("ACTIVE", "ACTIVE", "ACTIVE"))
				.body("pageInfo.page", is(1))
				.body("pageInfo.size", is(8))
				.body("pageInfo.totalElements", is(3))
				.body("pageInfo.totalPages", is(1));
		}

		@DisplayName("별점 높은 순")
		@UserAccount({"test", "010-0000-0000"})
		@Test
		void 별점_높은_순() throws Exception {

			//given
			주문_저장(주문_정보());
			리뷰_생성(1L, "content1", 4.0);
			리뷰_생성(2L, "content2", 5.0);
			리뷰_생성(3L, "content3", 3.0);

			//when
			given()
				.log().all()
				.queryParam("size", 8)
				.queryParam("page", 1)
				.queryParam("sort", "STAR_H")
				.when()
				.get("/reviews/users/1")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(3))
				.body("data.reviewId", contains(2, 1, 3))
				.body("data.itemId", contains(2, 1, 3))
				.body("data.userId", contains(1, 1, 1))
				.body("data.star", contains(5.0f, 4.0f, 3.0f))
				.body("data.content", contains("content2", "content1", "content3"))
				.body("data.reviewStatus", contains("ACTIVE", "ACTIVE", "ACTIVE"))
				.body("pageInfo.page", is(1))
				.body("pageInfo.size", is(8))
				.body("pageInfo.totalElements", is(3))
				.body("pageInfo.totalPages", is(1));
		}

		@DisplayName("별점 낮은 순")
		@UserAccount({"test", "010-0000-0000"})
		@Test
		void 별점_낮은_순() throws Exception {

			//given
			주문_저장(주문_정보());
			리뷰_생성(1L, "content1", 5.0);
			리뷰_생성(2L, "content2", 4.0);
			리뷰_생성(3L, "content3", 3.0);

			//when
			given()
				.log().all()
				.queryParam("size", 8)
				.queryParam("page", 1)
				.queryParam("sort", "STAR_L")
				.when()
				.get("/reviews/users/1")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(3))
				.body("data.reviewId", contains(3, 2, 1))
				.body("data.itemId", contains(3, 2, 1))
				.body("data.userId", contains(1, 1, 1))
				.body("data.star", contains(3.0f, 4.0f, 5.0f))
				.body("data.content", contains("content3", "content2", "content1"))
				.body("data.reviewStatus", contains("ACTIVE", "ACTIVE", "ACTIVE"))
				.body("pageInfo.page", is(1))
				.body("pageInfo.size", is(8))
				.body("pageInfo.totalElements", is(3))
				.body("pageInfo.totalPages", is(1));
		}
	}

	private void 리뷰_생성(long itemId, String content, double star) {
		ReviewContext context = ReviewContext.builder()
			.orderId(1L)
			.userId(1L)
			.itemId(itemId)
			.displayName("test")
			.star(star)
			.content(content)
			.build();

		reviewCommandService.createReview(context);
	}

	private void 주문_저장(OrderPostListDto postListDto) {
		아이템_저장(10000, "16종혼합유산균 디에스1");
		아이템_저장(11000, "16종혼합유산균 디에스2");
		아이템_저장(12000, "16종혼합유산균 디에스3");

		List<OrderItemServiceDto> orderItemPostDto =
			orderItemMapper.toOrderItemPostDto(postListDto.getOrderPostDtoList());
		OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
		List<OrderItem> orderItems = orderItemService.toOrderItems(orderItemPostDto);

		Order order = orderCreateService.callOrder(orderItems, orderContext);

		order.changeOrderStatus(OrderStatus.COMPLETE);
		orderCommandRepository.save(order);
	}

	private OrderPostListDto 주문_정보() {

		return OrderPostListDto.builder()
			.userId(1L)
			.subscription(false)
			.orderedAtCart(false)
			.city("서울")
			.detailAddress("101 번지")
			.realName("홍길동")
			.phoneNumber("010-1111-1111")
			.orderPostDtoList(List.of(
					OrderPostDto.builder()
						.itemId(1L)
						.period(0)
						.quantity(1)
						.subscription(false)
						.build(),
					OrderPostDto.builder()
						.itemId(2L)
						.period(0)
						.quantity(1)
						.subscription(false)
						.build(),
					OrderPostDto.builder()
						.itemId(3L)
						.period(0)
						.quantity(1)
						.subscription(false)
						.build()
				)
			)
			.build();
	}

	private void 아이템_저장(int originPrice, String productName) {
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
			.price(new Price(originPrice, 0))
			.build();

		Item item = Item.builder()
			.information(information)
			.statistics(new Statistic())
			.build();

		item.addIncludedCategory(Set.of(CategoryName.INTESTINE));
		item.getItemCategory().add(CategoryName.INTESTINE);

		itemCommandRepository.save(item);
	}
}