package com.team33.modulecore.order.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;

public class FakeOrderRepository implements OrderRepository {

	private Map<Long, Order> store;
	private EntityManager em;

	public FakeOrderRepository(EntityManager em) {
		this.em = em;
	}

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
		return em.createQuery("select o.isSubscription from orders o where o.id = :orderId", Boolean.class)
			.setParameter("orderId", orderId)
			.getSingleResult();
	}

	@Override
	public String findTid(Long orderId) {
		return em.createQuery("select o.paymentCode.tid from orders o where o.id = :orderId", String.class)
			.setParameter("orderId", orderId)
			.getSingleResult();
	}
}
