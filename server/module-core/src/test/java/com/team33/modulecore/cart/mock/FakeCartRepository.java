package com.team33.modulecore.cart.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.team33.modulecore.core.cart.domain.entity.Cart;
import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;

public class FakeCartRepository implements CartRepository {

	private Map<Long, Cart> store = new HashMap<>();

	@Override
	public void save(Cart cart) {
		store.put(cart.getId(), cart);
	}

	@Override
	public Optional<Cart> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public Optional<NormalCart> findNormalCartById(Long cartId) {
		return Optional.ofNullable((NormalCart) store.get(cartId));
	}

	@Override
	public Optional<SubscriptionCart> findSubscriptionCartById(Long cartId) {
		return Optional.ofNullable((SubscriptionCart) store.get(cartId));
	}

	@Override
	public void deleteById(Long cartId) {
		store.remove(cartId);
	}
}
