package com.team33.modulecore.order.application;

import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.repository.OrderRepository;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.orderitem.application.OrderItemService;
import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.orderitem.repository.OrderItemRepository;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemService orderItemService;
    private final UserRepository userRepository;
    private final UserFindHelper userFindHelper;

    public Order callOrder(List<OrderItem> orderItems, boolean subscription, long userId) {
        Order order = createOrder(orderItems, subscription, userId);
        orderRepository.save(order);
        return order;
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

//    public Page<Order> findOrders(User user, int page, boolean subscription) {
//        if (subscription) {
//            return orderRepository.findAllByUserAndSubscriptionAndOrderStatusNot(
//                PageRequest.of(page, 7, Sort.by("orderId").descending()),
//                user, true, OrderStatus.ORDER_REQUEST); //주문요청 빼고 다 가지고 와라 ,취소, 구독, 완료
//        }
//
//        return orderRepository.findAllByUserAndSubscriptionAndOrderStatusNotAndOrderStatusNot(
//            PageRequest.of(page, 7, Sort.by("orderId").descending()),
//            user, false, OrderStatus.ORDER_REQUEST, OrderStatus.ORDER_SUBSCRIBE);
//    }

    public Page<Order> findSubs(User user, int page) {
        Page<Order> findAllSubs = orderRepository.findAllByUserAndOrderStatus(
            PageRequest.of(page, 6, Sort.by("orderId").descending()), user,
            OrderStatus.ORDER_SUBSCRIBE);

        return findAllSubs;
    }

    public Page<OrderItem> findAllSubs(User user, int page) {
        Page<OrderItem> findAllSubs = orderItemRepository.findAllSubs(
            PageRequest.of(page, 6, Sort.by("itemOrderId").descending()),
            OrderStatus.ORDER_SUBSCRIBE, user.getId());

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

    private Order createOrder(List<OrderItem> orderItems, boolean subscription, long userId) {
        User user = userFindHelper.findUser(userId);
        return Order.create(orderItems, subscription, user);
    }
}
