package com.team33.moduleapi.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import com.team33.ApiTest;
import com.team33.UserAccount;
import com.team33.modulecore.domain.user.dto.UserPatchDto;
import com.team33.modulecore.domain.user.dto.UserPostDto;
import com.team33.modulecore.domain.user.dto.UserPostOauthDto;
import com.team33.modulecore.domain.user.entity.Address;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.entity.UserRoles;
import com.team33.modulecore.domain.user.repository.UserRepository;
import com.team33.modulecore.domain.user.service.UserService;
import com.team33.modulecore.global.security.dto.LoginDto;
import com.team33.modulecore.global.security.jwt.JwtTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class UserApiTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("회원 가입")
    @Test
    void 회원가입() throws Exception {

        UserPostDto joinDto = join("test@gmail.com", "test1", "010-1111-1111");

        RestAssured
            .given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(joinDto)
            .when().post("/users")
            .then()
                .log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .assertThat().body(containsString("회원 가입 완료"))
                .extract();
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 가입 시 닉네임 중복의 경우 예외가 발생합니다.")
    @Test
    void 회원가입_닉네임_중복() throws Exception {

        UserPostDto dto2 = join("teset2@gmail.com", "test", "010-1111-1111");

        RestAssured
            .given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto2)
            .when().post("/users")
            .then()
                .log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .assertThat().body(containsString("이미 존재하는 닉네임입니다."))
                .extract();
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 가입 시 이메일 중복의 경우 예외가 발생합니다.")
    @Test
    void 회원가입_이메일_중복() throws Exception {

        UserPostDto dto2 = join("test@test.com", "test112", "010-1111-1111");

        RestAssured
            .given(super.spec)
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto2)
            .when().post("/users")
            .then()
                .log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .assertThat().body(containsString("이미 가입한 e-mail입니다."))
                .extract();
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 가입 시 연락처가 중복될 경우 예외 처리 ")
    @Test
    void 회원가입_연락처_중복() throws Exception {
        UserPostDto dto2 = join("test@gmail.com", "test112", "010-0000-0000");

        ExtractableResponse<Response> response = RestAssured
            .given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto2)
            .when()
                .post("/users")
            .then()
            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
            .assertThat().body(Matchers.containsString("이미 존재하는 연락처입니다."))
            .log().all().extract();
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
            .post("/users/more-info")
            .then()
            .assertThat().statusCode(HttpStatus.CREATED.value())
            .assertThat().header("Authorization",Matchers.notNullValue())
            .assertThat()
            .log()
            .all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Authorization")).isNotBlank();
        assertThat(response.body().jsonPath().get("data").toString())
            .hasToString("소셜 회원 추가 정보 기입 완료");
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 탈퇴")
    @Test
    void 회원_탈퇴() throws Exception {

        String token = getToken();
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

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보 수정")
    @Test
    void 회원_정보_수정() throws Exception {

        String token = getToken();
        UserPatchDto userPatchDto = updateUser("test2", "010-1121-1111");

        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .when()
            .patch("/users")
            .then()
            .log().all().extract();

        SoftAssertions.assertSoftly(soft -> {
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

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보 수정 시 닉네임 중복의 경우 예외가 발생합니다.")
    @Test
    void 정보_수정_닉네임_중복_예외() throws Exception {

        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        String token = getToken();
        UserPatchDto userPatchDto = updateUser("test22", "010-1111-1111");

        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .when()
            .patch("/users")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        String data = response.jsonPath().get("message").toString();
        assertThat(data).isEqualTo("이미 존재하는 닉네임입니다.");
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보 수정 시 연락처 중복의 경우 예외가 발생합니다.")
    @Test
    void 정보_수정_연락처_중복_예외() throws Exception {

        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        String token = getToken();
        UserPatchDto userPatchDto = updateUser("test1", "010-1112-1111");

        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .when()
            .patch("/users")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        String data = response.jsonPath().get("message").toString();
        assertThat(data).isEqualTo("이미 존재하는 연락처입니다.");
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보를 조회 (회원 가입을 통한 로그인일 경우 social false)")
    @Test
    void 회원_조회() throws Exception {
        String token = getToken();

        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .header("Authorization", token)
            .when()
            .get("/users")
            .then()
            .log().all().extract();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            soft.assertThat((Boolean) response.jsonPath().getMap("data").get("social")).isFalse();
            soft.assertThat(response.jsonPath().getMap("data").get("email"))
                .isEqualTo("test@test.com");
            soft.assertThat(response.jsonPath().getMap("data").get("displayName"))
                .isEqualTo("test");
            soft.assertThat(response.jsonPath().getMap("data").get("detailAddress"))
                .isEqualTo("압구정동");
            soft.assertThat(response.jsonPath().getMap("data").get("realName")).isEqualTo("name");
            soft.assertThat(response.jsonPath().getMap("data").get("phone"))
                .isEqualTo("010-0000-0000");
            soft.assertThat(response.jsonPath().getMap("data").get("city")).isEqualTo("서울");
        });
    }

    @DisplayName("회원 정보를 조회 (oauth를 통한 로그인일 경우 social true)")
    @Test
    void 회원_조회2() throws Exception {
        userRepository.save(oauthUser());
        String token = "Bearer " + jwtTokenProvider.delegateAccessToken(oauthUser());
        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .header("Authorization", token)
            .when()
            .get("/users")
            .then()
            .log().all().extract();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            soft.assertThat((Boolean) response.jsonPath().getMap("data").get("social")).isTrue();
            soft.assertThat(response.jsonPath().getMap("data").get("email"))
                .isEqualTo("test@test.com");
            soft.assertThat(response.jsonPath().getMap("data").get("displayName"))
                .isEqualTo("test");
            soft.assertThat(response.jsonPath().getMap("data").get("detailAddress"))
                .isEqualTo("압구정동");
            soft.assertThat(response.jsonPath().getMap("data").get("realName")).isEqualTo("name");
            soft.assertThat(response.jsonPath().getMap("data").get("phone"))
                .isEqualTo("010-0000-0000");
            soft.assertThat(response.jsonPath().getMap("data").get("city")).isEqualTo("서울");
        });
    }

    @Test
    void 로그인() throws Exception {
        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        LoginDto dto = LoginDto.builder().username("test@gmail.com").password("sdfsdfe!1").build();

        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto)
            .when()
            .post("/users/login")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.header("Authorization")).isNotBlank();
    }

    @UserAccount({"test", "010-0000-0000"})
    @Test
    void 로그아웃() throws Exception {

        String token = getToken();

        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", token)
            .when()
            .post("/users/logout")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
    }

    private UserPostDto join(String email, String displayName, String phone) {

        String password = "sdfsdfe!1";
        String city = "서울시 부평구 송도동";
        String detailAddress = "101 번지";
        String realName = "홍길동";

        return UserPostDto.builder()
            .detailAddress(detailAddress)
            .city(city)
            .email(email)
            .phone(phone)
            .realName(realName)
            .password(password)
            .displayName(displayName)
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

    private UserPatchDto updateUser(String displayName, String phone) {
        String email = "test@test.com";
        String password = "123123";
        String city = "수원";
        String detailAddress = "압구정";
        String realName = "dignem";

        return UserPatchDto.builder().phone(phone).city(city).detailAddress(detailAddress)
            .displayName(displayName).email(email).password(password).realName(realName).build();
    }

    private User oauthUser() {
        String displayName = "test";
        String email = "test@test.com";
        String password = "sdfsdf232!";
        String city = "서울";
        String detailAddress = "압구정동";
        String realName = "name";
        String phone = "010-0000-0000";
        String oauthId = "sub";

        return User.builder().displayName(displayName).email(email).password(password)
            .address(new Address(city, detailAddress))
            .realName(realName)
            .phone(phone)
            .oauthId(oauthId)
            .roles(UserRoles.USER)
            .build();
    }

    private String getToken() {
        User loginUser = userService.getLoginUser();
        return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
    }
}
