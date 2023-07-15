package com.team33.moduleapi.docs;

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

import com.team33.moduleapi.controller.ApiTest;
import com.team33.moduleapi.controller.UserAccount;
import com.team33.modulecore.domain.user.dto.UserPatchDto;
import com.team33.modulecore.domain.user.dto.UserPostDto;
import com.team33.modulecore.domain.user.dto.UserPostOauthDto;
import com.team33.modulecore.domain.user.entity.Address;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.entity.UserRoles;
import com.team33.modulecore.global.security.dto.LoginDto;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class UserApiDocs extends ApiTest {


    @DisplayName("회원 가입")
    @Test
    void 회원가입() throws Exception {

        UserPostDto joinDto = join("test@gmail.com", "test1", "010-1111-1111");

        RestAssured
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(joinDto)
            .filter(document("user-create",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("data").type(JsonFieldType.STRING).description("회원 가입 완료")
                    )
                )
            )
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
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto2)
            .filter(document("user-error1-dn",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("이미 존재하는 닉네임입니다."),
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("Bad Request")
                    )
                )
            )
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
            .filter(document("user-error2-email",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("이미 가입한 e-mail입니다."),
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("Bad Request")
                    )
                )
            )
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

        RestAssured
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto2)
            .filter(document("user-error3-phone",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("이미 존재하는 연락처입니다."),
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("Bad Request")
                    )
                )
            )
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

        RestAssured
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto)
            .filter(document("user-more-info",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("data").type(JsonFieldType.STRING)
                            .description("소셜 회원 추가 정보 기입 완료")
                    )
                )
            )
            .when()
            .post("/users/more-info")
            .then()
            .assertThat().statusCode(HttpStatus.CREATED.value())
            .assertThat().header("Authorization", Matchers.notNullValue())
            .assertThat().body(Matchers.containsString("소셜 회원 추가 정보 기입 완료"))
            .log().all().extract();

    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 탈퇴")
    @Test
    void 회원_탈퇴() throws Exception {

        String token = getToken();
        RestAssured
            .given(super.spec)
            .log().all()
            .header("Authorization", token)
            .filter(document("user-withdrawal",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("data").type(JsonFieldType.STRING)
                            .description("회원 탈퇴")
                    )
                )
            )
            .when()
            .delete("/users")
            .then()
            .assertThat().statusCode(HttpStatus.ACCEPTED.value())
            .assertThat().body(Matchers.containsString("USER_WITHDRAWAL"))
            .log().all().extract();

    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보 수정")
    @Test
    void 회원_정보_수정() throws Exception {

        String token = getToken();
        UserPatchDto userPatchDto = updateUser("test2", "010-1121-1111");

        RestAssured
            .given(super.spec)
            .log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .filter(document("user-update",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("data.realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("data.detailAddress").type(JsonFieldType.STRING)
                            .description("상세 주소"),
                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("data.displayName").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("data.social").type(JsonFieldType.BOOLEAN)
                            .description("소셜 로그인(false)"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING)
                            .description("최근 수정일"),
                        fieldWithPath("data.createAt").type(JsonFieldType.STRING).description("회원가입 일")
                    )
                )
            )
            .when()
            .patch("/users")
            .then()
            .assertThat().statusCode(HttpStatus.ACCEPTED.value())
            .assertThat().body(containsString(userPatchDto.getEmail()))
            .assertThat().body(containsString(userPatchDto.getCity()))
            .assertThat().body(containsString(userPatchDto.getRealName()))
            .assertThat().body(containsString(userPatchDto.getDetailAddress()))
            .assertThat().body(containsString(userPatchDto.getPhone()))
            .log().all().extract();
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보 수정 시 닉네임 중복의 경우 예외가 발생합니다.")
    @Test
    void 정보_수정_닉네임_중복_예외() throws Exception {

        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        String token = getToken();
        UserPatchDto userPatchDto = updateUser("test22", "010-1111-1111");

        RestAssured
            .given(super.spec)
            .log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .filter(document("user-update-error1-dn",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("이미 존재하는 닉네임입니다."),
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("Bad Request")
                    )
                )
            )
            .when()
            .patch("/users")
            .then()
            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
            .assertThat().body(containsString("이미 존재하는 닉네임입니다."))
            .log().all().extract();
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보 수정 시 연락처 중복의 경우 예외가 발생합니다.")
    @Test
    void 정보_수정_연락처_중복_예외() throws Exception {

        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        String token = getToken();
        UserPatchDto userPatchDto = updateUser("test1", "010-1112-1111");

        RestAssured
            .given(super.spec)
            .log().all()
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .filter(document("user-update-error2-phone",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("이미 존재하는 연락처입니다."),
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("Bad Request")
                    )
                )
            )
            .when()
            .patch("/users")
            .then()
            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
            .assertThat().body(containsString("이미 존재하는 연락처입니다."))
            .log().all().extract();
    }

    @UserAccount({"test", "010-0000-0000"})
    @DisplayName("회원 정보를 조회 (회원 가입을 통한 로그인일 경우 social false)")
    @Test
    void 회원_조회() throws Exception {
        String token = getToken();

        RestAssured
            .given(super.spec)
            .log().all()
            .header("Authorization", token)
            .filter(document("user-general",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("data.realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("data.detailAddress").type(JsonFieldType.STRING)
                            .description("상세 주소"),
                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("data.displayName").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("data.social").type(JsonFieldType.BOOLEAN)
                            .description("소셜 로그인(false)"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING)
                            .description("최근 수정일"),
                        fieldWithPath("data.createAt").type(JsonFieldType.STRING).description("회원가입일")
                    )
                )
            )
            .when()
            .get("/users")
            .then()
            .assertThat().statusCode(HttpStatus.OK.value())
            .assertThat().body(containsString("test@test.com"))
            .assertThat().body(containsString("test"))
            .assertThat().body(containsString("압구정동"))
            .assertThat().body(containsString("name"))
            .assertThat().body(containsString("서울"))
            .assertThat().body(containsString("010-0000-0000"))
            .assertThat().body(containsString("압구정동"))
            .assertThat().body(containsString("false"))
            .log().all().extract();
    }

    @DisplayName("회원 정보를 조회 (oauth를 통한 로그인일 경우 social true)")
    @Test
    void 회원_조회2() throws Exception {
        userRepository.save(oauthUser());
        String token = "Bearer " + jwtTokenProvider.delegateAccessToken(oauthUser());

        RestAssured
            .given(super.spec)
            .log().all()
            .header("Authorization", token)
            .filter(document("user-social",
                    preprocessRequest(modifyUris()
                        .scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()
                    ),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("data.realName").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("data.detailAddress").type(JsonFieldType.STRING)
                            .description("상세 주소"),
                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("연락처"),
                        fieldWithPath("data.displayName").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("data.social").type(JsonFieldType.BOOLEAN)
                            .description("소셜 로그인(true)"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING)
                            .description("최근 수정일"),
                        fieldWithPath("data.createAt").type(JsonFieldType.STRING).description("회원가입일")
                    )
                )
            )
            .when()
            .get("/users")
            .then()
            .assertThat().statusCode(HttpStatus.OK.value())
            .assertThat().body(containsString("test@test.com"))
            .assertThat().body(containsString("test"))
            .assertThat().body(containsString("압구정동"))
            .assertThat().body(containsString("name"))
            .assertThat().body(containsString("서울"))
            .assertThat().body(containsString("010-0000-0000"))
            .assertThat().body(containsString("압구정동"))
            .assertThat().body(containsString("true"))
            .log().all().extract();
    }

    @Test
    void 로그인() throws Exception {
        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        LoginDto dto = LoginDto.builder().username("test@gmail.com").password("sdfsdfe!1").build();

        RestAssured
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto)
            .filter(
                document("user-login",
                    preprocessRequest(modifyUris().scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                    )
                )
            )
            .when()
            .post("/users/login")
            .then()
            .assertThat().statusCode(HttpStatus.OK.value())
            .assertThat().header("Authorization", Matchers.notNullValue())
            .log().all().extract();
    }

    @DisplayName("비밀번호 오류로 인한 로그인 실패")
    @Test
    void 비밀번호_오류() throws Exception {
        //given
        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        LoginDto dto = LoginDto.builder().username("test@gmail.com").password("sdfsd1").build();

        ExtractableResponse<Response> response = RestAssured
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto)
            .filter(
                document("user-login-error-pw",
                    preprocessRequest(modifyUris().scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                    )
                )
            )
            .when()
            .post("/users/login")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.body().jsonPath().get("message").toString())
            .isIn("Bad credentials", "자격 증명에 실패하였습니다.");

    }

    @DisplayName("이메일 오류로 인한 로그인 실패")
    @Test
    void test5() throws Exception {
        //given
        UserPostDto postDto = join("test@gmail.com", "test22", "010-1112-1111");
        userService.join(postDto);

        LoginDto dto = LoginDto.builder().username("te2st@gmail.com").password("sdfsdfe!1").build();

        ExtractableResponse<Response> response = RestAssured
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(dto)
            .filter(
                document("user-login-error-email",
                    preprocessRequest(modifyUris().scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                    )
                )
            )
            .when()
            .post("/users/login")
            .then()
            .log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.body().jsonPath().get("message").toString())
            .isIn("Bad credentials", "자격 증명에 실패하였습니다.");
    }

    @UserAccount({"test", "010-0000-0000"})
    @Test
    void 로그아웃() throws Exception {

        String token = getToken();

        RestAssured
            .given(super.spec)
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", token)
            .filter(
                document("user-logout",
                    preprocessRequest(modifyUris().scheme("http")
                        .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                        .removePort(), prettyPrint())
                )
            )
            .when()
            .post("/users/logout")
            .then()
            .assertThat().statusCode(HttpStatus.ACCEPTED.value())
            .log().all().extract();
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
        String password = "12311!adf3";
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
}
