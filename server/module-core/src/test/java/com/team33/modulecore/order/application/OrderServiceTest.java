package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.dto.OrderItemServiceDto;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;


class OrderServiceTest extends OrderDomainHelper {

    private Item item;
    private User user;

    @BeforeEach
    void setUpEach() {
         item = getItem();
         user = getUser();
    }

    @Transactional
    @DisplayName("단건 order 객체를 생성할 수 있다.")
    @Test
    void callOrderTest() throws Exception {
        //given
        List<OrderItem> orderItemSingle = getOrderItems(item);

        //when
        Order order = orderService.callOrder(orderItemSingle, false, user.getId());
        OrderPrice orderPrice = new OrderPrice(orderItemSingle);

        //then
        assertThat(order.getOrderPrice()).isEqualTo(orderPrice);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REQUEST);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalItems()).isEqualTo(1);
        assertThat(order.getUser()).isEqualTo(user);
    }

    @Transactional
    @DisplayName("정기 구독 아이템의 수량을 조정할 수 있다.")
    @Test
    void 정기_구독_수량_조정() throws Exception {
        //given
        OrderItem sampleOrderItem = fixtureMonkey.giveMeBuilder(OrderItem.class)
            .set("id", null)
            .set("item", item)
            .set("quantity", 3)
            .sample();

        Order order = orderService.callOrder(List.of(sampleOrderItem), true, user.getId());

        //when
        orderService.changeSubscriptionItemQuantity(1L, 1L, 2);

        //then
        assertThat(order.getOrderItems()).hasSize(1)
            .extracting("quantity")
            .containsExactly(2);
    }

    @Transactional
    @DisplayName("주문을 취소할 수 있다.")
    @Test
    void 주문_취소() throws Exception {
        //given
        OrderItem sampleOrderItem = fixtureMonkey.giveMeBuilder(OrderItem.class)
            .set("id", null)
            .set("item", item)
            .set("quantity", 3)
            .sample();

        Order order = orderService.callOrder(List.of(sampleOrderItem), true, user.getId());

        //when
        orderService.cancelOrder(order.getId());

        //then
        assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.CANCEL);
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
        var dto = OrderItemServiceDto.builder()
            .isSubscription(false)
            .itemId(item.getId())
            .period(30)
            .quantity(3)
            .build();

        return orderItemService.getOrderItemSingle(dto);
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