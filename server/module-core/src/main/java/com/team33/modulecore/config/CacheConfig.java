package com.team33.modulecore.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.team33.modulecore.core.cart.domain.CartVO;
import com.team33.modulecore.core.cart.event.CartSavedEvent;

@EnableCaching
@Configuration
public class CacheConfig {

	private final CacheManager cacheManager;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	CacheConfig(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Bean
	public CacheManager cacheManager() {

		CaffeineCacheManager cacheManager = new CaffeineCacheManager();

		cacheManager.registerCustomCache("mainItems", Caffeine.newBuilder()
				.initialCapacity(10)
				.maximumSize(100)
				.expireAfterWrite(7, TimeUnit.DAYS)
				.recordStats()
				.build());

		cacheManager.registerCustomCache("categoryItems", Caffeine.newBuilder()
				.initialCapacity(50)
				.maximumSize(100)
				.expireAfterWrite(3, TimeUnit.DAYS)
				.recordStats()
				.build());

		cacheManager.registerCustomCache("cart", Caffeine.newBuilder()
				.initialCapacity(100)
				.maximumSize(1000)
				.expireAfterWrite(4, TimeUnit.HOURS)
				.removalListener((key, value, cause) -> {
					if(cause.wasEvicted()){
						CartVO cartVO = (CartVO) value;
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
