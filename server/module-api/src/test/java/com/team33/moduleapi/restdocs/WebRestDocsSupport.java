package com.team33.moduleapi.restdocs;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.team33.modulecore.user.domain.entity.User;
import com.team33.modulecore.user.domain.repository.UserRepository;
import com.team33.modulecore.user.application.UserService;
import com.team33.moduleapi.security.infra.JwtTokenProvider;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles({"test", "auth", "quartztest"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public abstract class WebRestDocsSupport {

    @LocalServerPort
    private int port;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

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
        userRepository.deleteAll();
    }

    protected String getToken() {
        User loginUser = userService.getLoginUser();
        return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
    }
}
