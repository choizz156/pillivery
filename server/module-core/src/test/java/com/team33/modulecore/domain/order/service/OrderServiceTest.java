package com.team33.modulecore.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.entity.OrderStatus;
import com.team33.modulecore.domain.order.value.OrderItemInfo;
import com.team33.modulecore.domain.order.value.Price;
import com.team33.modulecore.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;



class OrderServiceTest extends ServiceTest {

    @Transactional
    @DisplayName("단건 order 객체를 생성할 수 있다.")
    @Test
    void callOrderTest() throws Exception {
        //given
        Item item = getItem();
        List<OrderItem> orderItemSingle = getOrderItems(item);
        User user = getUser();

        //when
        Order order = orderService.callOrder(orderItemSingle, false, user.getId());
        Price price = new Price(orderItemSingle);

        //then
        assertThat(order.getPrice()).isEqualTo(price);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDER_COMPLETE);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalItem()).isEqualTo(1);
        assertThat(order.getUser()).isEqualTo(user);
        assertThat(order.getAddress()).isEqualTo(user.getAddress());
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

    private List<OrderItem> getOrderItems(Item item) {
        OrderItemInfo orderItemInfo = OrderItemInfo.of(1, false, 30);
        return orderItemService.getOrderItemSingle(item.getItemId(), orderItemInfo);
    }


    private Item getItem() {
        Item sampleItem = fixtureMonkey.giveMeBuilder(Item.class)
            .set("itemId", null)
            .set("wishList", new ArrayList<>())
            .set("categories", new ArrayList<>())
            .set("reviews", new ArrayList<>())
            .set("talks", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sample();

        return itemRepository.save(sampleItem);
    }
}