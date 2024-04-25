package com.team33.modulecore.order.domain.repository;


import com.team33.modulecore.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
//
//    @Query("Select distinct o from orders o join OrderItem io on o.id = io.order.id " +
//            "where io.item.itemId = :itemId and o.user.id = :userId and o.orderStatus not in :status")
//    List<Order> findByItemAndUser(@Param("itemId") long itemId, @Param("userId") long userId, @Param("status") OrderStatus status);
}
