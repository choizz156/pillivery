package com.team33.moduleapi.ui.cart;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.UserAccount;
import com.team33.moduleapi.ui.cart.dto.SubscriptionCartItemPostDto;
import com.team33.modulecore.cart.SubscriptionContext;
import com.team33.modulecore.cart.application.SubscriptionCartItemService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.order.domain.SubscriptionInfo;

class SubscriptionCartControllerTest extends ApiTest {

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private SubscriptionCartItemService subscriptionCartItemService;

	/**
	 * {@code @UserAccount}에서 회원 가입이 임의로 진행되고, 회원 가입시 카트가 생성(id = 2)됩니다.
	 * 따라서, 카트를 저장하는 로직은 따로 없습니다.
	 */
	@BeforeEach
	void setUp() {

		List<Item> items = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.setNull("id")
			.setNull("itemCategory")
			.setNull("reviewIds")
			.setNull("categories")
			.set("statistics.starAvg", 0.0)
			.set("statistics.reviewCount", 0)
			.set("statistics.view", 0)
			.set("statistics.sales", 0)
			.set("information.price.realPrice", 10000)
			.set("information.price.discountPrice", 1000)
			.set("information.price.discountRate", 10.0)
			.set("information.price.originPrice", 11000)
			.sampleList(2);

		itemCommandRepository.saveAll(items);

		SubscriptionContext subscriptionContext = SubscriptionContext.builder()
			.quantity(1)
			.item(items.get(0))
			.subscriptionInfo(SubscriptionInfo.of(true, 30))
			.build();

		subscriptionCartItemService.addSubscriptionItem(2L, subscriptionContext);
	}

	@DisplayName("구독 카트를 조회할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 구독_카트_조회() throws Exception {

		given()
			.log().all()
			.header("Authorization", getToken())
			.when()
			.get("/carts/subscription/2")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.cartId", equalTo(2))
			.body("data.totalItemCount", equalTo(1))
			.body("data.totalPrice", equalTo(10000))
			.body("data.totalDiscountPrice", equalTo(1000))
			.body("data.expectPrice", equalTo(9000))
			.body("data.cartItems[0].quantity", equalTo(1))
			.body("data.cartItems[0].period", equalTo(30))
			.body("data.cartItems[0].subscription", equalTo(true))
			.body("data.cartItems[0].item.itemId", equalTo(1))
			.body("data.cartItems[0].item.originPrice", equalTo(11000))
			.body("data.cartItems[0].item.realPrice", equalTo(0))
			.body("data.cartItems[0].item.discountRate", equalTo(10.0f))
			.body("data.cartItems[0].item.discountPrice", equalTo(1000));

	}

	@DisplayName("구독 카트에 상품을 추가할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 구독_카트_상품_추가() throws Exception {

		SubscriptionCartItemPostDto postDto = new SubscriptionCartItemPostDto();
		postDto.setItemId(2L);
		postDto.setQuantity(1);
		postDto.setPeriod(30);

		given()
			.log().all()
			.header("Authorization", getToken())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(postDto)
			.when()
			.post("/carts/subscription/2")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body("data", equalTo(2));
	}

	@DisplayName("구독 카트에 상품을 삭제할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 구독_카트_상품_삭제() throws Exception {
		given()
			.log().all()
			.header("Authorization", getToken())
			.queryParam("cartItemId", 1)
			.when()
			.delete("/carts/subscription/2")
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("구독 카트에 상품 개수를 변경할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 구독_카트_상품_개수_변경() throws Exception {
		given()
			.log().all()
			.header("Authorization", getToken())
			.queryParam("quantity", 2)
			.queryParam("cartItemId", 1)
			.when()
			.patch("/carts/subscription/2/quantity")
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName(" 구독 카트에 구독 기간을 변경할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 구독_카트_상품_구독_기간_변경() throws Exception {
		given()
			.log().all()
			.header("Authorization", getToken())
			.queryParam("period", 60)
			.queryParam("cartItemId", 1)
			.when()
			.patch("/carts/subscription/2/period")
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}
}