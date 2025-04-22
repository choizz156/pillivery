package com.team33.moduleapi.docs;

import static com.team33.modulecore.cache.CacheType.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.cart.dto.SubscriptionCartItemPostDto;
import com.team33.moduleapi.api.cart.mapper.CartServiceMapper;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.cart.application.CartKeySupplier;
import com.team33.modulecore.core.cart.application.MemoryCartService;
import com.team33.modulecore.core.cart.application.SubscriptionCartItemService;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

class SubscriptionCartDocsTest extends WebRestDocsSupport {

    private static final String BASE_URL = "/api/carts/subscription";
    private static final Long CART_ID = 2L;
    private static final String KEY = CartKeySupplier.from(CART_ID);

    @Autowired
    private ItemCommandRepository itemCommandRepository;
    @Autowired
    private CartServiceMapper cartServiceMapper;
    @Autowired
    private SubscriptionCartItemService subscriptionCartItemService;
    @Autowired
    private MemoryCartService memoryCartService;
    @Autowired
    private CacheManager cacheManager;


    private List<Item> items;
    private ItemVO firstItem;
    private SubscriptionContext subscriptionContext;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache(CARTS.name())).clear();
        items = createTestItems();
        itemCommandRepository.saveAll(items);
        firstItem = cartServiceMapper.toItemVO(1L);

        subscriptionContext = SubscriptionContext.builder()
            .item(firstItem)
            .subscriptionInfo(SubscriptionInfo.of(true, 30))
            .quantity(1)
            .build();

        subscriptionCartItemService.findCart(KEY, CART_ID);
        memoryCartService.addSubscriptionItem(KEY, subscriptionContext);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 구독_카트_조회_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .filter(document("get-subscription-cart",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("구독 장바구니 ID")),
                responseFields(
                    fieldWithPath("data.cartId").type(JsonFieldType.NUMBER).description("구독 장바구니 ID"),
                    fieldWithPath("data.cartItems[].quantity").type(JsonFieldType.NUMBER).description("상품 수량"),
                    fieldWithPath("data.cartItems[].item.itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.cartItems[].item.originPrice").type(JsonFieldType.NUMBER).description("정상 가격"),
                    fieldWithPath("data.cartItems[].item.discountPrice").type(JsonFieldType.NUMBER).description("할인 가격"),
                    fieldWithPath("data.totalItemCount").type(JsonFieldType.NUMBER).description("총 상품 개수"),
                    fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("총 결제 금액"),
                    fieldWithPath("data.totalDiscountPrice").type(JsonFieldType.NUMBER).description("총 할인 금액"),
                    fieldWithPath("data.expectPrice").type(JsonFieldType.NUMBER).description("최종 결제 금액"),
                    fieldWithPath("createTime").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("data.cartItems[].period").type(JsonFieldType.NUMBER).description("구독 주기"),
                    fieldWithPath("data.cartItems[].subscription").type(JsonFieldType.BOOLEAN).description("구독 여부"),
                    fieldWithPath("data.cartItems[].item.enterprise").type(JsonFieldType.STRING).description("판매 업체"),
                    fieldWithPath("data.cartItems[].item.thumbnail").type(JsonFieldType.STRING).description("상품 썸네일"),
                    fieldWithPath("data.cartItems[].item.product").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.cartItems[].item.realPrice").type(JsonFieldType.NUMBER).description("실제 가격"),
                    fieldWithPath("data.cartItems[].item.discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                    fieldWithPath("data.cartItems[].createdAt").type(JsonFieldType.NULL).description("생성 시간"),
                    fieldWithPath("data.cartItems[].updatedAt").type(JsonFieldType.NULL).description("수정 시간"))))
            .when()
            .get(BASE_URL + "/{cartId}", CART_ID)
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 구독_카트_상품_추가_문서화() {
        SubscriptionCartItemPostDto postDto = new SubscriptionCartItemPostDto();
        postDto.setItemId(2L);
        postDto.setPeriod(30);
        postDto.setQuantity(1);
        postDto.setSubscription(true);

        given(spec)
            .header("Authorization", getToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(postDto)
            .filter(document("add-subscription-cart-item",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰"),
                    headerWithName("Content-Type").description("요청 컨텐츠 타입")),
                pathParameters(
                    parameterWithName("cartId").description("구독 장바구니 ID")),
                requestFields(
                    fieldWithPath("itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("period").type(JsonFieldType.NUMBER).description("구독 주기"),
                    fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("상품 수량"),
                    fieldWithPath("subscription").type(JsonFieldType.BOOLEAN).description("구독 여부")),
                responseFields(
                    fieldWithPath("data").type(JsonFieldType.NUMBER).description("추가된 상품 ID"),
                    fieldWithPath("createTime").type(JsonFieldType.STRING).description("생성 시간"))))
            .when()
            .post(BASE_URL + "/{cartId}", CART_ID)
            .then()
            .log().all()
            .statusCode(201);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 구독_카트_상품_삭제_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .param("itemId", 1)
            .filter(document("delete-subscription-cart-item",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("구독 장바구니 ID")),
                requestParameters(
                    parameterWithName("itemId").description("삭제할 상품 ID"))))
            .when()
            .delete(BASE_URL + "/{cartId}", CART_ID)
            .then()
            .statusCode(204);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 구독_카트_상품_수량_변경_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .param("itemId", 1)
            .param("quantity", 2)
            .filter(document("update-subscription-cart-quantity",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("구독 장바구니 ID")),
                requestParameters(
                    parameterWithName("quantity").description("변경할 수량"),
                    parameterWithName("itemId").description("상품 ID"))))
            .when()
            .patch(BASE_URL + "/{cartId}/quantity", CART_ID)
            .then()
            .statusCode(204);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 구독_카트_상품_주기_변경_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .param("itemId", 1)
            .param("period", 60)
            .filter(document("update-subscription-cart-period",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("구독 장바구니 ID")),
                requestParameters(
                    parameterWithName("period").description("변경할 구독 주기"),
                    parameterWithName("itemId").description("상품 ID"))))
            .when()
            .patch(BASE_URL + "/{cartId}/period", CART_ID)
            .then()
            .statusCode(204);
    }

    private List<Item> createTestItems() {
        return FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
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
    }
}
