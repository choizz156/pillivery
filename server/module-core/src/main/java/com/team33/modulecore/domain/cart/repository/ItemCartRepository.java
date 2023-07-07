package com.team33.modulecore.domain.cart.repository;

import com.team33.modulecore.domain.item.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.domain.cart.entity.Cart;
import com.team33.modulecore.domain.cart.entity.ItemCart;

public interface ItemCartRepository extends JpaRepository<ItemCart, Long> {

    ItemCart findByCartAndItemAndSubscription(Cart cart, Item item, boolean subscription);

    List<ItemCart> findAllByCartAndSubscription(Cart cart, boolean subscription);

    List<ItemCart> findAllByCartAndSubscriptionAndBuyNow(Cart cart, boolean subscription, boolean buyNow);

    void deleteByItemAndCart(Item item, Cart cart); // 주문시 장바구니에서 아이템 삭제
}
