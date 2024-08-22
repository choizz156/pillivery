
package com.team33.moduleapi.restdocs;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.team33.moduleapi.DataCleaner;
import com.team33.modulecore.security.infra.JwtTokenProvider;
import com.team33.modulecore.core.common.UserFindHelper;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.specification.RequestSpecification;

@ActiveProfiles({"auth", "test"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
public abstract class WebRestDocsSupport {

	@LocalServerPort
	private int port;

	@Autowired
	protected UserFindHelper userFindHelper;

	@Autowired
	protected JwtTokenProvider jwtTokenProvider;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected DataCleaner dataCleaner;

	protected MockMvcRequestSpecification webSpec;

	protected RequestSpecification spec;

	@BeforeEach
	void beforeEach(WebApplicationContext web, RestDocumentationContextProvider restDocumentation,
		RestDocumentationContextProvider provider) throws Exception {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
			dataCleaner.afterPropertiesSet();
		}

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

		spec = new RequestSpecBuilder().addFilter(
			RestAssuredRestDocumentation.documentationConfiguration(provider)).build();
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
