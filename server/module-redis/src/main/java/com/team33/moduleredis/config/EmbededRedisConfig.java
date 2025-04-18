package com.team33.moduleredis.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

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
import redis.embedded.core.RedisServerBuilder;

@Slf4j
@Profile("test || local")
@Configuration
public class EmbededRedisConfig {

	private static final String REDISSON_HOST_PREFIX = "redis://";
	private final String host = "localhost";
	private int port;
	private RedisServer redisServer;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		config.setCodec(new JsonJacksonCodec(mapper));

		return Redisson.create(config);
	}


	@PostConstruct
	public void redisServer() {
		try {
			int defaultRedisPort = 6380;
			try {
				port = isRedisRunning() ? findAvailablePort() : defaultRedisPort;
			} catch (Exception e) {
				log.warn("Failed to check if Redis is running using system commands. Falling back to socket check. Error: {}", e.getMessage());
				port = isPortInUse(defaultRedisPort) ? findAvailablePortUsingSocket() : defaultRedisPort;
			}


			try {
				redisServer = new RedisServerBuilder()
					.port(port)
					.setting("daemonize no")
					.setting("appendonly no")
					.setting("save \"\"")
					.setting("dbfilename \"\"")
					.setting("stop-writes-on-bgsave-error no")
					.build();


				redisServer.start();
				log.info("Embedded Redis started on port {}", port);
			} catch (Exception e) {
				log.error("Failed to start embedded Redis server. Tests will continue without Redis. Error: {}", e.getMessage());
				// Don't throw exception to allow tests to continue
			}
		} catch (Exception e) {
			log.error("Error during Redis server initialization: {}", e.getMessage());
			// Don't throw exception to allow tests to continue
		}
	}

	@PreDestroy
	public void stopRedis() {
		try {
			if (redisServer != null) {
				redisServer.stop();
				log.info("Embedded Redis stopped");
			}
		} catch (Exception e) {
			log.error("Error stopping Redis server: {}", e.getMessage());
		}
	}

	public int findAvailablePort() throws IOException {
		for (int port = 10000; port <= 65535; port++) {
			try {
				Process process = executeGrepProcessCommand(port);
				if (!isRunning(process)) {
					return port;
				}
			} catch (Exception e) {
				log.warn("Error checking port {}: {}", port, e.getMessage());
			}
		}

		return findAvailablePortUsingSocket();
	}

	private int findAvailablePortUsingSocket() {
		for (int port = 10000; port <= 65535; port++) {
			if (!isPortInUse(port)) {
				return port;
			}
		}
		throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
	}

	private boolean isPortInUse(int port) {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			return false;
		} catch (IOException e) {
			return true;
		}
	}

	private boolean isRedisRunning() throws IOException {
		try {
			return isRunning(executeGrepProcessCommand(port));
		} catch (Exception e) {
			log.warn("Error checking if Redis is running: {}", e.getMessage());
			return false;
		}
	}

	private Process executeGrepProcessCommand(int port) throws IOException {
		try {
			String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
			String[] shell = {"/bin/sh", "-c", command};
			return Runtime.getRuntime().exec(shell);
		} catch (Exception e) {
			log.warn("Failed to execute grep command: {}", e.getMessage());
			throw e;
		}
	}

	private boolean isRunning(Process process) {
		String line;
		StringBuilder pidInfo = new StringBuilder();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			while ((line = input.readLine()) != null) {
				pidInfo.append(line);
			}
		} catch (Exception e) {
			log.warn("Error reading process output: {}", e.getMessage());
			return false;
		}

		return StringUtils.hasLength(pidInfo.toString());
	}
}
