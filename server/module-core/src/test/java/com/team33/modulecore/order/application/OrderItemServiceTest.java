package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.cart.domain.Cart;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionItemInfo;
import com.team33.modulecore.order.dto.OrderItemServiceDto;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.HashSet;
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
        var sampleItem = fixtureMonkey.giveMeBuilder(Item.class)
            .set("id", null)
            .set("wishList", new ArrayList<>())
            .set("itemCategories", new HashSet<>())
            .set("reviews", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sample();

        var item = itemCommandRepository.save(sampleItem);

        var dto = OrderItemServiceDto.builder()
            .isSubscription(false)
            .itemId(sampleItem.getId())
            .period(30)
            .quantity(3)
            .build();

        //when
        List<OrderItem> orderItemSingle = orderItemService.getOrderItemSingle(dto);
        System.out.println("orderItemSingle = " + orderItemSingle.get(0).getQuantity());

        //then
        orderItem_생성(orderItemSingle);
    }


    @DisplayName("장바구니에 있는 아이템을 OrderItem 객체를 만들 수 있다.")
    @Test
    void createOrderItemCart() throws Exception {
        //given
        Item item1 = findItem("title1");
        Item item2 = findItem("title2");
        User user = getUser();
        Cart cart = getCart(user);

        ItemCarts result = findItemCarts(cart, item1, item2);

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
        assertThat(orderItem1.getSubscriptionItemInfo()).isEqualTo(result.subscriptionItemInfo);
        assertThat(orderItem1.getQuantity()).isEqualTo(3);
        assertThat(orderItem1.getPeriod()).isEqualTo(30);
        assertThat(orderItem1.isSubscription()).isFalse();
    }

    private void orderItem_생성(List<OrderItem> orderItemSingle) {
        OrderItem orderItem = orderItemSingle.get(0);
        assertThat(orderItemSingle).hasSize(1);
        assertThat(orderItem.getItem().getId()).isNotNull();
        assertThat(orderItem.getSubscriptionItemInfo())
            .isEqualTo(orderItemSingle.get(0).getSubscriptionItemInfo());
        assertThat(orderItem.getQuantity()).isEqualTo(3);
        assertThat(orderItem.getPeriod()).isEqualTo(30);
        assertThat(orderItem.isSubscription()).isFalse();
    }

    private ItemCarts findItemCarts(Cart cart, Item item1, Item item2) {
        SubscriptionItemInfo subscriptionItemInfo = SubscriptionItemInfo.of(false, 30);
        ItemCart itemCart1 = ItemCart.builder()
            .cart(cart)
            .item(item1)
            .buyNow(true)
            .quantity(3)
            .subscriptionItemInfo(subscriptionItemInfo)
            .build();

        ItemCart itemCart2 = ItemCart.builder()
            .cart(cart)
            .item(item2)
            .buyNow(true)
            .quantity(3)
            .subscriptionItemInfo(subscriptionItemInfo)
            .build();

        return new ItemCarts(subscriptionItemInfo, itemCart1, itemCart2);
    }

    private Cart getCart(User user) {
        Cart cartSample = Cart.createCart(user);
        return cartRepository.save(cartSample);
    }

    private User getUser() {
        User userSample = fixtureMonkey.giveMeBuilder(User.class)
            .set("id", null)
            .set("wishList", new ArrayList<>())
            .set("displayName", "test")
            .set("cart", null)
            .sample();

        return userRepository.save(userSample);
    }

    private List<OrderItem> makeOrderItems(Item item) {

        var dto = OrderItemServiceDto.builder()
            .isSubscription(false)
            .itemId(item.getId())
            .period(30)
            .quantity(3)
            .build();

        return orderItemService.getOrderItemSingle(dto);
    }

    private Item findItem(String name) {
        Item sampleItem = fixtureMonkey.giveMeBuilder(Item.class)
            .set("id", null)
            .set("title", name)
            .set("wishList", new ArrayList<>())
            .set("itemCategories", new HashSet<>())
            .set("reviews", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sample();

        return itemCommandRepository.save(sampleItem);
    }

    private static class ItemCarts {

        public final SubscriptionItemInfo subscriptionItemInfo;
        public final ItemCart itemCart1;
        public final ItemCart itemCart2;

        public ItemCarts(SubscriptionItemInfo subscriptionItemInfo, ItemCart itemCart1,
            ItemCart itemCart2) {
            this.subscriptionItemInfo = subscriptionItemInfo;
            this.itemCart1 = itemCart1;
            this.itemCart2 = itemCart2;
        }
    }
}