package com.team33.moduleapi.restdocs;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.team33.moduleapi.DataCleaner;
import com.team33.moduleapi.security.infra.JwtTokenProvider;
import com.team33.modulecore.core.common.UserFindHelper;
import com.team33.modulecore.core.user.application.UserService;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;

@ActiveProfiles({"test", "auth"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public abstract class WebRestDocsSupport {

	@LocalServerPort
	private int port;

	@Autowired
	protected UserService userService;

	@Autowired
	protected UserFindHelper userFindHelper;

	@Autowired
	protected JwtTokenProvider jwtTokenProvider;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected DataCleaner dataCleaner;

	protected MockMvcRequestSpecification webSpec;

	@BeforeEach
	void beforeEach(WebApplicationContext web, RestDocumentationContextProvider restDocumentation) {
		RestAssured.port = port;

		this.webSpec = RestAssuredMockMvc.given()
			.mockMvc(
				MockMvcBuilders.webAppContextSetup(web)
					.apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
						.operationPreprocessors()
						.withRequestDefaults(prettyPrint())
						.withResponseDefaults(prettyPrint())
					)
					.build()
			);
	}

	@AfterEach
	void tearDown() {
		dataCleaner.execute();
		SecurityContextHolder.clearContext();
	}

	protected String getToken() {
		User loginUser = userFindHelper.findUser(1L);
		return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
	}
}
