package com.team33.moduleapi.docs;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.review.dto.ReviewDeleteDto;
import com.team33.moduleapi.api.review.dto.ReviewPatchDto;
import com.team33.moduleapi.api.review.dto.ReviewPostDto;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.Receiver;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.review.application.ReviewCommandService;
import com.team33.modulecore.core.review.domain.ReviewContext;
import com.team33.modulecore.core.review.domain.entity.Review;
import com.team33.modulecore.core.review.domain.repository.ReviewCommandRepository;
import com.team33.modulecore.core.review.dto.query.ReviewSortOption;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;

class ReviewDocsTest extends WebRestDocsSupport {

        private static final String BASE_URL = "/api/reviews";

        @Autowired
        private ReviewCommandService reviewCommandService;

        @Autowired
        private ReviewCommandRepository reviewCommandRepository;

        @Autowired
        private ItemCommandRepository itemCommandRepository;

        @Autowired
        private UserRepository userRepository;

        private List<Item> items;
        private User testUser;
        private Review testReview;

        @Autowired
        private OrderCommandRepository orderCommandRepository;

        @BeforeEach
        void setUp() {
                // Create test items
                items = createTestItems();
                itemCommandRepository.saveAll(items);


                testUser = userRepository.findById(1L).orElse(null);
                if (testUser == null) {
                        testUser = FixtureMonkeyFactory.get().giveMeBuilder(User.class)
                                        .setNull("id")
                                        .set("email", "test@example.com")
                                        .set("displayName", "테스트유저")
                                        .set("phone", "010-0000-0000")
                                        .sample();
                        testUser = userRepository.save(testUser);
                }

                // Create test order with order items
                OrderItem orderItem = OrderItem.create(
                                items.get(0), // Use the first test item
                                2,
                                SubscriptionInfo.of(false, 0));

                List<OrderItem> orderItems = new ArrayList<>();
                orderItems.add(orderItem);

                // Create receiver
                com.team33.modulecore.core.user.domain.Address receiverAddress = com.team33.modulecore.core.user.domain.Address
                                .builder()
                                .city("서울시")
                                .detailAddress("강남구 역삼동")
                                .build();

                Receiver receiver = Receiver.builder()
                                .realName("홍길동")
                                .phone("010-1234-5678")
                                .address(receiverAddress)
                                .build();

                // Create order context
                OrderContext orderContext = OrderContext.builder()
                                .userId(testUser.getId())
                                .isOrderedCart(true)
                                .isSubscription(false)
                                .receiver(receiver)
                                .period(0)
                                .build();

                // Create order
                OrderCommonInfo orderCommonInfo = OrderCommonInfo.createFromContext(orderItems, orderContext);
                Order testOrder = Order.create(orderItems, orderCommonInfo, orderContext);
                testOrder.changeOrderStatus(OrderStatus.COMPLETE);
                orderCommandRepository.save(testOrder);

                // Create test review
                ReviewContext reviewContext = ReviewContext.builder()
                                .content("좋은 상품입니다.")
                                .star(4.5)
                                .itemId(1L)
                                .userId(testUser.getId())
                                .displayName(testUser.getDisplayName())
                                .orderId(1L)
                                .build();

                // Delete existing review if exists to avoid duplication
                try {
                        testReview = reviewCommandService.findReview(1L);
                        reviewCommandService.deleteReview(ReviewContext.builder()
                                        .reviewId(testReview.getId())
                                        .userId(testUser.getId())
                                        .itemId(testReview.getItemId())
                                        .build());
                } catch (Exception e) {
                        // Ignore if review doesn't exist
                }

                // Create new review
                testReview = reviewCommandService.createReview(reviewContext);
        }

        @Test
        // @Disabled("API endpoint not implemented or test data missing")
        @UserAccount({ "test", "010-0000-0000" })
        void 리뷰_생성_문서화() {
                ReviewPostDto reviewPostDto = ReviewPostDto.builder()
                                .userId(testUser.getId())
                                .itemId(1L) // Use the second item to avoid duplication
                                .orderId(1L)
                                .displayName(testUser.getDisplayName())
                                .content("좋은 상품입니다.")
                                .star(4.5)
                                .build();

                given(spec)
                                .header("Authorization", getToken())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .body(reviewPostDto)
                                .filter(document("review-create",
                                                requestHeaders(
                                                                headerWithName("Authorization")
                                                                                .description("Bearer 인증 토큰"),
                                                                headerWithName("Content-Type")
                                                                                .description("요청 컨텐츠 타입")),
                                                requestFields(
                                                                fieldWithPath("userId").type(JsonFieldType.NUMBER)
                                                                                .description("사용자 ID"),
                                                                fieldWithPath("itemId").type(JsonFieldType.NUMBER)
                                                                                .description("상품 ID"),
                                                                fieldWithPath("orderId").type(JsonFieldType.NUMBER)
                                                                                .description("주문 ID"),
                                                                fieldWithPath("displayName").type(JsonFieldType.STRING)
                                                                                .description("사용자 닉네임"),
                                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                                                .description("리뷰 내용"),
                                                                fieldWithPath("star").type(JsonFieldType.NUMBER)
                                                                                .description("별점"))))
                                .when()
                                .post(BASE_URL)
                                .then()
                                .log().all()
                                .statusCode(201);
        }

        @Test
        // @Disabled("API endpoint not implemented or test data missing")
        @UserAccount({ "test", "010-0000-0000" })
        void 리뷰_수정_문서화() {
                ReviewPatchDto reviewPatchDto = ReviewPatchDto.builder()
                                .userId(1L)
                                .itemId(1L)
                                .reviewId(1L)
                                .content("수정된 리뷰 내용입니다.")
                                .star(5.0)
                                .build();

                given(spec)
                                .header("Authorization", getToken())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .body(reviewPatchDto)
                                .filter(document("review-update",
                                                requestHeaders(
                                                                headerWithName("Authorization")
                                                                                .description("Bearer 인증 토큰"),
                                                                headerWithName("Content-Type")
                                                                                .description("요청 컨텐츠 타입")),
                                                requestFields(
                                                                fieldWithPath("userId").type(JsonFieldType.NUMBER)
                                                                                .description("사용자 ID"),
                                                                fieldWithPath("itemId").type(JsonFieldType.NUMBER)
                                                                                .description("상품 ID"),
                                                                fieldWithPath("reviewId").type(JsonFieldType.NUMBER)
                                                                                .description("리뷰 ID"),
                                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                                                .description("수정할 리뷰 내용"),
                                                                fieldWithPath("star").type(JsonFieldType.NUMBER)
                                                                                .description("수정할 별점"))))
                                .when()
                                .patch(BASE_URL)
                                .then()
                                .log().all()
                                .statusCode(anyOf(is(200), is(201), is(204)));
        }

        @Test
        // @Disabled("API endpoint not implemented or test data missing")
        @UserAccount({ "test", "010-0000-0000" })
        void 리뷰_삭제_문서화() {
                ReviewDeleteDto reviewDeleteDto = ReviewDeleteDto.builder()
                                .userId(1L)
                                .itemId(1L)
                                .reviewId(1L)
                                .build();

                given(spec)
                                .header("Authorization", getToken())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .body(reviewDeleteDto)
                                .filter(document("review-delete",
                                                requestHeaders(
                                                                headerWithName("Authorization")
                                                                                .description("Bearer 인증 토큰"),
                                                                headerWithName("Content-Type")
                                                                                .description("요청 컨텐츠 타입")),
                                                requestFields(
                                                                fieldWithPath("userId").type(JsonFieldType.NUMBER)
                                                                                .description("사용자 ID"),
                                                                fieldWithPath("itemId").type(JsonFieldType.NUMBER)
                                                                                .description("상품 ID"),
                                                                fieldWithPath("reviewId").type(JsonFieldType.NUMBER)
                                                                                .description("리뷰 ID"))))
                                .when()
                                .delete(BASE_URL)
                                .then()
                                .log().all()
                                .statusCode(anyOf(is(200), is(201), is(204)));
        }

        @Test
        // @Disabled("API endpoint not implemented or test data missing")
        void 리뷰_조회_문서화() {
                given(spec)
                                .filter(document("review-get",
                                                pathParameters(
                                                                parameterWithName("reviewId").description("리뷰 ID"))))
                                .when()
                                .get(BASE_URL + "/{reviewId}", 1)
                                .then()
                                .log().all()
                                .statusCode(anyOf(is(200), is(201), is(204)));
        }

        @Test
        // @Disabled("API endpoint not implemented or test data missing")
        void 상품별_리뷰_조회_문서화() {
                given(spec)
                                .param("page", 1)
                                .param("size", 8)
                                .param("sort", ReviewSortOption.NEWEST)
                                .filter(document("review-by-item",
                                                pathParameters(
                                                                parameterWithName("itemId").description("상품 ID")),
                                                requestParameters(
                                                                parameterWithName("page").description("페이지 번호"),
                                                                parameterWithName("size").description("페이지 크기"),
                                                                parameterWithName("sort").description("정렬 방식"))))
                                .when()
                                .get(BASE_URL + "/items/{itemId}", testReview.getItemId()) // Use the item ID from the
                                                                                           // test review
                                .then()
                                .log().all()
                                .statusCode(200);
        }

        @Test
        // @Disabled("API endpoint not implemented or test data missing")
        void 사용자별_리뷰_조회_문서화() {
                given(spec)
                                .param("page", 1)
                                .param("size", 8)
                                .param("sort", ReviewSortOption.NEWEST)
                                .filter(document("review-by-user",
                                                pathParameters(
                                                                parameterWithName("userId").description("사용자 ID")),
                                                requestParameters(
                                                                parameterWithName("page").description("페이지 번호"),
                                                                parameterWithName("size").description("페이지 크기"),
                                                                parameterWithName("sort").description("정렬 방식"))))
                                .when()
                                .get(BASE_URL + "/users/{userId}", testUser.getId()) // Use the user ID from the test
                                                                                     // user
                                .then()
                                .log().all()
                                .statusCode(200);
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
