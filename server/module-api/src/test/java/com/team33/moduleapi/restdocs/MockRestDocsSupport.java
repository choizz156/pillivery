// package com.team33.moduleapi.restdocs;
//
// import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
// import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.springframework.restdocs.RestDocumentationContextProvider;
// import org.springframework.restdocs.RestDocumentationExtension;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
// import io.restassured.module.mockmvc.RestAssuredMockMvc;
// import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
//
// @ExtendWith(RestDocumentationExtension.class)
// public abstract class MockRestDocsSupport {
//
//     //rest-docs assured
//     protected MockMvcRequestSpecification givenSpec;
//
//     @BeforeEach
//     void beforeEach(RestDocumentationContextProvider restDocumentation) {
//         this.givenSpec = RestAssuredMockMvc.given()
//             .mockMvc(
//                 MockMvcBuilders.standaloneSetup(testController())
//                     .apply(documentationConfiguration(restDocumentation)
//                         .operationPreprocessors()
//                         .withRequestDefaults(prettyPrint())
//                         .withResponseDefaults(prettyPrint())
//                     )
//                     .build()
//             );
//     }
//
//     protected abstract Object testController();
// }
