package server.team33.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.team33.ApiTest;
import server.team33.domain.UserAccount;
import server.team33.domain.user.dto.UserPatchDto;
import server.team33.domain.user.dto.UserPostDto;
import server.team33.domain.user.dto.UserPostOauthDto;
import server.team33.domain.user.entity.User;
import server.team33.domain.user.entity.UserRoles;
import server.team33.domain.user.repository.UserRepository;

class UserApiTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @DisplayName("회원가입")
    @Test
    void 회원가입() throws Exception {
        UserPostDto joinDto = join("test@gmail.com", "test1", "010-1111-1111");

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

    @UserAccount({"test11", "010-2220-2222"})
    @DisplayName("회원 가입시 닉네임 중복의 경우 예외가 발생합니다.")
    @Test
    void 회원가입_닉네임_중복() throws Exception {

        UserPostDto dto2 = join("teset2@gmail.com", "test11", "010-1111-1111");

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

    @UserAccount({"test", "010-2222-2222"})
    @DisplayName("회원 가입시 이메일 중복의 경우 예외가 발생합니다.")
    @Test
    void 회원가입_이메일_중복() throws Exception {

        UserPostDto dto2 = join("test@test.com", "test112", "010-1111-1111");

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

    @UserAccount({"test1", "010-1111-1111"})
    @DisplayName("회원 가입 시 연락처가 중복될 경우 예외 처리 ")
    @Test
    void 회원가입_연락처_중복() throws Exception {
        UserPostDto dto2 = join("test@gmail.com", "test112", "010-1111-1111");

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
        assertThat(data).isEqualTo("이미 존재하는 연락처입니다.");
    }

    @DisplayName("oauth 로그인 시 추가 정보를 기입하면, 토큰이 발급됩니다.")
    @Test
    void oauth_토큰_발행() throws Exception {
        User user = User.builder().email("test@gmail.com").roles(UserRoles.USER).build();
        userRepository.save(user);

        UserPostOauthDto dto = oauthJoin();
        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto)
            .when()
            .post("users/more-info")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Authorization")).isNotBlank();
        assertThat(response.body().asPrettyString()).isEqualTo("소셜 회원 추가 정보 기입 완료");
    }

    @UserAccount({"test", "010-1111-1111"})
    @DisplayName("회원 탈퇴")
    @Test
    void 회원_탈퇴() throws Exception {

        String token = super.getToken();
        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .header("Authorization", token)
            .when()
            .delete("/users")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
        String data = response.jsonPath().get("data").toString();
        assertThat(data).isEqualTo("USER_WITHDRAWAL");
    }

    @UserAccount({"test", "010-1111-1111"})
    @DisplayName("회원 정보 수정")
    @Test
    void 회원_정보_수정() throws Exception {

        String token = super.getToken();
        UserPatchDto userPatchDto = updateUser();

        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .when()
            .patch("/users")
            .then()
            .log().all().extract();

        assertSoftly(soft -> {
            soft.assertThat(response.statusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
            soft.assertThat(response.jsonPath().getMap("data"))
                .containsValues(userPatchDto.getEmail())
                .containsValues(userPatchDto.getCity())
                .containsValues(userPatchDto.getRealName())
                .containsValues(userPatchDto.getDetailAddress())
                .containsValues(userPatchDto.getPhone()
                );
        });
    }

    private UserPostDto join(String email, String displayName, String phone) {

        String password = "1234";
        String city = "서울시 부평구 송도동";
        String detailAddress = "101 번지";
        String realName = "sdfgsdf";

        return UserPostDto.builder().detailAddress(detailAddress).city(city).email(email)
            .phone(phone).realName(realName).password(password).displayName(displayName)
            .build();
    }

    private UserPostOauthDto oauthJoin() {

        String email = "test@gmail.com";
        String displayName = "test2";
        String city = "서울";
        String detailAddress = "압구정동";
        String phone = "010-3333-333";

        return UserPostOauthDto.builder().phone(phone).detailAddress(detailAddress).city(city)
            .email(email).displayName(displayName).build();
    }

    private UserPatchDto updateUser() {
        String email = "test@test.com";
        String password = "123123";
        String displayName = "test22";
        String city = "수원";
        String detailAddress = "압구정";
        String realName = "dignem";
        String phone = "010-4344-444";

        return UserPatchDto.builder().phone(phone).city(city).detailAddress(detailAddress)
            .displayName(displayName).email(email).password(password).realName(realName).build();
    }

}