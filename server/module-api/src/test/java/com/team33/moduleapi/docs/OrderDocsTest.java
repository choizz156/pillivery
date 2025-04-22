package com.team33.moduleapi.docs;

import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.moduleapi.api.order.dto.OrderPostDto;
import com.team33.moduleapi.api.order.dto.OrderPostListDto;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Price;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.Receiver;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;

class OrderDocsTest extends WebRestDocsSupport {

    protected static final String BASE_URL = "/api/orders";
    @Autowired
    private ItemCommandRepository itemCommandRepository;
    @Autowired
    private OrderCommandRepository orderCommandRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Create test user if not exists
        User user = userRepository.findById(1L).orElse(null);
        if (user == null) {
            com.team33.modulecore.core.user.domain.Address address = com.team33.modulecore.core.user.domain.Address.builder()
                .city("서울")
                .detailAddress("압구정동")
                .build();

            user = User.builder()
                .email("test@test.com")
                .displayName("test")
                .password("1234")
                .address(address)
                .realName("홍길동")
                .phone("010-0000-0000")
                .roles(com.team33.modulecore.core.user.domain.UserRoles.USER)
                .userStatus(com.team33.modulecore.core.user.domain.UserStatus.USER_ACTIVE)
                .build();
            userRepository.save(user);
        }

        // Create test item if not exists
        Item item = itemCommandRepository.findById(1L).orElse(null);
        if (item == null) {
            Information information = Information.builder()
                .enterprise("테스트 업체")
                .productName("테스트 상품")
                .servingUse("1일 1회")
                .price(new Price(10000, 10.0))
                .image(new Image("http://example.com/thumbnail.jpg", "http://example.com/description.jpg"))
                .build();

            item = Item.create(information);
            itemCommandRepository.save(item);
        }

        // Create test order if not exists
        Order order = orderCommandRepository.findById(1L).orElse(null);
        if (order == null) {
            // Create order item
            OrderItem orderItem = OrderItem.create(
                item,
                2,
                SubscriptionInfo.of(false, 0)
            );

            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem);

            // Create receiver
            com.team33.modulecore.core.user.domain.Address receiverAddress = com.team33.modulecore.core.user.domain.Address.builder()
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
                .userId(1L)
                .isOrderedCart(true)
                .isSubscription(false)
                .receiver(receiver)
                .period(0)
                .build();

            // Create order
            OrderCommonInfo orderCommonInfo = OrderCommonInfo.createFromContext(orderItems, orderContext);
            order = Order.create(orderItems, orderCommonInfo, orderContext);

            orderCommandRepository.save(order);
        }

        // Create subscription order if not exists
        Order subscriptionOrder = orderCommandRepository.findById(2L).orElse(null);
        if (subscriptionOrder == null) {
            // Create subscription order item
            OrderItem subscriptionOrderItem = OrderItem.create(
                item,
                3,
                SubscriptionInfo.of(true, 30) // 30 days subscription period
            );

            List<OrderItem> subscriptionOrderItems = new ArrayList<>();
            subscriptionOrderItems.add(subscriptionOrderItem);

            // Create receiver
            com.team33.modulecore.core.user.domain.Address receiverAddress = com.team33.modulecore.core.user.domain.Address.builder()
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
                .userId(1L)
                .isOrderedCart(true)
                .isSubscription(true)
                .receiver(receiver)
                .period(30)
                .build();

            // Create order
            OrderCommonInfo orderCommonInfo = OrderCommonInfo.createFromContext(subscriptionOrderItems, orderContext);
            subscriptionOrder = Order.create(subscriptionOrderItems, orderCommonInfo, orderContext);

            orderCommandRepository.save(subscriptionOrder);
        }
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 주문_생성_문서화() {
        // Create order items
        List<OrderPostDto> orderItems = new ArrayList<>();
        orderItems.add(OrderPostDto.builder()
            .itemId(1L)
            .quantity(2)
            .subscription(false)
            .build());

        // Create order request
        OrderPostListDto orderRequest = OrderPostListDto.builder()
            .userId(1L)
            .subscription(false)
            .orderedAtCart(true)
            .city("서울시")
            .detailAddress("강남구 역삼동")
            .realName("홍길동")
            .phoneNumber("010-1234-5678")
            .orderPostDtoList(orderItems)
            .build();

        given(spec)
            .header("Authorization", getToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(orderRequest)
            .filter(document("order-create",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰"),
                    headerWithName("Content-Type").description("요청 컨텐츠 타입")),
                requestFields(
                    fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("subscription").type(JsonFieldType.BOOLEAN).description("구독 여부"),
                    fieldWithPath("orderedAtCart").type(JsonFieldType.BOOLEAN).description("장바구니에서 주문 여부"),
                    fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                    fieldWithPath("realName").type(JsonFieldType.STRING).description("수령인 이름"),
                    fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("연락처"),
                    fieldWithPath("orderPostDtoList").type(JsonFieldType.ARRAY).description("주문 상품 목록"),
                    fieldWithPath("orderPostDtoList[].itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("orderPostDtoList[].quantity").type(JsonFieldType.NUMBER).description("수량"),
                    fieldWithPath("orderPostDtoList[].period").type(JsonFieldType.NUMBER).description("구독 주기").optional(),
                    fieldWithPath("orderPostDtoList[].subscription").type(JsonFieldType.BOOLEAN).description("구독 여부")),
                relaxedResponseFields(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("createTime").type(JsonFieldType.STRING).description("응답 생성 시간"))))
            .when()
            .post(BASE_URL)
            .then()
            .log().all()
            .statusCode(201);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 구독_상품_수량_변경_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .param("orderItemId", 1)
            .param("quantity", 3)
            .filter(document("order-subscription-quantity-change",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("orderId").description("주문 ID")),
                requestParameters(
                    parameterWithName("orderItemId").description("주문 상품 ID"),
                    parameterWithName("quantity").description("변경할 수량"))))
            .when()
            .patch(BASE_URL + "/subscriptions/{orderId}", 1)
            .then()
            .log().all()
            .statusCode(204);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 주문_목록_조회_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .param("userId", 1)
            .param("page", 1)
            .param("size", 10)
            .param("sort", "DESC")
            .filter(document("order-list",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")),
                requestParameters(
                    parameterWithName("userId").description("사용자 ID"),
                    parameterWithName("page").description("페이지 번호"),
                    parameterWithName("size").description("페이지 크기"),
                    parameterWithName("sort").description("정렬 방향"))))
            .when()
            .get(BASE_URL)
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 구독_주문_목록_조회_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .param("userId", 1)
            .param("page", 1)
            .param("size", 10)
            .param("sort", "DESC")
            .filter(document("order-subscription-list",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")),
                requestParameters(
                    parameterWithName("userId").description("사용자 ID"),
                    parameterWithName("page").description("페이지 번호"),
                    parameterWithName("size").description("페이지 크기"),
                    parameterWithName("sort").description("정렬 방향"))))
            .when()
            .get(BASE_URL + "/subscriptions")
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 주문_상세_조회_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .filter(document("order-detail",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("orderId").description("주문 ID")),
                relaxedResponseFields(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("createTime").type(JsonFieldType.STRING).description("응답 생성 시간"))))
            .when()
            .get(BASE_URL + "/{orderId}", 1)
            .then()
            .log().all()
            .statusCode(200);
    }
}
