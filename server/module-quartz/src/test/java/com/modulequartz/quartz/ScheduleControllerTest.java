package com.modulequartz.quartz;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.ApiTest;
import com.team33.ModuleQuartzApplication;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.entity.ItemOrder;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.reposiroty.OrderRepository;
import com.team33.modulecore.domain.order.service.ItemOrderService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulequartz.subscription.service.SubscriptionService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("quartz")
@Import(ModuleQuartzApplication.class)
class ScheduleControllerTest extends ApiTest {

    @MockBean(name = "orderService")
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ItemOrderService itemOrderService;

    @Autowired
    private SubscriptionService subscriptionService;

    private User user;
    private Order order;
    private ItemOrder itemOrder;
    private Item item;


    @BeforeEach
    void setUp() {
        user = fixtureMonkey.giveMeBuilder(User.class)
            .set("userId", 1L)
            .sample();

        item = fixtureMonkey.giveMeBuilder(Item.class).set("title", "testItem").sample();

        ZonedDateTime now = ZonedDateTime.now();

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

        itemOrder.setNextDelivery(itemOrder.getPaymentDay().plusDays(30));
        itemOrder.setPaymentDay(itemOrder.getPaymentDay().plusDays(30));
        given(itemOrderService.updateDeliveryInfo(any(), any(), any(ItemOrder.class)))
            .willReturn(itemOrder);
    }

    private final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .build();

    @DisplayName("요청 시 스케쥴러가 설정된다.")
    @Test
    void test1() throws Exception {

        Long orderId = order.getOrderId();

        given()
            .log().all()
            .param("orderId", orderId)
            .when()
            .get("/schedule")
            .then()
            .assertThat().statusCode(HttpStatus.ACCEPTED.value())
            .assertThat().body(containsString("스케쥴 구성 완료"))
            .log().all();
    }

    @DisplayName("스케쥴을 수정할 수 있다.")
    @Test
    void test2() throws Exception {

        subscriptionService.startSchedule(order, itemOrder);

        given().log().all()
            .param("period", 30)
            .param("orderId", 1L)
            .param("itemOrderId", 1L)
            .when()
            .patch("/schedule/change")
            .then().statusCode(HttpStatus.ACCEPTED.value());

    }
}