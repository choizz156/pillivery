package com.team33.modulecore.order.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;

public class FakeOrderRepository implements OrderRepository {

    private final Map<Long, Order> store;

    public FakeOrderRepository() {
        this.store = new HashMap<>();
    }

    public Order save(Order order) {
        store.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return store.containsKey(id) ? Optional.of(store.get(id)) : Optional.empty();
    }

    @Override
    public void delete(Order entity) {
        store.remove(entity.getId());
    }

    @Override
    public boolean findIsSubscriptionById(Long orderId) {
        return false;
    }
}
