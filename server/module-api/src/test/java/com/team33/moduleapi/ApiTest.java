package com.team33.moduleapi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulecore.core.common.UserFindHelper;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;
import com.team33.modulecore.security.infra.JwtTokenProvider;

import io.restassured.RestAssured;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

	@LocalServerPort
	private int port;
	@Autowired
	private DataCleaner dataCleaner;
	@Autowired
	protected UserFindHelper userFindHelper;
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected JwtTokenProvider jwtTokenProvider;
	@Autowired
	protected CacheManager cacheManager;

	@BeforeEach
	void beforeEach() throws Exception {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
			dataCleaner.afterPropertiesSet();
		}
	}

	@AfterEach
	void tearDown() {
		dataCleaner.execute();
		cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
		SecurityContextHolder.clearContext();
	}

	protected String getToken() {
	    User loginUser = userFindHelper.findUser(1L);
	    return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
	}

}