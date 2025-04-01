package com.team33.moduleredis.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Slf4j
@Profile("test || local")
@Configuration
public class EmbededRedisConfig {

	private static final String REDISSON_HOST_PREFIX = "redis://";
	private final int defaultRedisPort = 6379;
	private final String host = "localhost";
	private RedisServer redisServer;

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

		int port = isRedisRunning() ? findAvailablePort() : defaultRedisPort;
		redisServer = new RedisServer(port);
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() throws IOException {

		if (redisServer != null) {
			redisServer.stop();
		}
	}

	public int findAvailablePort() throws IOException {

		for (int port = 10000; port <= 65535; port++) {
			Process process = executeGrepProcessCommand(port);
			if (!isRunning(process)) {
				return port;
			}
		}

		throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
	}

	private boolean isRedisRunning() throws IOException {

		return isRunning(executeGrepProcessCommand(defaultRedisPort));
	}

	private Process executeGrepProcessCommand(int port) throws IOException {

		String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
		String[] shell = {"/bin/sh", "-c", command};
		return Runtime.getRuntime().exec(shell);
	}

	private boolean isRunning(Process process) {

		String line;
		StringBuilder pidInfo = new StringBuilder();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

			while ((line = input.readLine()) != null) {
				pidInfo.append(line);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return StringUtils.hasLength(pidInfo.toString());
	}
}