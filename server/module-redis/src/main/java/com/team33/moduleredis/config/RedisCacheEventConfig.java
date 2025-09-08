package com.team33.moduleredis.config;

import static com.team33.moduleredis.application.CacheEventPublisher.CACHE_INVALIDATION_TOPIC;

import com.team33.moduleredis.application.CacheEventSubscriber;
import com.team33.moduleredis.dto.CachedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisCacheEventConfig {

	@Bean
	public MessageListenerAdapter listenerAdapter(CacheEventSubscriber subscriber) {

		return new MessageListenerAdapter(subscriber, "handleCacheEvent");
	}

	@Bean
	public RedisMessageListenerContainer redisContainer(
		RedisConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter
	) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(connectionFactory);
		redisMessageListenerContainer.addMessageListener(listenerAdapter, new ChannelTopic(CACHE_INVALIDATION_TOPIC));
		return redisMessageListenerContainer;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(CachedEvent.class));
		return redisTemplate;
	}
}
