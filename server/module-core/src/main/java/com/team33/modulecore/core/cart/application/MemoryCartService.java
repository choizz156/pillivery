package com.team33.modulecore.core.cart.application;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.Cart;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemoryCartService {

	private final RedissonClient redissonClient;

	public Cart getCart(String key) {
		RMapCache<String, Cart> mapCache = redissonClient.getMapCache("cart");
		return mapCache.get(key);
	}

	public void saveCart(String key, Cart cart) {
		redissonClient.getMapCache("cart").put(key, cart);
	}

}
