package com.team33.modulecore.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.domain.cart.entity.Cart;
import com.team33.modulecore.domain.cart.entity.ItemCart;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.value.OrderItemInfo;
import com.team33.modulecore.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;


@Transactional
class OrderItemServiceTest extends ServiceTest {

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

    @Rollback(value = false)
    @DisplayName("장바구니에 있는 아이템을 OrderItem 객체를 만들 수 있다.")
    @Test
    void createOrderItemCart() throws Exception {
        //given
        Item item1 = getItem("title1");
        Item item2 = getItem("title2");
        User user = getUser();

        Cart cartSample = Cart.createCart(user);
        Cart cart = cartRepository.save(cartSample);

        OrderItemInfo orderItemInfo = OrderItemInfo.of(1, false, 30);
        ItemCart itemCart1 = ItemCart.builder()
            .cart(cart)
            .item(item1)
            .buyNow(true)
            .orderItemInfo(orderItemInfo)
            .build();

        ItemCart itemCart2 = ItemCart.builder()
            .cart(cart)
            .item(item2)
            .buyNow(true)
            .orderItemInfo(orderItemInfo)
            .build();

        List<ItemCart> itemCartsSample = List.of(itemCart1, itemCart2);

        List<ItemCart> itemCarts = itemCartRepository.saveAll(itemCartsSample);
        //when
        List<OrderItem> orderItemsInCart = orderItemService.getOrderItemsInCart(itemCarts);

        //then
        assertThat(orderItemsInCart).hasSize(2);
        assertThat(itemCartRepository.count()).isEqualTo(0);
        OrderItem orderItem1 = orderItemsInCart.get(0);
        assertThat(orderItem1.getItem().getItemId()).isEqualTo(1L);
        assertThat(orderItem1.getItem().getTitle()).isEqualTo("title1");
        assertThat(orderItem1.getOrderItemInfo()).isEqualTo(orderItemInfo);
        assertThat(orderItem1.getQuantity()).isEqualTo(1);
        assertThat(orderItem1.getPeriod()).isEqualTo(30);
        assertThat(orderItem1.isSubscription()).isFalse();

        OrderItem orderItem2 = orderItemsInCart.get(1);
        assertThat(orderItem2.getItem().getItemId()).isEqualTo(2L);
        assertThat(orderItem2.getItem().getTitle()).isEqualTo("title2");
        assertThat(orderItem2.getOrderItemInfo()).isEqualTo(orderItemInfo);
        assertThat(orderItem2.getQuantity()).isEqualTo(1);
        assertThat(orderItem2.getPeriod()).isEqualTo(30);
        assertThat(orderItem2.isSubscription()).isFalse();
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


    private Item getItem(String name) {
        Item sampleItem = fixtureMonkey.giveMeBuilder(Item.class)
            .set("itemId", null)
            .set("title",name)
            .set("wishList", new ArrayList<>())
            .set("categories", new ArrayList<>())
            .set("reviews", new ArrayList<>())
            .set("talks", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sample();

        return itemRepository.save(sampleItem);
    }
}