package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.cart.domain.Cart;
import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.orderitem.domain.OrderItemInfo;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OrderItemServiceTest extends OrderDomainHelper {


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
        orderItem_생성(orderItemSingle, orderItemInfo);
    }


    @DisplayName("장바구니에 있는 아이템을 OrderItem 객체를 만들 수 있다.")
    @Test
    void createOrderItemCart() throws Exception {
        //given
        Item item1 = getItem("title1");
        Item item2 = getItem("title2");
        User user = getUser();
        Cart cart = getCart(user);

        ItemCarts result = getItemCarts(cart, item1, item2);

        List<ItemCart> itemCartsSample = List.of(result.itemCart1, result.itemCart2);

        List<ItemCart> itemCarts = itemCartRepository.saveAll(itemCartsSample);
        //when
        List<OrderItem> orderItemsInCart = orderItemService.getOrderItemsInCart(itemCarts);

        //then
        assertThat(orderItemsInCart).hasSize(2);
        assertThat(itemCartRepository.count()).isEqualTo(0);
        orderItem_생성(orderItemsInCart, 0, "title1", result);
        orderItem_생성(orderItemsInCart, 1, "title2", result);
    }

    private void orderItem_생성(
        List<OrderItem> orderItemsInCart,
        int i,
        String title1,
        ItemCarts result
    ) {
        OrderItem orderItem1 = orderItemsInCart.get(i);
        assertThat(orderItem1.getItem().getTitle()).isEqualTo(title1);
        assertThat(orderItem1.getOrderItemInfo()).isEqualTo(result.orderItemInfo);
        assertThat(orderItem1.getQuantity()).isEqualTo(1);
        assertThat(orderItem1.getPeriod()).isEqualTo(30);
        assertThat(orderItem1.isSubscription()).isFalse();
    }

    private void orderItem_생성(List<OrderItem> orderItemSingle, OrderItemInfo orderItemInfo) {
        OrderItem orderItem = orderItemSingle.get(0);
        assertThat(orderItemSingle).hasSize(1);
        assertThat(orderItem.getItem().getItemId()).isNotNull();
        assertThat(orderItem.getOrderItemInfo()).isEqualTo(orderItemInfo);
        assertThat(orderItem.getQuantity()).isEqualTo(1);
        assertThat(orderItem.getPeriod()).isEqualTo(30);
        assertThat(orderItem.isSubscription()).isFalse();
    }

    private ItemCarts getItemCarts(Cart cart, Item item1, Item item2) {
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

        return new ItemCarts(orderItemInfo, itemCart1, itemCart2);
    }

    private Cart getCart(User user) {
        Cart cartSample = Cart.createCart(user);
        return cartRepository.save(cartSample);
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
            .set("title", name)
            .set("wishList", new ArrayList<>())
            .set("categories", new ArrayList<>())
            .set("reviews", new ArrayList<>())
            .set("talks", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sample();

        return itemRepository.save(sampleItem);
    }

    private static class ItemCarts {

        public final OrderItemInfo orderItemInfo;
        public final ItemCart itemCart1;
        public final ItemCart itemCart2;

        public ItemCarts(OrderItemInfo orderItemInfo, ItemCart itemCart1, ItemCart itemCart2) {
            this.orderItemInfo = orderItemInfo;
            this.itemCart1 = itemCart1;
            this.itemCart2 = itemCart2;
        }
    }
}