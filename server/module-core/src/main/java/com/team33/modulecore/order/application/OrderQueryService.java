package com.team33.modulecore.order.application;

import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderItemSimpleResponse;
import com.team33.modulecore.order.dto.OrderPageRequest;
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

    private final OrderQueryRepository orderQueryRepository;
    private final UserRepository userRepository;
    private final UserFindHelper userFindHelper;

    public Page<Order> findAllOrders(Long userId, OrderPageRequest orderPageRequest) {
        User user = userFindHelper.findUser(userId);
        OrderFindCondition orderFindCondition =
            OrderFindCondition.to(user, OrderStatus.REQUEST);

        return orderQueryRepository.searchOrders(
            orderPageRequest,
            orderFindCondition
        );
    }

    public List<OrderItemSimpleResponse> findAllSubscriptions(
        Long userId,
        OrderPageRequest orderPageRequest
    ) {
        User user = userFindHelper.findUser(userId);
        List<OrderItem> subscriptionOrder = orderQueryRepository.findSubscriptionOrderItem(
            orderPageRequest,
            OrderFindCondition.to(user, OrderStatus.SUBSCRIBE)
        );
        return getSubscriptionItem(subscriptionOrder);
    }

    public Order findOrder(Long orderId) {
        return orderQueryRepository.findById(orderId);
    }

    private List<OrderItemSimpleResponse> getSubscriptionItem(List<OrderItem> subscriptionOrder) {
        return subscriptionOrder.stream()
            .map(OrderItemSimpleResponse::of)
            .collect(Collectors.toUnmodifiableList());
    }
}
