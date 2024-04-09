package com.team33.modulecore.domain.order.service;

import com.team33.modulecore.domain.order.dto.OrderDto.Post;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.entity.OrderStatus;
import com.team33.modulecore.domain.order.repository.OrderItemRepository;
import com.team33.modulecore.domain.order.repository.OrderRepository;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.repository.UserRepository;
import com.team33.modulecore.global.exception.BusinessLogicException;
import com.team33.modulecore.global.exception.ExceptionCode;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemService orderItemService;
    private final UserRepository userRepository;

    public Order callOrder(List<OrderItem> orderItems, User user) {
        Order order = new Order();
//        order.setItemOrders(orderItems);
//        order.setName(user.getRealName());
//        order.setAddress(user.getAddress().getCity());
//        order.setDetailAddress(user.getAddress().getDetailAddress());
//        order.setPhone(user.getPhone());
//        order.setSubscription(orderItems.get(0).isSubscription());
//        order.setTotalItems(orderItems.size());
//        order.setTotalPrice(orderItemService.countTotalPrice(orderItems));
//        order.setTotalDiscountPrice(orderItemService.countDiscountTotalPrice(orderItems));
//        order.setExpectPrice(order.getTotalPrice() - order.getTotalDiscountPrice());
//        order.setUser(user);
//        order.setOrderStatus(OrderStatus.ORDER_REQUEST);
//        order.setTotalQuantity(orderItemService.countQuantity(orderItems));
//
//        for (OrderItem orderItem : orderItems) {
//            orderItem.setOrder(order);
//            orderItemService.plusSales(orderItem); // 판매량 누적
//            orderItemRepository.save(orderItem);
//        }
//
//        orderRepository.save(order);
        return order;
    }

    public Order callOrder(List<OrderItem> orderItems, Post dto, long userId) {
        Order order = createOrder(orderItems, dto, userId);
        orderRepository.save(order);
        return order;
    }


    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public void cancelOrder(long orderId) {
        Order findOrder = findOrder(orderId);
        findOrder.setOrderStatus(OrderStatus.ORDER_CANCEL);
        orderItemService.minusSales(findOrder.getOrderItems()); // 주문 취소 -> 판매량 집계에서 제외
        orderRepository.save(findOrder);
    }

    public Order findOrder(long orderId) {
        return findVerifiedOrder(orderId);
    }

    public Order findVerifiedOrder(long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        return optionalOrder.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }


    public Page<Order> findOrders(User user, int page, boolean subscription) {
        if (subscription) {
            Page<Order> findAllOrder = orderRepository.findAllByUserAndSubscriptionAndOrderStatusNot(
                PageRequest.of(page, 7, Sort.by("orderId").descending()),
                user, true, OrderStatus.ORDER_REQUEST);

            return findAllOrder;
        }
        Page<Order> findAllOrder = orderRepository.findAllByUserAndSubscriptionAndOrderStatusNotAndOrderStatusNot(
            PageRequest.of(page, 7, Sort.by("orderId").descending()),
            user, false, OrderStatus.ORDER_REQUEST, OrderStatus.ORDER_SUBSCRIBE);

        return findAllOrder;
    }

    public Page<Order> findSubs(User user, int page) {
        Page<Order> findAllSubs = orderRepository.findAllByUserAndOrderStatus(
            PageRequest.of(page, 6, Sort.by("orderId").descending()), user,
            OrderStatus.ORDER_SUBSCRIBE);

        return findAllSubs;
    }

    public Page<OrderItem> findAllSubs(User user, int page) {
        Page<OrderItem> findAllSubs = orderItemRepository.findAllSubs(
            PageRequest.of(page, 6, Sort.by("itemOrderId").descending()),
            OrderStatus.ORDER_SUBSCRIBE, user.getUserId());

        return findAllSubs;
    }

    public boolean isShopper(long itemId, long userId) { // 유저의 특정 아이템 구매여부 확인
        List<Order> order = orderRepository.findByItemAndUser(itemId, userId,
            OrderStatus.ORDER_REQUEST);
        if (order.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void completeOrder(Long orderId) {
        Order order = findOrder(orderId);
        order.setOrderStatus(OrderStatus.ORDER_COMPLETE);
    }

    public void subsOrder(Long orderId) {
        Order order = findOrder(orderId);
        order.setOrderStatus(OrderStatus.ORDER_SUBSCRIBE);
    }

    public Order deepCopy(Order order) {
        Order newOrder = new Order(order);
        orderRepository.save(newOrder);
        return newOrder;
    }

    public void creatOrderItem(Order order) {
        order.getOrderItems().forEach(oi -> {
            saveOrderItem(order, oi);
        });
    }

    private void saveOrderItem(Order order, OrderItem oi) {
        oi.addOrder(order);
        pulusSalesOfItem(oi);
        orderItemRepository.save(oi);
    }

    private void pulusSalesOfItem(OrderItem oi) {
        oi.getItem().plusSales(oi.getItem().getSales() + oi.getQuantity());
    }

    private Order createOrder(List<OrderItem> orderItems, Post dto, long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        return Order.create(orderItems, dto, user);
    }
}
