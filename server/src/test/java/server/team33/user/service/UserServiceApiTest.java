package server.team33.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import server.team33.ApiTest;
import server.team33.domain.user.dto.UserPostDto;
import server.team33.domain.user.service.UserService;


class UserServiceApiTest extends ApiTest {

    @Autowired
    private UserService userService;
    @Test
    void 회원가입() throws Exception {
        UserPostDto joinDto = join("test@gmail.com", "test1");

        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(joinDto)
            .when()
                .post("/users")
            .then()
                .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        String data = response.jsonPath().get("data").toString();
        assertThat(data).isEqualTo("회원 가입 완료");
    }

    @DisplayName("회원 가입시 닉네임 중복")
    @Test
    void 회원가입_닉네임_중복() throws Exception {
        UserPostDto dto = join("test@gmail.com", "test11");
        userService.join(dto);

        UserPostDto dto2 = join("teset2@gmail.com", "test11");

        ExtractableResponse<Response> response = RestAssured
            .given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto2)
            .when()
                .post("/users")
            .then()
                .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        String data = response.jsonPath().get("message").toString();
        assertThat(data).isEqualTo("이미 존재하는 닉네임입니다.");

    }
    @DisplayName("회원 가입시 이메일 중복")
    @Test
    void 회원가입_이메일_중복() throws Exception {
        UserPostDto dto = join("test@gmail.com", "test11");
        userService.join(dto);

        UserPostDto dto2 = join("test@gmail.com", "test112");

        ExtractableResponse<Response> response = RestAssured
            .given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto2)
            .when()
                .post("/users")
            .then()
                .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        String data = response.jsonPath().get("message").toString();
        assertThat(data).isEqualTo("이미 가입한 e-mail입니다.");

    }

    private UserPostDto join(String email, String displayName) {

        String password = "1234";
        String city = "서울시 부평구 송도동";
        String detailAddress = "101 번지";
        String realName = "sdfgsdf";
        String phone = "010-2030-203";

        return UserPostDto.builder().detailAddress(detailAddress).city(city).email(email)
            .phone(phone).realName(realName).password(password).displayName(displayName)
            .build();
    }
}