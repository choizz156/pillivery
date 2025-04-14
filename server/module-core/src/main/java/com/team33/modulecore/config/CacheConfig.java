package com.team33.modulecore.config;

import static com.team33.modulecore.cache.CacheType.*;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.team33.modulecore.core.cart.vo.CartVO;
import com.team33.modulecore.core.cart.event.CartSavedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Bean
	public CacheManager cacheManager() {

		CaffeineCacheManager cacheManager = new CaffeineCacheManager();

		cacheManager.registerCustomCache(MAIN_ITEMS.name(), Caffeine.newBuilder()
			.initialCapacity(10)
			.maximumSize(100)
			.expireAfterWrite(7, TimeUnit.DAYS)
			.recordStats()
			.build());

		cacheManager.registerCustomCache(CATEGORY_ITEMS.name(), Caffeine.newBuilder()
			.initialCapacity(50)
			.maximumSize(100)
			.expireAfterWrite(3, TimeUnit.DAYS)
			.recordStats()
			.build());

		cacheManager.registerCustomCache(CARTS.name(), Caffeine.newBuilder()
			.initialCapacity(100)
			.maximumSize(1000)
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
