package com.team33.modulecore.config.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Profile("test || local")
@Slf4j
@Configuration
public class EmbededRedisConfig {

	private final int defaultRedisPort = 6379;

	private final String host = "localhost";

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

	/**
	 * Embedded Redis가 현재 실행중인지 확인
	 */
	private boolean isRedisRunning() throws IOException {
		return isRunning(executeGrepProcessCommand(defaultRedisPort));
	}

	/**
	 * 현재 PC/서버에서 사용가능한 포트 조회
	 */
	public int findAvailablePort() throws IOException {
		for (int port = 10000; port <= 65535; port++) {
			Process process = executeGrepProcessCommand(port);
			if (!isRunning(process)) {
				return port;
			}
		}

		throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
	}

	/**
	 * 해당 port를 사용중인 프로세스 확인하는 sh 실행
	 */
	private Process executeGrepProcessCommand(int port) throws IOException {
		String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
		String[] shell = {"/bin/sh", "-c", command};
		return Runtime.getRuntime().exec(shell);
	}

	/**
	 * 해당 Process가 현재 실행중인지 확인
	 */
	private boolean isRunning(Process process) {
		String line;
		StringBuilder pidInfo = new StringBuilder();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

			while ((line = input.readLine()) != null) {
				pidInfo.append(line);
			}

		} catch (Exception e) {
		}

		return !StringUtils.isEmpty(pidInfo.toString());
	}
}