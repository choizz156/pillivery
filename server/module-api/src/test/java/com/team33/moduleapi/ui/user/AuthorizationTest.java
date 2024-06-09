package com.team33.moduleapi.ui.user;

import static io.restassured.RestAssured.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.ApiTest;

class AuthorizationTest extends ApiTest {

    @DisplayName("user 권한이 없으면, 회원 정보를 조회할 수 없다.")
    @Test
    void test1() throws Exception {
        given()
                .log().all()
        .when()
                .get("/users")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @DisplayName("user 권한이 없으면, 주문을 할 수 없다.")
    @Test
    void test2() throws Exception {
        given()
                .log().all()
        .when()
                .get("/orders/**")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @DisplayName("user 권한이 없으면, 장바구니 기능을 사용할 수 없다.")
    @Test
    void test3() throws Exception {
        given()
                .log().all()
        .when()
                .get("carts/**")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @DisplayName("user 권한이 없으면, 리뷰 기능을 사용할 수 없다.")
    @Test
    void test5() throws Exception {
        given()
                .log().all()
        .when()
                .post("/reviews/**")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @DisplayName("user 권한이 없으면, 정기 구독 수정을 할 수 없다.")
    @Test
    void test6() throws Exception {
        given()
                .log().all()
        .when()
                .patch("/schedule")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @DisplayName("user 권한이 없으면, 정기 구독 취소를 할 수 없다.")
    @Test
    void test7() throws Exception {
        given()
                .log().all()
        .when()
                .delete("/schedule")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @DisplayName("user 권한이 없으면, 결제를 할 수 없다.")
    @Test
    void test8() throws Exception {
        given()
                .log().all()
        .when()
                .post("/payments/1")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }
}
