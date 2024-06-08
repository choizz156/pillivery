package com.team33.modulecore.order.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderCommandRepository;

public class FakeOrderCommandRepository implements OrderCommandRepository {

	private Map<Long, Order> store;
	private EntityManager em;

	public FakeOrderCommandRepository(EntityManager em) {
		this.em = em;
	}

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
