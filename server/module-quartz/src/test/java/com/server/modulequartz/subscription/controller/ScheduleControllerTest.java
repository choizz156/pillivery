package com.server.modulequartz.subscription.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.ApiTest;
import com.team33.ModuleQuartzApplication;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.entity.ItemOrder;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.reposiroty.OrderRepository;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;

//TODO: 정확하게 알아보기위해서... 지워야한다.
@ActiveProfiles("quartz")
@Import(ModuleQuartzApplication.class)
class ScheduleControllerTest extends ApiTest {

    @MockBean(name = "orderService")
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .build();

    @DisplayName("요청 시 스케쥴러가 설정된다.")
    @Test
    void test1() throws Exception {
        //given
        User user = fixtureMonkey.giveMeBuilder(User.class)
            .set("userId", 1L)
            .sample();

        Item item = fixtureMonkey.giveMeBuilder(Item.class).set("title", "testItem").sample();

        ItemOrder itemOrder = fixtureMonkey.giveMeBuilder(ItemOrder.class)
            .set("period", 30)
            .set("item", item)
            .sample();
        System.out.println(itemOrder+"==================");

        Order order = fixtureMonkey.giveMeBuilder(Order.class)
            .set("user", user)
            .set("orderId", 1L)
            .set("itemOrders", List.of(itemOrder))
            .set("createdAt", ZonedDateTime.now())
            .sample();

        BDDMockito.given(orderService.findOrder(anyLong()))
            .willReturn(order);
        BDDMockito.given(orderRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(order));

        Long orderId = order.getOrderId();

        //@formatter:off
            given(super.spec)
                .log().all()
                .param("orderId", orderId)
                .filter(document("quartz",
                        preprocessRequest(modifyUris()
                            .scheme("http")
                            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                            .removePort(), prettyPrint()
                        ),
                        preprocessResponse(prettyPrint()),
                        requestParameters(parameterWithName("orderId").description("주문 아이디")),
                        responseFields(fieldWithPath("data").type(JsonFieldType.STRING).description("스케쥴 구성 완료"))
                    )
                )
            .when()
                .get("/schedule")
            .then()
                .assertThat().statusCode(HttpStatus.ACCEPTED.value())
                .assertThat().body(containsString("스케쥴 구성 완료"))
                .log().all().extract();

    }
}