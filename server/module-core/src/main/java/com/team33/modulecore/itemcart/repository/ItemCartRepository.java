package com.team33.modulecore.itemcart.repository;

import com.team33.modulecore.item.domain.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.cart.domain.Cart;
import com.team33.modulecore.itemcart.domain.ItemCart;
import org.springframework.data.jpa.repository.Query;

public interface ItemCartRepository extends JpaRepository<ItemCart, Long> {

    ItemCart findByCartAndItemAndSubscriptionItemInfoIsSubscription(Cart cart, Item item, boolean subscription);

    List<ItemCart> findAllByCartAndSubscriptionItemInfoIsSubscription(Cart cart, boolean subscription);

    List<ItemCart> findAllByCartAndSubscriptionItemInfoIsSubscriptionAndBuyNow(Cart cart, boolean subscription, boolean buyNow);

    void deleteByItemAndCart(Item item, Cart cart); // 주문시 장바구니에서 아이템 삭제

    @Query("select count(i) from ItemCart i")
    long countFirstBy();
}
