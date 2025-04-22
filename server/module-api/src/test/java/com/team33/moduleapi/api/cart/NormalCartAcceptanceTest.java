package com.team33.moduleapi.api.cart;

import static com.team33.modulecore.cache.CacheType.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.cart.mapper.CartServiceMapper;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.cart.application.CartKeySupplier;
import com.team33.modulecore.core.cart.application.MemoryCartService;
import com.team33.modulecore.core.cart.application.NormalCartItemService;
import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;

class NormalCartAcceptanceTest extends ApiTest {

    private static final Long CART_ID = 1L;
    private static final String KEY = CartKeySupplier.from(CART_ID);

    @Autowired
    private ItemCommandRepository itemCommandRepository;
    @Autowired
    private CartServiceMapper cartServiceMapper;
    @Autowired
    private NormalCartItemService normalCartItemService;
    @Autowired
    private MemoryCartService memoryCartService;
    @Autowired
    private CacheManager cacheManager;

    private List<Item> items;
    private ItemVO firstItem;

    @BeforeEach
    void setUp() {
        clearCache();
        setupTestItems();
        setupInitialCart();
    }

	@DisplayName("일반 카트를 조회할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 일반_카트_조회() throws Exception {

		given()
			.log().all()
			.header("Authorization", getToken())
			.when()
			.get("/api/carts/normal/1")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.cartId", equalTo(1))
			.body("data.totalItemCount", equalTo(1))
			.body("data.totalPrice", equalTo(10000))
			.body("data.totalDiscountPrice", equalTo(1000))
			.body("data.expectPrice", equalTo(9000))
			.body("data.cartItems[0].quantity", equalTo(1))
			.body("data.cartItems[0].period", equalTo(0))
			.body("data.cartItems[0].subscription", equalTo(false))
			.body("data.cartItems[0].item.itemId", equalTo(1))
			.body("data.cartItems[0].item.originPrice", equalTo(11000))
			.body("data.cartItems[0].item.realPrice", equalTo(0))
			.body("data.cartItems[0].item.discountRate", equalTo(10.0f))
			.body("data.cartItems[0].item.discountPrice", equalTo(1000));

	}

	@DisplayName("카트에 상품을 추가할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 일반_카트_상품_추가() throws Exception {
		given()
			.log().all()
			.header("Authorization", getToken())
			.queryParam("quantity", 1)
			.queryParam("itemId", 2)
			.when()
			.post("/api/carts/normal/1")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body("data", equalTo(2));
	}

	@DisplayName("카트에 상품을 삭제할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 일반_카트_상품_삭제() throws Exception {
		given()
			.log().all()
			.header("Authorization", getToken())
			.queryParam("itemId", 1)
			.when()
			.delete("/api/carts/normal/1")
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("카트에 상품 개수를 변경할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 일반_카트_상품_개수_변경() throws Exception {
		given()
			.log().all()
			.header("Authorization", getToken())
			.queryParam("quantity", 2)
			.queryParam("itemId", 1)
			.when()
			.patch("/api/carts/normal/1")
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

    private void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(CARTS.name())).clear();
    }

    private void setupTestItems() {
        items = createTestItems();
        itemCommandRepository.saveAll(items);
        firstItem = cartServiceMapper.toItemVO(1L);
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

    private void setupInitialCart() {
        normalCartItemService.findCart(KEY, CART_ID);
        memoryCartService.addNormalItem(KEY, firstItem, 1);
    }
}