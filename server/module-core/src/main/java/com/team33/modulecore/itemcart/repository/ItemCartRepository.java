package com.team33.modulecore.itemcart.repository;

import com.team33.modulecore.cart.domain.entity.NormalCart;
import com.team33.modulecore.item.domain.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team33.modulecore.cart.domain.NormalCartItem;

import org.springframework.data.jpa.repository.Query;

public interface ItemCartRepository extends JpaRepository<NormalCartItem, Long> {

    NormalCartItem findByCartAndItemAndSubscriptionItemInfoIsSubscription(NormalCart normalCart, Item item, boolean subscription);

    List<NormalCartItem> findAllByCartAndSubscriptionItemInfoIsSubscription(NormalCart normalCart, boolean subscription);

    List<NormalCartItem> findAllByCartAndSubscriptionItemInfoIsSubscriptionAndBuyNow(NormalCart normalCart, boolean subscription, boolean buyNow);

    void deleteByItemAndCart(Item item, NormalCart normalCart); // 주문시 장바구니에서 아이템 삭제

    @Query("select count(i) from NormalCartItem i")
    long countFirstBy();
}
