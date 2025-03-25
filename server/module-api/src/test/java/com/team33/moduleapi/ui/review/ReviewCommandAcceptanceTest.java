package com.team33.moduleapi.ui.review;

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
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.moduleapi.api.order.dto.OrderPostDto;
import com.team33.moduleapi.api.order.dto.OrderPostListDto;
import com.team33.moduleapi.api.order.mapper.OrderItemMapper;
import com.team33.moduleapi.api.review.dto.ReviewDeleteDto;
import com.team33.moduleapi.api.review.dto.ReviewPatchDto;
import com.team33.moduleapi.api.review.dto.ReviewPostDto;
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
@DisplayName("리뷰 생성, 삭제, 수정 api 인수 테스트")
class ReviewCommandAcceptanceTest extends ApiTest {

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

	@DisplayName("리뷰를 등록할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 리뷰_등록() {

		//given
		주문_저장(주문_정보(), OrderStatus.COMPLETE);

		ReviewPostDto reviewPostDto = ReviewPostDto.builder()
			.content("content")
			.displayName("test")
			.orderId(1L)
			.itemId(1L)
			.userId(1L)
			.star(5.0)
			.build();

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(reviewPostDto)
			.header("Authorization", getToken())
			.when()
			.post("/api/reviews")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body("data.reviewId", is(1))
			.body("data.content", is("content"))
			.body("data.displayName", is("test"))
			.body("data.userId", is(1))
			.body("data.star", is(5.0f))
			.body("data.createdAt", notNullValue())
			.body("data.updatedAt", notNullValue());
	}

	@DisplayName("구매하지 않은 상품 리뷰 작성 시도 시 예외가 발생한다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 리뷰_예외1() {

		//given
		주문_저장(주문_정보(), OrderStatus.COMPLETE);

		ReviewPostDto reviewPostDto = ReviewPostDto.builder()
			.content("content")
			.displayName("test")
			.orderId(1L)
			.itemId(2L)
			.userId(1L)
			.star(5.0)
			.build();

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(reviewPostDto)
			.header("Authorization", getToken())
			.when()
			.post("/api/reviews")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("message", is("구매한 상품만 리뷰할 수 있습니다."));
	}

	@DisplayName("주문의 상태가 Complete, Subscribe가 아닌 상태에서 리뷰 작성 시도 시 예외를 던진다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 리뷰_예외2() {

		//given
		주문_저장(주문_정보(), OrderStatus.SUBSCRIBE_CANCEL);

		ReviewPostDto reviewPostDto = ReviewPostDto.builder()
			.content("content")
			.displayName("test")
			.orderId(1L)
			.itemId(2L)
			.userId(1L)
			.star(5.0)
			.build();

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(reviewPostDto)
			.header("Authorization", getToken())
			.when()
			.post("/api/reviews")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("message", is("구매한 상품만 리뷰할 수 있습니다."));
	}

	@DisplayName("리뷰를 수정할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 리뷰_수정() {

		//given
		주문_저장(주문_정보(), OrderStatus.COMPLETE);

		리뷰_생성();

		ReviewPatchDto reviewPatchDto = ReviewPatchDto.builder()
			.content("content change")
			.reviewId(1L)
			.itemId(1L)
			.userId(1L)
			.star(5.0)
			.build();

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(reviewPatchDto)
			.header("Authorization", getToken())
			.when()
			.patch("/api/reviews")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.reviewId", is(1))
			.body("data.content", is("content change"))
			.body("data.displayName", is("test"))
			.body("data.userId", is(1))
			.body("data.star", is(5.0f))
			.body("data.createdAt", notNullValue())
			.body("data.updatedAt", notNullValue());
	}

	@DisplayName("본인이 작성한 리뷰를 수정하려 할 경우 예외를 던진다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 리뷰_수정_예외() {

		//given
		주문_저장(주문_정보(), OrderStatus.COMPLETE);

		리뷰_생성();

		ReviewPatchDto reviewPatchDto = ReviewPatchDto.builder()
			.content("content change")
			.reviewId(1L)
			.itemId(1L)
			.userId(2L)
			.star(5.0)
			.build();

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(reviewPatchDto)
			.header("Authorization", getToken())
			.when()
			.patch("/api/reviews")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("message", is("권한이 없는 유저입니다."));
	}

	@DisplayName("리뷰를 삭제할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 리뷰_삭제() {

		//given
		주문_저장(주문_정보(), OrderStatus.COMPLETE);
		리뷰_생성();

		ReviewDeleteDto reviewDeleteDto = ReviewDeleteDto.builder()
			.reviewId(1L)
			.itemId(1L)
			.userId(1L)
			.build();

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(reviewDeleteDto)
			.header("Authorization", getToken())
			.when()
			.delete("/api/reviews")
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("리뷰를 삭제시 작성자가 아닐 경우 예외를 던진다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 리뷰_삭제_예외() {

		//given
		주문_저장(주문_정보(), OrderStatus.COMPLETE);
		리뷰_생성();

		ReviewDeleteDto reviewDeleteDto = ReviewDeleteDto.builder()
			.reviewId(1L)
			.itemId(1L)
			.userId(2L)
			.build();

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(reviewDeleteDto)
			.header("Authorization", getToken())
			.when()
			.delete("/api/reviews")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("message", is("권한이 없는 유저입니다."));
	}

	private void 리뷰_생성() {
		ReviewContext context = ReviewContext.builder()
			.orderId(1L)
			.userId(1L)
			.itemId(1L)
			.displayName("test")
			.star(5.0)
			.content("content")
			.build();

		reviewCommandService.createReview(context);
	}

	private void 주문_저장(OrderPostListDto postListDto, OrderStatus orderStatus) {
		아이템_저장();

		List<OrderItemServiceDto> orderItemPostDto =
			orderItemMapper.toOrderItemPostDto(postListDto.getOrderPostDtoList());
		OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
		List<OrderItem> orderItems = orderItemService.convertToOrderItems(orderItemPostDto);

		Order order = orderCreateService.callOrder(orderItems, orderContext);

		order.changeOrderStatus(orderStatus);
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