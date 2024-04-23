package com.team33.modulecore.order.application;

import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.order.repository.OrderRepository;
import com.team33.modulecore.orderitem.dto.OrderItemSimpleResponse;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserFindHelper userFindHelper;

    public Page<Order> findAllOrders(Long userId, OrderPageRequest orderPageRequest) {
        User user = userFindHelper.findUser(userId);
        OrderFindCondition orderFindCondition =
            OrderFindCondition.to(user, OrderStatus.ORDER_REQUEST);

        return orderRepository.searchOrders(
            orderPageRequest,
            orderFindCondition
        );
    }

    public List<OrderItemSimpleResponse> findAllSubscriptions(
        Long userId,
        OrderPageRequest orderPageRequest
    ) {
        User user = userFindHelper.findUser(userId);
        List<Order> subscriptionOrder = orderRepository.findSubscriptionOrder(
            orderPageRequest,
            OrderFindCondition.to(user, OrderStatus.ORDER_SUBSCRIBE)
        );
        return getSubscriptionItem(subscriptionOrder);
    }

    private List<OrderItemSimpleResponse> getSubscriptionItem(List<Order> subscriptionOrder) {
        return subscriptionOrder.stream()
            .map(Order::getOrderItems)
            .flatMap(List::stream)
            .map(OrderItemSimpleResponse::of)
            .collect(Collectors.toList());
    }
}
