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
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.cart.mapper.CartServiceMapper;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.cart.application.CartKeySupplier;
import com.team33.modulecore.core.cart.application.MemoryCartClient;
import com.team33.modulecore.core.cart.application.NormalCartItemService;
import com.team33.modulecore.core.cart.dto.ItemVO;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;

class NormalCartDocsTest extends WebRestDocsSupport {

    private static final String BASE_URL = "/api/carts/normal";
    private static final Long CART_ID = 1L;
    private static final String KEY = CartKeySupplier.from(CART_ID);
    @Autowired
    private ItemCommandRepository itemCommandRepository;
    @Autowired
    private CartServiceMapper cartServiceMapper;
    @Autowired
    private NormalCartItemService normalCartItemService;
    @Autowired
    private MemoryCartClient memoryCartClient;
    @Autowired
    private CacheManager cacheManager;
    private List<Item> items;
    private ItemVO firstItem;

    @BeforeEach
    void setUp() {

        Objects.requireNonNull(cacheManager.getCache(CARTS.name())).clear();
        items = createTestItems();
        itemCommandRepository.saveAll(items);
        firstItem = cartServiceMapper.toItemVO(1L);
        normalCartItemService.findCart(KEY, CART_ID);
        memoryCartClient.addNormalItem(KEY, firstItem, 1);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 일반_카트_조회_문서화() throws Exception {

        given(spec)
            .header("Authorization", getToken())
            .log().all()
            .filter(document("get-normal-cart",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("일반 장바구니 ID")),
                responseFields(
                    fieldWithPath("data.cartId").type(JsonFieldType.NUMBER).description("일반 장바구니 ID"),
                    fieldWithPath("data.cartItems[].quantity").type(JsonFieldType.NUMBER)
                        .description("상품 수량"),
                    fieldWithPath("data.cartItems[].item.itemId").type(JsonFieldType.NUMBER)
                        .description("상품 ID"),
                    fieldWithPath("data.cartItems[].item.originPrice").type(JsonFieldType.NUMBER)
                        .description("정상 가격"),
                    fieldWithPath("data.cartItems[].item.discountPrice").type(JsonFieldType.NUMBER)
                        .description("할인 가격"),
                    fieldWithPath("data.totalItemCount").type(JsonFieldType.NUMBER).description("총 상품 개수"),
                    fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("총 결제 금액"),
                    fieldWithPath("data.totalDiscountPrice").type(JsonFieldType.NUMBER)
                        .description("총 할인 금액"),
                    fieldWithPath("data.expectPrice").type(JsonFieldType.NUMBER).description("최종 결제 금액"),
                    fieldWithPath("createTime").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("data.cartItems[].period").type(JsonFieldType.NUMBER)
                        .description("주기"),
                    fieldWithPath("data.cartItems[].subscription").type(JsonFieldType.BOOLEAN)
                        .description("구독 여부"),
                    fieldWithPath("data.cartItems[].item.enterprise").type(JsonFieldType.STRING)
                        .description("판매 업체"),
                    fieldWithPath("data.cartItems[].item.thumbnail").type(JsonFieldType.STRING)
                        .description("상품 썸네일"),
                    fieldWithPath("data.cartItems[].item.product").type(JsonFieldType.STRING)
                        .description("상품명"),
                    fieldWithPath("data.cartItems[].item.realPrice").type(JsonFieldType.NUMBER)
                        .description("실제 가격"),
                    fieldWithPath("data.cartItems[].item.discountRate").type(JsonFieldType.NUMBER)
                        .description("할인율"),
                    fieldWithPath("data.cartItems[].createdAt").type(JsonFieldType.NULL)
                        .description("생성 시간"),
                    fieldWithPath("data.cartItems[].updatedAt").type(JsonFieldType.NULL)
                        .description("수정 시간"))))
            .when()
            .get(BASE_URL + "/{cartId}", 1)
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 일반_카트_상품_추가_문서화() throws Exception {

        given(spec)
            .header("Authorization", getToken())
            .param("itemId", 2)
            .param("quantity", 1)
            .filter(document("add-normal-cart-item",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("일반 장바구니 ID")),
                requestParameters(
                    parameterWithName("quantity").description("상품 수량"),
                    parameterWithName("itemId").description("상품 ID"))))
            .when()
            .post(BASE_URL + "/{cartId}", CART_ID)
            .then()
            .log().all()
            .statusCode(201);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 일반_카트_상품_삭제_문서화() throws Exception {

        given(spec)
            .header("Authorization", getToken())
            .param("itemId", 1)
            .filter(document("delete-normal-cart-item",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("일반 장바구니 ID")),
                requestParameters(
                    parameterWithName("itemId").description("삭제할 상품 ID"))))
            .when()
            .delete(BASE_URL + "/{cartId}", CART_ID)
            .then()
            .statusCode(204);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 일반_카트_상품_개수_변경_문서화() throws Exception {

        given(spec)
            .header("Authorization", getToken())
            .param("itemId", 1)
            .param("quantity", 2)
            .filter(document("update-normal-cart-item",
                requestHeaders(headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("cartId").description("일반 장바구니 ID")),
                requestParameters(
                    parameterWithName("quantity").description("변경할 수량"),
                    parameterWithName("itemId").description("상품 ID"))))
            .when()
            .patch(BASE_URL + "/{cartId}", CART_ID)
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