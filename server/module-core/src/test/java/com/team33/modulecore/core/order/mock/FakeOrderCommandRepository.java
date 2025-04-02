package com.team33.modulecore.core.order.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;

public class FakeOrderCommandRepository implements OrderCommandRepository {

	private Map<Long, Order> store;

	public FakeOrderCommandRepository() {
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

}
