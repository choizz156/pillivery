package com.team33.modulecore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Profile("!test")
@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory() {

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

		config.setPort(port);
		config.setHostName(host);

		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<?,?> redisTemplate() {
		RedisTemplate<?,?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return redisTemplate;
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer() {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(lettuceConnectionFactory());
		return container;
	}
}
