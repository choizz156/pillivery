package com.team33.modulecore.order.mock;

import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeOrderRepository implements OrderRepository {

    private final Map<Long, Order> store;

    public FakeOrderRepository() {
        this.store = new HashMap<>();
    }

    public Order save(Order order) {
        store.put(order.getId(), order);
        System.out.println("order.getId() = " + order.getId());
        System.out.println(store.get(order.getId()));
        return order;
    }

    public Optional<Order> findById(Long id) {
        System.out.println("id = " + id);
        System.out.println(store.get(id));
        return store.containsKey(id) ? Optional.of(store.get(id)) : Optional.empty();
    }

    @Override
    public void delete(Order entity) {
        store.remove(entity.getId());
    }
}
