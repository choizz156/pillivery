package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.orderitem.domain.OrderItemInfo;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

class OrderQueryServiceTest extends OrderDomainTest {

    @Autowired
    OrderQueryService orderQueryService;

    @DisplayName("유저의 전체 주문 목록을 조회할 수 있다.")
    @Test
    void 주문_목록_조회() throws Exception {
        //given
        User user = getUser();
        List<Item> items = getItems();
        List<OrderItemInfo> orderItemInfos = List.of(
            OrderItemInfo.of(3, false, 0),
            OrderItemInfo.of(2, false, 0),
            OrderItemInfo.of(1, false, 0)
        );

        List<OrderItem> orderItems = List.of(
            OrderItem.createWithoutOrder(items.get(0), orderItemInfos.get(0)),
            OrderItem.createWithoutOrder(items.get(1), orderItemInfos.get(1)),
            OrderItem.createWithoutOrder(items.get(2), orderItemInfos.get(2))
        );

        Order order = orderService.callOrder(orderItems, false, user.getId());
        orderService.completeOrder(order.getId());

        //when
        Page<Order> allOrders = orderQueryService.findAllOrders(user.getId(), 5, 5, false);
        List<Order> contents = allOrders.getContent();

        //then
        assertThat(contents).hasSize(1);
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