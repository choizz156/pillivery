package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.orderitem.domain.SubscriptionItemInfo;
import com.team33.modulecore.orderitem.dto.OrderItemSimpleResponse;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;


class OrderQueryServiceTest extends OrderDomainHelper {


    @Autowired
    OrderQueryService orderQueryService;

    @Transactional
    @DisplayName("유저의 전체 주문 목록을 조회할 수 있다.")
    @Test
    void 주문_목록_조회() throws Exception {
        //given
        var user = getUser();
        var items = getItems();
        var orderItemInfos1 = List.of(
            SubscriptionItemInfo.of(false, 0),
            SubscriptionItemInfo.of(false, 0),
            SubscriptionItemInfo.of(false, 0)
        );

        var orderItemInfoSubs = List.of(
            SubscriptionItemInfo.of(true, 30),
            SubscriptionItemInfo.of(true, 30),
            SubscriptionItemInfo.of(true, 30)
        );

        var orderItems = List.of(
            OrderItem.createWithoutOrder(items.get(0), orderItemInfos1.get(0), 3),
            OrderItem.createWithoutOrder(items.get(1), orderItemInfos1.get(1), 2),
            OrderItem.createWithoutOrder(items.get(2), orderItemInfos1.get(2), 2)
        );

        var orderItemsSubs = List.of(
            OrderItem.createWithoutOrder(items.get(0), orderItemInfoSubs.get(0), 3
            ),
            OrderItem.createWithoutOrder(items.get(1), orderItemInfoSubs.get(1), 3
            ),
            OrderItem.createWithoutOrder(items.get(2), orderItemInfoSubs.get(2), 3
            )
        );

        var order = orderService.callOrder(orderItems, false, user.getId());
        var orderSubs = orderService.callOrder(orderItemsSubs, true, user.getId());
        orderService.completeOrder(order.getId());
        orderService.subscribeOrder(orderSubs.getId());

        var orderPageRequest = OrderPageRequest.of(1, 5);
        //when
        Page<Order> allOrders = orderQueryService.findAllOrders(user.getId(), orderPageRequest);

        //then
        List<Order> content = allOrders.getContent();
        assertThat(allOrders.getSize()).isEqualTo(7);
        assertThat(allOrders.getTotalPages()).isEqualTo(1);
        assertThat(allOrders.getNumberOfElements()).isEqualTo(2);
        assertThat(content).hasSize(2)
            .extracting("id", "user", "orderStatus")
            .containsExactly(
                tuple(orderSubs.getId(), user, OrderStatus.ORDER_SUBSCRIBE),
                tuple(order.getId(), user, OrderStatus.ORDER_COMPLETE)
            );

        assertThat(content.get(0).getOrderItems())
            .hasSize(3)
            .extracting("item")
            .containsExactlyInAnyOrder(items.get(0), items.get(1), items.get(2));

        assertThat(content.get(1).getOrderItems())
            .hasSize(3)
            .extracting("item")
            .containsExactlyInAnyOrder(items.get(0), items.get(1), items.get(2));
    }



    @Transactional
    @DisplayName("유저의 정기 주문(구독) 목록을 조회할 수 있다.")
    @Test
    void 정기_주문_목록_조회() throws Exception {
        //given
        var user = getUser();
        var items = getItems();

        List<OrderItem> orderItems = items.stream()
            .map(item -> OrderItem.createWithoutOrder(
                item,
                SubscriptionItemInfo.of(true, 60),
                1
            ))
            .collect(Collectors.toList());
        var orderSubs1 = orderService.callOrder(orderItems, true, user.getId());
        orderService.subscribeOrder(orderSubs1.getId());

        var orderPageRequest1 = OrderPageRequest.of(1, 5);
        var orderPageRequest2 = OrderPageRequest.of(2, 10);

        //when
        List<OrderItemSimpleResponse> allSubscriptions1 = orderQueryService.findAllSubscriptions(
            user.getId(), orderPageRequest1);
        List<OrderItemSimpleResponse> allSubscriptions2 = orderQueryService.findAllSubscriptions(
            user.getId(), orderPageRequest2);

        //then
        assertThat(allSubscriptions1).hasSize(7)
            .extracting("item.title")
            .as("page 1, size 7, offset 0, 내림차순")
            .containsExactly(
                "sample23",
                "sample22",
                "sample21",
                "sample20",
                "sample19",
                "sample18",
                "sample17"
            );

        assertThat(allSubscriptions2).hasSize(10)
            .extracting("item.title")
            .as("page 2, size 10, offset 10, 내림차순")
            .containsExactly(
                "sample13",
                "sample12",
                "sample11",
                "sample10",
                "sample9",
                "sample8",
                "sample7",
                "sample6",
                "sample5",
                "sample4"
            );

    }


    private User getUser() {
        User userSample = fixtureMonkey.giveMeBuilder(User.class)
            .set("userId", null)
            .set("wishList", new ArrayList<>())
            .set("displayName", "test")
            .set("cart", null)
            .sample();

        return userRepository.save(userSample);
    }

    private List<Item> getItems() {
        AtomicReference<Integer> value = new AtomicReference<>(1);
        List<Item> items = fixtureMonkey.giveMeBuilder(Item.class)
            .setLazy("title", () -> "sample" + value.getAndSet(value.get() + 1))
            .set("itemId", null)
            .set("wishList", new ArrayList<>())
            .set("categories", new ArrayList<>())
            .set("reviews", new ArrayList<>())
            .set("talks", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sampleList(23);

        return itemRepository.saveAll(items);
    }
}