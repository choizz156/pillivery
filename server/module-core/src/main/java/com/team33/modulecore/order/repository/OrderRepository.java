package com.team33.modulecore.order.repository;


import com.team33.modulecore.order.domain.Order;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.user.domain.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserAndSubscriptionAndOrderStatusNot(
            Pageable pageable, User user, boolean subscription, OrderStatus orderStatus1);

    Page<Order> findAllByUserAndSubscriptionAndOrderStatusNotAndOrderStatusNot(
            Pageable pageable, User user, boolean subscription, OrderStatus orderStatus1, OrderStatus orderStatus2
    );

    @Query("Select distinct o from orders o join OrderItem io on o.orderId = io.order.orderId " +
            "where io.item.itemId = :itemId and o.user.id = :userId and o.orderStatus not in :status")
    List<Order> findByItemAndUser(@Param("itemId") long itemId, @Param("userId") long userId, @Param("status") OrderStatus status);

    Page<Order> findAllByUserAndOrderStatus(Pageable pageable, User user, OrderStatus orderStatus);
}
