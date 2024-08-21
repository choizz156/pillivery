package com.team33.modulecore.config;

import java.io.IOException;
import java.net.ServerSocket;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Profile("test")
@Slf4j
@Configuration
public class RedisTestConfig {

    @Value("${spring.data.redis.port}")
    private int defaultRedisPort;

    @Value("${spring.data.redis.host}")
    private String host;

    private RedisServer redisServer;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + defaultRedisPort);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        config.setCodec(new JsonJacksonCodec(mapper));

        return Redisson.create(config);
    }

    @PostConstruct
    public void redisServer() throws IOException {
        int redisPort = isRedisRunning(defaultRedisPort) ? findAvailablePort() : defaultRedisPort;
        log.warn("Start Redis server on port = {}", redisPort);
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    private boolean isRedisRunning(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                return port;
            } catch (IOException e) {
            }
        }
        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }
}