package com.team33.moduleapi.docs;

import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import org.springframework.restdocs.payload.ResponseBodySnippet;

import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
class ProfileDocsTest extends WebRestDocsSupport {

    @Test
    void 프로필_조회_문서화() {
        given(spec)
            .filter(document("profile",
                preprocessResponse(prettyPrint()),
                responseBody()))
            .when()
            .get("/profile")
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    void 헬스_체크_문서화() {
        given(spec)
            .filter(document("health",
                preprocessResponse(prettyPrint()),
                responseBody()))
            .when()
            .get("/health")
            .then()
            .log().all()
            .statusCode(200);
    }
}
