package com.team33.moduleapi.docs;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.moduleapi.api.user.dto.UserPatchDto;
import com.team33.moduleapi.api.user.dto.UserPostDto;
import com.team33.moduleapi.api.user.dto.UserPostOauthDto;
import com.team33.moduleapi.mockuser.UserAccount;

class UserDocsTest extends WebRestDocsSupport {

    private static final String BASE_URL = "/api/users";

    @Test
    void 회원가입_문서화() {
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setEmail("test@example.com");
        userPostDto.setPassword("password123!");
        userPostDto.setDisplayName("테스트유저");
        userPostDto.setRealName("홍길동");
        userPostDto.setCity("서울시");
        userPostDto.setDetailAddress("강남구 역삼동");
        userPostDto.setPhone("010-1234-5678");

        given(spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPostDto)
            .filter(document("user-signup",
                requestHeaders(
                    headerWithName("Content-Type").description("요청 컨텐츠 타입")),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("realName").type(JsonFieldType.STRING).description("실명"),
                    fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세주소"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호")),
                requestHeaders(
                    headerWithName("Content-Type").description("요청 컨텐츠 타입")),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("realName").type(JsonFieldType.STRING).description("실명"),
                    fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세주소"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호"))))
            .when()
            .post(BASE_URL)
            .then()
            .log().all()
            .statusCode(201);
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void OAuth_추가정보_문서화() {
        UserPostOauthDto userPostOauthDto = new UserPostOauthDto();
        userPostOauthDto.setEmail("test@test.com");
        userPostOauthDto.setDisplayName("OAuth유저");
        userPostOauthDto.setCity("서울시");
        userPostOauthDto.setDetailAddress("강남구 역삼동");
        userPostOauthDto.setPhone("010-9876-5432");

        given(spec)
            .header("Authorization", getToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPostOauthDto)
            .filter(document("user-oauth-more-info",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰"),
                    headerWithName("Content-Type").description("요청 컨텐츠 타입")),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세주소"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호"))))
            .when()
            .post(BASE_URL + "/more-info")
            .then()
            .log().all()
            .statusCode(anyOf(is(200), is(201), is(204)));
    }

    @Test
    @UserAccount({"test2", "010-1111-1111"})
    void 사용자_정보_수정_문서화() {
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setDisplayName("수정된이름");
        userPatchDto.setEmail("test2@test.com");
        userPatchDto.setPassword("newpassword123!");
        userPatchDto.setCity("부산시");
        userPatchDto.setDetailAddress("해운대구");
        userPatchDto.setRealName("김철수");
        userPatchDto.setPhone("010-5555-5555");

        given(spec)
            .header("Authorization", getToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userPatchDto)
            .filter(document("user-update",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰"),
                    headerWithName("Content-Type").description("요청 컨텐츠 타입")),
                pathParameters(
                    parameterWithName("userId").description("사용자 ID")),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                    fieldWithPath("displayName").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("city").type(JsonFieldType.STRING).description("도시"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세주소"),
                    fieldWithPath("realName").type(JsonFieldType.STRING).description("실명"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호"))))
            .when()
            .patch(BASE_URL + "/{userId}", 1)
            .then()
            .log().all()
            .statusCode(anyOf(is(200), is(201), is(204)));
    }

    @Test
    @UserAccount({"test3", "010-2222-2222"})
    void 사용자_정보_조회_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .filter(document("user-get",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("userId").description("사용자 ID"))))
            .when()
            .get(BASE_URL + "/{userId}", 1)
            .then()
            .log().all()
            .statusCode(anyOf(is(200), is(201), is(204)));
    }

    @Test
    @UserAccount({"test", "010-0000-0000"})
    void 로그아웃_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .filter(document("user-logout",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰"))))
            .when()
            .post(BASE_URL + "/logout")
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    @UserAccount({"test8", "010-0000-0000"})
    void 회원탈퇴_문서화() {
        given(spec)
            .header("Authorization", getToken())
            .filter(document("user-delete",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")),
                pathParameters(
                    parameterWithName("userId").description("사용자 ID"))))
            .when()
            .delete(BASE_URL + "/{userId}", 1)
            .then()
            .log().all()
            .statusCode(anyOf(is(200), is(201), is(204)));
    }
}
