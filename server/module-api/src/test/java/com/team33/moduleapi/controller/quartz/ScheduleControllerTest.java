// package com.team33.moduleapi.controller.quartz;
//
// import static io.restassured.RestAssured.given;
// import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
// import static org.hamcrest.Matchers.containsString;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.BDDMockito.given;
//
// import com.navercorp.fixturemonkey.FixtureMonkey;
// import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
// import com.team33.moduleapi.controller.ApiTest;
// import com.team33.moduleapi.controller.UserAccount;
// import com.team33.modulecore.item.domain.entity.Item;
// import com.team33.modulecore.order.application.OrderItemService;
// import com.team33.modulecore.order.application.OrderQueryService;
// import com.team33.modulecore.order.application.OrderCreateService;
// import com.team33.modulecore.order.domain.entity.Order;
// import com.team33.modulecore.order.domain.OrderItem;
// import com.team33.modulecore.order.domain.repository.OrderRepository;
// import com.team33.modulecore.user.domain.entity.User;
// import io.restassured.response.ExtractableResponse;
// import io.restassured.response.Response;
// import java.time.ZoneId;
// import java.time.ZonedDateTime;
// import java.util.List;
// import java.util.Optional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.HttpStatus;
//
//
// class ScheduleControllerTest extends ApiTest {
//
//     @MockBean(name = "orderService")
//     private OrderCreateService orderCreateService;
//
//     @MockBean
//     private OrderQueryService orderQueryService;
//
//     @MockBean
//     private OrderRepository orderRepository;
//
//     @MockBean
//     private OrderItemService orderItemService;
//
//
//     private User user;
//     private Order order;
//     private OrderItem orderItem;
//     private Item item;
//     private ZonedDateTime now;
//
//
//     private final FixtureMonkey fixtureMonkey = FixtureMonkey
//         .builder()
//         .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
//         .build();
//
//     @BeforeEach
//     void setUp() {
//         user = fixtureMonkey.giveMeBuilder(User.class)
//             .set("userId", 1L)
//             .sample();
//
//         item = fixtureMonkey.giveMeBuilder(Item.class)
//             .set("itemId", 1L)
//             .set("title", "testItem").sample();
//
//         now = ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
//
//         orderItem = fixtureMonkey.giveMeBuilder(OrderItem.class)
//             .set("itemOrderId", 1L)
//             .set("period", 30)
//             .set("paymentDay", now)
//             .set("nextDelivery", now.plusDays(30))
//             .set("quantity", 1)
//             .set("item", item)
//             .sample();
//
//         order = fixtureMonkey.giveMeBuilder(Order.class)
//             .set("user", user)
//             .set("orderId", 1L)
//             .set("createdAt", now)
//             .sample();
//
//         order.setOrderItems(List.of(orderItem));
//         orderItem.setOrder(orider);
//
//         given(orderQueryService.findOrder(anyLong()))
//             .willReturn(order);
//         given(orderRepository.findById(anyLong()))
//             .willReturn(Optional.ofNullable(order));
//         given(orderItemService.findOrderItem(anyLong()))
//             .willReturn(orderItem);
//
//         //다음 배송일 업데이트
//         orderItem.setNextDelivery(orderItem.getPaymentDay().plusDays(60));
//         orderItem.setPaymentDay(orderItem.getPaymentDay().plusDays(60));
//
//         given(orderItemService.updateDeliveryInfo(any(), any(), any(OrderItem.class)))
//             .willReturn(orderItem);
//     }
//
//     @DisplayName("요청 시 스케쥴러가 설정된다.")
//     @Test
//     void test1() throws Exception {
//
//         Long orderId = order.getId();
//         //@formatter:off
//         given()
//                 .log().all()
//                 .param("orderId", orderId)
//         .when()
//                 .get("/schedule")
//         .then()
//                 .statusCode(HttpStatus.ACCEPTED.value())
//                 .body(containsString("스케쥴 구성 완료"))
//                 .log().all();
//         //@formatter:on
//     }
//
//     @DisplayName("스케쥴을 수정할 수 있다.(30 -> 60)")
//     @UserAccount({"test", "010-0000-0000"})
//     @Test
//     void test2() throws Exception {
//
//         String token = super.getToken();
//
//         ExtractableResponse<Response> response =
//             //@formatter:off
//             given()
//                     .log().all()
//                     .header("Authorization", token)
//                     .param("period", 60)
//                     .param("orderId", 1L)
//                     .param("itemOrderId", 1L)
//             .when()
//                     .patch("/schedule")
//             .then()
//                     .log().all()
//                     .statusCode(HttpStatus.OK.value())
//                     .extract();
//             //@formatter:on
//
//         String year = response.jsonPath().get("data.nextDelivery").toString().substring(0, 4);
//         String month = response.jsonPath().get("data.nextDelivery").toString().substring(6, 7);
//         String day = response.jsonPath().get("data.nextDelivery").toString().substring(9, 10);
//
//         System.out.println(year);
//         System.out.println(month);
//         System.out.println(day);
//
//         assertThat(year).isEqualTo(String.valueOf(orderItem.getPaymentDay().getYear()));
//         assertThat(month).isEqualTo(
//             String.valueOf(orderItem.getPaymentDay().getMonth().getValue())
//         );
//         assertThat(day).isEqualTo(
//             String.valueOf(orderItem.getPaymentDay().getDayOfMonth())
//         );
//     }
//
//     @DisplayName("스케쥴을 취소할 수 있다.")
//     @UserAccount({"test", "010-0000-0000"})
//     @Test
//     void test3() throws Exception {
//
//         String token = super.getToken();
//
//         ExtractableResponse<Response> response =
//             //@formatter:off
//             given()
//                     .log().all()
//                     .header("Authorization", token)
//                     .param("orderId", 1L)
//                     .param("itemOrderId", 1L)
//             .when()
//                     .delete("/schedule")
//             .then()
//                     .log().all()
//                     .statusCode(HttpStatus.OK.value())
//                     .extract();
//             //@formatter:on
//
//         String year = response.jsonPath().get("data").toString().substring(0, 4);
//         String month = response.jsonPath().get("data").toString().substring(6, 7);
//         String day = response.jsonPath().get("data").toString().substring(9, 10);
//
//         assertThat(year).isEqualTo(String.valueOf(ZonedDateTime.now().getYear()));
//         assertThat(month).isEqualTo(String.valueOf(ZonedDateTime.now().getMonth().getValue()));
//         assertThat(day).isEqualTo(String.valueOf(ZonedDateTime.now().getDayOfMonth()));
//     }
// }