
package com.team33.moduleapi.docs;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.team33.moduleapi.ApiTest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.specification.RequestSpecification;

// @ActiveProfiles("test")
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
public abstract class WebRestDocsSupport extends ApiTest {

	// @Autowired
	// protected UserFindHelper userFindHelper;
	// @Autowired
	// protected JwtTokenProvider jwtTokenProvider;
	// @Autowired
	// protected DataCleaner dataCleaner;
	protected MockMvcRequestSpecification webSpec;
	protected RequestSpecification spec;
	// @LocalServerPort
	// private int port;

	@BeforeEach
	void beforeEach(WebApplicationContext web, RestDocumentationContextProvider restDocumentation,
		RestDocumentationContextProvider provider) throws Exception {

		// if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
		// 	RestAssured.port = port;
		// 	dataCleaner.afterPropertiesSet();
		// 	SecurityContextHolder.clearContext();
		// }

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
				RestAssuredRestDocumentation.documentationConfiguration(provider)
					.operationPreprocessors()
					.withRequestDefaults(modifyUris().scheme("https").host("localhost").removePort(),prettyPrint())
					.withResponseDefaults(prettyPrint()))
			.build();
	}

	// @AfterEach
	// void tearDown() {
	//
	// 	dataCleaner.execute();
	// 	SecurityContextHolder.clearContext();
	// }
	//
	// protected String getToken() {
	//
	// 	User loginUser = userFindHelper.findUser(1L);
	// 	return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
	// }
}
