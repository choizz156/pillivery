
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


@ExtendWith(RestDocumentationExtension.class)
public abstract class WebRestDocsSupport extends ApiTest {


	protected MockMvcRequestSpecification webSpec;
	protected RequestSpecification spec;


	@BeforeEach
	void beforeEach(WebApplicationContext web, RestDocumentationContextProvider restDocumentation,
		RestDocumentationContextProvider provider) throws Exception {

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

}
