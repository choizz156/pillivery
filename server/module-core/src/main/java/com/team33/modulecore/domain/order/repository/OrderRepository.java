package com.team33.modulecore.domain.order.repository;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.entity.OrderStatus;
import com.team33.modulecore.domain.user.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserAndSubscriptionAndOrderStatusNot(
            Pageable pageable, User user, boolean subscription, OrderStatus orderStatus1);

    Page<Order> findAllByUserAndSubscriptionAndOrderStatusNotAndOrderStatusNot(
            Pageable pageable, User user, boolean subscription, OrderStatus orderStatus1, OrderStatus orderStatus2
    );

    @Query("Select distinct o from ORDERS o join OrderItem io on o.orderId = io.order.orderId " +
            "where io.item.itemId = :itemId and o.user.userId = :userId and o.orderStatus not in :status")
    List<Order> findByItemAndUser(@Param("itemId") long itemId, @Param("userId") long userId, @Param("status") OrderStatus status);

    Page<Order> findAllByUserAndOrderStatus(Pageable pageable, User user, OrderStatus orderStatus);
}
