package com.team33.modulecore.order.domain.repository;

import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//TODO: 리팩토링으로 없애야됨.
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select io from OrderItem io join orders o on io.order.id = o.id "
        + "and o.orderStatus = :orderStatus and o.user.id = :id and io.subscriptionItemInfo.isSubscription = true")
    Page<OrderItem> findAllSubs(Pageable pageable, OrderStatus orderStatus, long id);
}
