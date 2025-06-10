package com.team33.modulecore.config;

import static com.team33.modulecore.cache.CacheType.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.team33.modulecore.cache.CategoryItemsExpiry;
import com.team33.modulecore.cache.ItemCacheManager;
import com.team33.modulecore.core.cart.event.CartSavedEvent;
import com.team33.modulecore.core.cart.vo.CartVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private ItemCacheManager itemCacheManager;

	@Bean
	public CacheManager cacheManager() {

		CaffeineCacheManager cacheManager = new CaffeineCacheManager();


		cacheManager.registerCustomCache(MAIN_ITEMS.name(), Caffeine.newBuilder()
			.initialCapacity(10)
			.maximumSize(10)
			.refreshAfterWrite(Duration.ofDays(7))
			.expireAfterWrite(Duration.ofDays(7).plusSeconds(20))
			.recordStats()
			.build(itemCacheManager::mainCategoryItems));

		cacheManager.registerCustomCache(CATEGORY_ITEMS.name(), Caffeine.newBuilder()
			.initialCapacity(50)
			.maximumSize(100)
			.expireAfter(new CategoryItemsExpiry())
			.refreshAfterWrite(7, TimeUnit.DAYS)
			.recordStats()
			.build(itemCacheManager::loadCategoryItems)
		);

		cacheManager.registerCustomCache(CARTS.name(), Caffeine.newBuilder()
			.initialCapacity(100)
			.maximumSize(10000)
			.expireAfterWrite(4, TimeUnit.HOURS)
			.removalListener((key, value, cause) -> {
				if (cause.wasEvicted()) {
					CartVO cartVO = (CartVO)value;
					long cartId = cartVO.getId();
					eventPublisher.publishEvent(new CartSavedEvent(cartId, cartVO));
				}
			})
			.recordStats()
			.build());

		cacheManager.setCaffeine(Caffeine.newBuilder()
			.initialCapacity(100)
			.maximumSize(1000)
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.recordStats());

		return cacheManager;
	}

}
