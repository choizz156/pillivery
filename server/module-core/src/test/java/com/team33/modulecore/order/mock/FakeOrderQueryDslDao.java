package com.team33.modulecore.order.mock;

import com.team33.modulecore.MockEntityFactory;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class FakeOrderQueryDslDao implements OrderQueryRepository {

    private Map<Long, Order> orders;

    public FakeOrderQueryDslDao() {
        this.orders = new HashMap<>();
        MockEntityFactory mockEntityFactory = MockEntityFactory.of();
        List<Order> mockOrders = mockEntityFactory.getMockOrders();
        mockOrders.forEach(order -> orders.put(order.getId(), order));
    }

    @Override
    public Page<Order> searchOrders(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    ) {

        List<Order> orderList = new ArrayList<>();

        for (Map.Entry<Long, Order> entry : orders.entrySet()) {
            orderList.add(entry.getValue());
        }

        return new PageImpl<>(
            orderList.size() < pageRequest.getSize()
                ? orderList
                : orderList.subList(0, pageRequest.getSize()),
            PageRequest.of(pageRequest.getPage() - 1, pageRequest.getSize()),
            orderList.size()
        );
    }

    @Override
    public List<OrderItem> findSubscriptionOrderItem(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    ) {
        return orders.values().stream()
            .filter(order -> order.getOrderStatus() == OrderStatus.SUBSCRIBE)
            .map(Order::getOrderItems)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @Override
    public Order findById(Long id) {
        return null;
    }
}
