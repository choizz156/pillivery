package com.team33.modulecore.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.value.OrderItemInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;



class OrderItemServiceTest extends ServiceTest {

    @Transactional
    @DisplayName("orderItem을 1개 생성할 수 있다.")
    @Test
    void createOrderItem() throws Exception {
        //given
        Item sampleItem = fixtureMonkey.giveMeBuilder(Item.class)
            .set("itemId", null)
            .set("wishList", new ArrayList<>())
            .set("categories", new ArrayList<>())
            .set("reviews", new ArrayList<>())
            .set("talks", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sample();

        Item item = itemRepository.save(sampleItem);
        OrderItemInfo orderItemInfo = OrderItemInfo.of(1, false, 30);
        //when
        List<OrderItem> orderItemSingle =
            orderItemService.getOrderItemSingle(item.getItemId(), orderItemInfo);
        //then
        OrderItem orderItem = orderItemSingle.get(0);
        assertThat(orderItemSingle).hasSize(1);
        assertThat(orderItem.getItem().getItemId()).isEqualTo(1L);
        assertThat(orderItem.getOrderItemInfo()).isEqualTo(orderItemInfo);
        assertThat(orderItem.getQuantity()).isEqualTo(1);
        assertThat(orderItem.getPeriod()).isEqualTo(30);
        assertThat(orderItem.isSubscription()).isFalse();
    }

}