package com.team33.moduleredis.application;

import com.team33.moduleredis.dto.CachedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheEventPublisher {

	public static final String CACHE_INVALIDATION_TOPIC = "cache-invalidation-topic";

	private final RedisTemplate<String, Object> redisTemplate;

	public void publish(String cacheName, String cacheKey) {
		CachedEvent event = new CachedEvent(cacheName, cacheKey);
		log.info("Publishing cache invalidation event. event = {}", event);
		redisTemplate.convertAndSend(CACHE_INVALIDATION_TOPIC, event);
	}
}
