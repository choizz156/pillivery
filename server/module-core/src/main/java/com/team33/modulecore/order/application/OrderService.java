package com.team33.modulecore.order.application;

import static com.team33.modulecore.order.domain.OrderStatus.CANCEL;

import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserRepository userRepository;
    private final UserFindHelper userFindHelper;
    private final OrderQueryService orderQueryService;

    public Order callOrder(List<OrderItem> orderItems, boolean subscription, long userId) {
        Order order = createOrder(orderItems, subscription, userId);

       return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {
        Order findOrder = orderQueryService.findOrder(orderId);

        findOrder.changeOrderStatus(CANCEL);

        findOrder.getOrderItems()
                .forEach(orderItem -> orderItem.getItem().minusSales(orderItem.getQuantity()));
    }

    public void completeOrder(Long orderId) {
        Order order = orderQueryService.findOrder(orderId);

        order.changeOrderStatus(OrderStatus.COMPLETE);
    }

    public void subscribeOrder(Long orderId) {
        Order order = orderQueryService.findOrder(orderId);

        order.changeOrderStatus(OrderStatus.SUBSCRIBE);
    }

    public Order deepCopy(Order order) {
        Order newOrder = new Order(order);

        orderRepository.save(newOrder);

        return newOrder;
    }

    public void changeSubscriptionItemQuantity(long orderId, long orderItemId, int quantity) {
        Order order = orderQueryService.findOrder(orderId);

        order.getOrderItems().stream()
            .filter(orderItem -> orderItem.getId() == orderItemId)
            .findFirst()
            .ifPresentOrElse(
                orderItem -> orderItem.changeQuantity(quantity),
                () -> {
                    throw new BusinessLogicException(ExceptionCode.NOT_ORDERED_ITEM);
                }
            );
    }

//    private void plusSalesOfItem(OrderItem oi) {
//        oi.getItem().plusSales(oi.getItem().getSales() + oi.getQuantity());
//    }
    //Todo: 주문 완료되면 판매량을 올려야함.

    private Order createOrder(List<OrderItem> orderItems, boolean subscription, long userId) {
        User user = userFindHelper.findUser(userId);
        return Order.create(orderItems, subscription, user);
    }
}
