package com.team33.modulecore.core.cart.application;

import static com.team33.modulecore.cache.CacheType.*;

import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import com.team33.modulecore.cache.CacheClient;
import com.team33.modulecore.core.cart.vo.CartVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CartCacheManager {

	private final CacheClient cacheClient;

	public <T extends CartVO> Optional<T> getCart(String key, Class<T> cartType) {
		if (key == null || cartType == null) {
			return Optional.empty();
		}
		Cache cache = cacheClient.getCache(CARTS.name());
		return Optional.ofNullable(cache.get(key, cartType));
	}

	public void saveCart(String key, CartVO cart) {
		Cache cache = cacheClient.getCache(CARTS.name());
		cache.put(key, cart);
	}

}
