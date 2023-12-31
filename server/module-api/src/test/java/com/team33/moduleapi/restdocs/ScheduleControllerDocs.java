package com.team33.moduleapi.restdocs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.moduleapi.controller.UserAccount;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.entity.ItemOrder;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.repository.OrderRepository;
import com.team33.modulecore.domain.order.service.ItemOrderService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.entity.User;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.response.ExtractableResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;


class ScheduleControllerDocs extends WebRestDocsSupport {

    @MockBean(name = "orderService")
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ItemOrderService itemOrderService;

    private User user;
    private Order order;
    private ItemOrder itemOrder;
    private Item item;
    private ZonedDateTime now;


    private final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @BeforeEach
    void setUp() {
        user = fixtureMonkey.giveMeBuilder(User.class)
            .set("userId", 1L)
            .sample();

        item = fixtureMonkey.giveMeBuilder(Item.class)
            .set("itemId", 1L)
            .set("title", "testItem").sample();

        now = ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));

        itemOrder = fixtureMonkey.giveMeBuilder(ItemOrder.class)
            .set("itemOrderId", 1L)
            .set("period", 30)
            .set("paymentDay", now)
            .set("nextDelivery", now.plusDays(30))
            .set("quantity", 1)
            .set("item", item)
            .sample();

        order = fixtureMonkey.giveMeBuilder(Order.class)
            .set("user", user)
            .set("orderId", 1L)
            .set("createdAt", now)
            .sample();

        order.setItemOrders(List.of(itemOrder));
        itemOrder.setOrder(order);

        given(orderService.findOrder(anyLong()))
            .willReturn(order);
        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(order));
        given(itemOrderService.findItemOrder(anyLong()))
            .willReturn(itemOrder);

        //다음 배송일 업데이트
        itemOrder.setNextDelivery(itemOrder.getPaymentDay().plusDays(60));
        itemOrder.setPaymentDay(itemOrder.getPaymentDay().plusDays(60));

        given(itemOrderService.updateDeliveryInfo(any(), any(), any(ItemOrder.class)))
            .willReturn(itemOrder);
    }

    @DisplayName("요청 시 스케쥴러가 설정된다.")
    @Test
    void test1() throws Exception {

        Long orderId = order.getOrderId();

        //@formatter:off
        super.webSpec
                        .param("orderId", orderId)
            .when()
                        .get("/schedule")
            .then()
                        .statusCode(HttpStatus.ACCEPTED.value())
                        .body(containsString("스케쥴 구성 완료"))
                        .assertThat()
                        .apply(document("quartz-setting",
                            preprocessRequest(modifyUris()
                                .scheme("http")
                                .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                                .removePort(), prettyPrint()
                            ),
                            preprocessResponse(prettyPrint()),
                            requestParameters(
                                parameterWithName("orderId").description("주문 아이디")
                            ),
                            responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("스케쥴 구성 완료")
                            ))
                        );
            //@formatter:on
    }


    @DisplayName("스케쥴을 수정할 수 있다.")
    @UserAccount({"test", "010-0000-0000"})
    @Test
    void test2() throws Exception {

        String token = super.getToken();

        ExtractableResponse<MockMvcResponse> response =
            super.webSpec
                .queryParam("period", 60)
                .queryParam("orderId", 1L)
                .queryParam("itemOrderId", 1L)
                .header("Authorization", token)
                .when()
                .patch("/schedule")
                .then()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .apply(document("quartz-change",
                        preprocessRequest(modifyUris()
                            .scheme("http")
                            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                            .removePort(), prettyPrint()
                        ),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                            parameterWithName("period").description("결제 주기"),
                            parameterWithName("orderId").description("주문 아이디"),
                            parameterWithName("itemOrderId").description("주문 아이템 아이디")
                        ),
                        responseFields(
                            fieldWithPath("data.orderId").type(JsonFieldType.NUMBER)
                                .description("주문 아이디"),
                            fieldWithPath("data.itemOrderId").type(JsonFieldType.NUMBER)
                                .description("주문된 아이템 아이디"),
                            fieldWithPath("data.quantity").type(JsonFieldType.NUMBER)
                                .description("총 주문 수량"),
                            fieldWithPath("data.period").type(JsonFieldType.NUMBER)
                                .description("구독 주기"),
                            fieldWithPath("data.item.itemId").type(JsonFieldType.NUMBER)
                                .description("아이템 아이디"),
                            fieldWithPath("data.item.brand").type(JsonFieldType.STRING)
                                .description("아이템 브랜드"),
                            fieldWithPath("data.item.thumbnail").type(JsonFieldType.STRING)
                                .description("아이템 섬네일"),
                            fieldWithPath("data.item.title").type(JsonFieldType.STRING)
                                .description("아이템 이름"),
                            fieldWithPath("data.item.capacity").type(JsonFieldType.NUMBER)
                                .description("아이템 수량"),
                            fieldWithPath("data.item.price").type(JsonFieldType.NUMBER)
                                .description("아이템 가격"),
                            fieldWithPath("data.item.discountRate").type(JsonFieldType.NUMBER)
                                .description("아이템 할인률"),
                            fieldWithPath("data.item.disCountPrice").type(JsonFieldType.NUMBER)
                                .description("아이템 할인가"),
                            fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                                .description("총 결제 금액"),
                            fieldWithPath("data.nextDelivery").type(JsonFieldType.STRING)
                                .description("다음 결제일 및 배송일")
                        )
                    )
                )
                .extract();

        String year = response.jsonPath().get("data.nextDelivery").toString().substring(0, 4);
        String month = response.jsonPath().get("data.nextDelivery").toString().substring(6, 7);
        String day = response.jsonPath().get("data.nextDelivery").toString().substring(9, 10);

        assertThat(year).isEqualTo(String.valueOf(itemOrder.getPaymentDay().getYear()));
        assertThat(month).isEqualTo(
            String.valueOf(itemOrder.getPaymentDay().getMonth().getValue())
        );
        assertThat(day).isEqualTo(
            String.valueOf(itemOrder.getPaymentDay().getDayOfMonth())
        );
    }

    @DisplayName("스케쥴을 취소할 수 있다.")
    @UserAccount({"test", "010-0000-0000"})
    @Test
    void test3() throws Exception {

        String token = super.getToken();

        ExtractableResponse<MockMvcResponse> response = super.webSpec
            .queryParam("orderId", 1L)
            .queryParam("itemOrderId", 1L)
            .header("Authorization", token)
            .when()
            .delete("/schedule")
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply(document("quartz-cancel",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                        parameterWithName("orderId").description("주문 아이디"),
                        parameterWithName("itemOrderId").description("주문 아이템 아이디")
                    ),
                    responseFields(
                        fieldWithPath("data").type(JsonFieldType.STRING).description("취소 일시")
                    )
                )
            )
            .extract();

        String year = response.jsonPath().get("data").toString().substring(0, 4);
        String month = response.jsonPath().get("data").toString().substring(6, 7);
        String day = response.jsonPath().get("data").toString().substring(9, 10);

        assertThat(year).isEqualTo(String.valueOf(ZonedDateTime.now().getYear()));
        assertThat(month).isEqualTo(String.valueOf(ZonedDateTime.now().getMonth().getValue()));
        assertThat(day).isEqualTo(String.valueOf(ZonedDateTime.now().getDayOfMonth()));
    }
}