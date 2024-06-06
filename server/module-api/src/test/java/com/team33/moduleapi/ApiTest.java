package com.team33.moduleapi;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;

@ActiveProfiles({"auth", "test"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

	@LocalServerPort
	private int port;

	// @Autowired
	// protected UserFindHelper userFindHelper;
	//
	// @Autowired
	// protected UserRepository userRepository;
	//
	// @Autowired
	// protected JwtTokenProvider jwtTokenProvider;

	@Autowired
	private DataCleaner dataCleaner;

	@BeforeEach
	void beforeEach() throws Exception {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
			dataCleaner.afterPropertiesSet();
		}
		dataCleaner.execute();
	}

	// @AfterEach
	// void tearDown() {
	//     SecurityContextHolder.clearContext();
	//     userRepository.deleteAll();
	// }

	// protected String getToken() {
	//     User loginUser = userFindHelper.findUser(1L);
	//     return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
	// }

}