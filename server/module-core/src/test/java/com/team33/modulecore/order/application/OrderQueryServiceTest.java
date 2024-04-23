package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.orderitem.domain.OrderItemInfo;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

class OrderQueryServiceTest extends OrderDomainHelper {

    @Autowired
    OrderQueryService orderQueryService;

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Transactional
    @DisplayName("유저의 전체 주문 목록을 조회할 수 있다.")
    @Test
    void 주문_목록_조회() throws Exception {
        //given
        var user = getUser();
        var items = getItems();
        var orderItemInfos1 = List.of(
            OrderItemInfo.of(3, false, 0),
            OrderItemInfo.of(2, false, 0),
            OrderItemInfo.of(1, false, 0)
        );

        var orderItemInfoSubs = List.of(
            OrderItemInfo.of(3, true,30),
            OrderItemInfo.of(2, true, 30),
            OrderItemInfo.of(1, true, 30 )
        );

        var orderItems = List.of(
            OrderItem.createWithoutOrder(items.get(0), orderItemInfos1.get(0)),
            OrderItem.createWithoutOrder(items.get(1), orderItemInfos1.get(1)),
            OrderItem.createWithoutOrder(items.get(2), orderItemInfos1.get(2))
        );
        var orderItemsSubs = List.of(
            OrderItem.createWithoutOrder(items.get(0), orderItemInfoSubs.get(0)),
            OrderItem.createWithoutOrder(items.get(1), orderItemInfoSubs.get(1)),
            OrderItem.createWithoutOrder(items.get(2), orderItemInfoSubs.get(2))
        );

        Order order = orderService.callOrder(orderItems, false, user.getId());
        Order orderSubs = orderService.callOrder(orderItemsSubs, true, user.getId());
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
        List<Item> items = fixtureMonkey.giveMeBuilder(Item.class)
            .set("itemId", null)
            .set("wishList", new ArrayList<>())
            .set("categories", new ArrayList<>())
            .set("reviews", new ArrayList<>())
            .set("talks", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sampleList(3);

        return itemRepository.saveAll(items);
    }
}