// package com.team33.moduleapi.restdocs;
//
// import static org.hamcrest.Matchers.*;
// import static org.springframework.restdocs.payload.PayloadDocumentation.*;
// import static org.springframework.restdocs.request.RequestDocumentation.*;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.redisson.api.RedissonClient;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.restdocs.RestDocumentationContextProvider;
// import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
// import org.springframework.restdocs.payload.JsonFieldType;
//
// import com.team33.moduleapi.FixtureMonkeyFactory;
// import com.team33.moduleapi.UserAccount;
// import com.team33.moduleapi.ui.cart.mapper.CartServiceMapper;
// import com.team33.modulecore.core.cart.application.CartKeySupplier;
// import com.team33.modulecore.core.cart.application.MemoryCartClient;
// import com.team33.modulecore.core.cart.application.NormalCartItemService;
// import com.team33.modulecore.core.cart.domain.ItemVO;
// import com.team33.modulecore.core.item.domain.entity.Item;
// import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
//
// class CartDocsTest extends WebRestDocsSupport {
//
// 	private static final String KEY = CartKeySupplier.from(1L);
//
// 	@Autowired
// 	private ItemCommandRepository itemCommandRepository;
//
// 	@Autowired
// 	private CartServiceMapper cartServiceMapper;
//
// 	@Autowired
// 	private NormalCartItemService normalCartItemService;
//
// 	@Autowired
// 	private RedissonClient redissonClient;
//
// 	@Autowired
// 	private MemoryCartClient memoryCartClient;
//
// 	private List<Item> items;
//
// 	@BeforeEach
// 	void setUp(RestDocumentationContextProvider restDocumentation) {
//
// 		redissonClient.getKeys().flushall();
//
// 		items = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
// 			.setNull("id")
// 			.setNull("itemCategory")
// 			.setNull("reviewIds")
// 			.setNull("categories")
// 			.set("statistics.starAvg", 0.0)
// 			.set("statistics.reviewCount", 0)
// 			.set("statistics.view", 0)
// 			.set("statistics.sales", 0)
// 			.set("information.price.realPrice", 10000)
// 			.set("information.price.discountPrice", 1000)
// 			.set("information.price.discountRate", 10.0)
// 			.set("information.price.originPrice", 11000)
// 			.sampleList(2);
//
// 		itemCommandRepository.saveAll(items);
// 		ItemVO itemVO = cartServiceMapper.toItemVO(1L);
//
// 		normalCartItemService.findCart(KEY, 1L);
// 		memoryCartClient.addNormalItem(KEY, itemVO, 1);
// 	}
//
// 	@DisplayName("일반 카트를 조회할 수 있다.")
// 	@UserAccount({"test", "010-0000-0000"})
// 	@Test
// 	void 일반_카트_조회() throws Exception {
//
// 		super.webSpec
// 			.log().all()
// 			.header("Authorization", getToken())
// 			.when()
// 			.get("/carts/normal/1")
// 			.then()
// 			.log().all()
// 			.statusCode(HttpStatus.OK.value())
// 			.apply(MockMvcRestDocumentation.document("get-normal-cart",
// 				responseFields(
// 					fieldWithPath("data.cartId").description("카트 ID"),
// 					fieldWithPath("data.totalItemCount").description("총 상품 수량"),
// 					fieldWithPath("data.totalPrice").description("총 상품 가격"),
// 					fieldWithPath("data.totalDiscountPrice").description("적용된 할인 가격"),
// 					fieldWithPath("data.expectPrice").description("기대되는 가격"),
// 					fieldWithPath("data.cartItems[0].quantity").description("상품 수량"),
// 					fieldWithPath("data.cartItems[0].period").description("주문 주기"),
// 					fieldWithPath("data.cartItems[0].subscription").description("구독 여부"),
// 					fieldWithPath("data.cartItems[0].item.itemId").description("상품 ID"),
// 					fieldWithPath("data.cartItems[0].item.originPrice").description("상품 원가"),
// 					fieldWithPath("data.cartItems[0].item.realPrice").description("실제 가격"),
// 					fieldWithPath("data.cartItems[0].item.discountRate").description("할인율"),
// 					fieldWithPath("data.cartItems[0].item.discountPrice").description("할인가"),
// 					fieldWithPath("data.cartItems[0].item.enterprise").description("제조 회사"),
// 					fieldWithPath("data.cartItems[0].item.thumbnail").description("상품 썸네일 URL"),
// 					fieldWithPath("data.cartItems[0].item.product").description("상품 이름"),
// 					fieldWithPath("data.cartItems[0].createdAt").description("추가일자").optional().type(JsonFieldType.NULL),
// 					fieldWithPath("data.cartItems[0].updatedAt").description("수정일자").optional().type(JsonFieldType.NULL),
// 					fieldWithPath("createTime")
// 						.type(JsonFieldType.STRING)
// 						.description("생성 시간")
// 				)
// 			));
// 	}
//
// 	@DisplayName("카트에 상품을 추가할 수 있다.")
// 	@UserAccount({"test", "010-0000-0000"})
// 	@Test
// 	void 일반_카트_상품_추가() throws Exception {
// 		Map<String, Object> request = new HashMap<>();
// 		request.put("itemId", 2L);
// 		request.put("quantity", 1);
//
// 		super.webSpec
// 			.log().all()
// 			.header("Authorization", getToken())
// 			.queryParams(request)
// 			.when()
// 			.post("/carts/normal/1")
// 			.then()
// 			.log().all()
// 			.statusCode(HttpStatus.CREATED.value())
// 			.body("data", equalTo(2))
// 			.apply(MockMvcRestDocumentation.document("add-item-to-cart",
// 				requestParameters(
// 					parameterWithName("itemId").description("상품 ID"),
// 					parameterWithName("quantity").description("추가할 상품 수량")
// 				),
// 				responseFields(
// 					fieldWithPath("data").description("추가된 상품의 ID"),
// 					fieldWithPath("createTime")
// 						.type(JsonFieldType.STRING)
// 						.description("생성 시간")
// 				)
// 			));
// 	}
//
// 	@DisplayName("카트에 상품을 삭제할 수 있다.")
// 	@UserAccount({"test", "010-0000-0000"})
// 	@Test
// 	void 일반_카트_상품_삭제() throws Exception {
// 		super.webSpec
// 			.log().all()
// 			.header("Authorization", getToken())
// 			.queryParam("itemId", 1)
// 			.when()
// 			.delete("/carts/normal/1")
// 			.then()
// 			.log().all()
// 			.statusCode(HttpStatus.NO_CONTENT.value())
// 			.apply(MockMvcRestDocumentation.document("remove-item-from-cart",
// 				requestParameters(
// 					parameterWithName("itemId").description("삭제할 상품 ID")
// 				)
// 			));
// 	}
//
// 	@DisplayName("카트에 상품 개수를 변경할 수 있다.")
// 	@UserAccount({"test", "010-0000-0000"})
// 	@Test
// 	void 일반_카트_상품_개수_변경() throws Exception {
// 		Map<String, Object> request = new HashMap<>();
// 		request.put("quantity", 2);
// 		request.put("itemId", 1);
//
// 		super.webSpec
// 			.log().all()
// 			.header("Authorization", getToken())
// 			.queryParams(request)
// 			.when()
// 			.patch("/carts/normal/1")
// 			.then()
// 			.log().all()
// 			.statusCode(HttpStatus.NO_CONTENT.value())
// 			.apply(MockMvcRestDocumentation.document("update-item-quantity-in-cart",
// 				requestParameters(
// 					parameterWithName("quantity").description("변경할 상품 수량"),
// 					parameterWithName("itemId").description("상품 ID")
// 				)
// 			));
// 	}
// }