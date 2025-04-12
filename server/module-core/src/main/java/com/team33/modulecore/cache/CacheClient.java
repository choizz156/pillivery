package com.team33.modulecore.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CacheClient {

	private final CacheManager cacheManager;

	public Cache getCache(String cacheName) {

		Cache cache = cacheManager.getCache(cacheName);

		if(cache == null) {
			throw new RuntimeException("Cache not found: " + cacheName);
		}

		return cache;
	}

}
