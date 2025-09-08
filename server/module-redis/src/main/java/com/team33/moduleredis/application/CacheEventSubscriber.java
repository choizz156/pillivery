package com.team33.moduleredis.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleredis.dto.CachedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheEventSubscriber {

	private final CacheManager cacheManager;
	private final ObjectMapper objectMapper;
	private final HttpMessageConverters messageConverters;

	public void handleCachedEvent(String msg) throws JsonProcessingException {
		CachedEvent cachedEvent = objectMapper.readValue(msg, CachedEvent.class);
		Cache cache = cacheManager.getCache(cachedEvent.getCacheName());

		if(cache != null) {
			cache.clear();
			log.info("Cache cache evicted. event = {}", cachedEvent);
			return;
		}
		log.warn("Cache not found. event = {}", cachedEvent);
	}
}
