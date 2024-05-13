package com.team33.modulecore.cart.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.domain.repository.CartRepository;

public class FakeCartRepository implements CartRepository {

	private Map<Long, Cart> store = new HashMap<>();
	@Override
	public void save(Cart cart) {
		store.put(cart.getId(), cart);
	}

	@Override
	public Optional<Cart> findById(long id) {
		return Optional.ofNullable(store.get(id));
	}

	public void clear() {
		store.clear();
	}
}
