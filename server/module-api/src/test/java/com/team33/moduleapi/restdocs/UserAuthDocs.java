package com.team33.moduleapi.restdocs;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.modulecore.security.dto.LoginDto;
import com.team33.moduleapi.api.user.dto.UserPostDto;
import com.team33.moduleapi.api.user.mapper.UserServiceMapper;
import com.team33.modulecore.core.user.application.UserService;
import com.team33.modulecore.core.user.dto.UserServicePostDto;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

class UserAuthDocs extends WebRestDocsSupport {

	@Autowired
	private UserService userService;

	@Test
	void 로그인12() throws Exception {

		회원_가입("displayName", "010-1112-1111", "test@gmail.com");
		LoginDto dto = LoginDto.builder().username("test@gmail.com").password("password1!").build();

		given(spec)
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto)
			.filter(document("user-login",
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
			.post("/api/users/auth")
			.then()
			.statusCode(HttpStatus.OK.value())
			.header("Authorization", notNullValue())
			.log().all().extract();
	}

	@DisplayName("비밀번호 오류로 인한 로그인 실패")
	@Test
	void 비밀번호_오류() throws Exception {
		//given

		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		LoginDto dto = LoginDto.builder().username("test@gmail.com").password("sdfsde!1").build();

		ExtractableResponse<Response> response =
			given(spec)
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
				.post("/api/users/auth")
				.then()
				.extract();

		assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(response.body().jsonPath().get("message").toString())
			.isIn("Bad credentials", "자격 증명에 실패하였습니다.");

	}

	@DisplayName("이메일 오류로 인한 로그인 실패")
	@Test
	void test5() throws Exception {
		//given
		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		LoginDto dto = LoginDto.builder().username("tet@gmail.com").password("sdfsdfe!1").build();

		ExtractableResponse<Response> response = given(spec)
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
			.post("/api/users/auth")
			.then()
			.log().all().extract();

		assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(response.body().jsonPath().get("message").toString())
			.isIn("Bad credentials", "자격 증명에 실패하였습니다.");
	}

	private UserPostDto join(String email, String displayName, String phone) {

		String password = "password1!";
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

	private void 회원_가입(String displayName, String phone, String mail) {
		UserPostDto postDto = join(mail, displayName, phone);
		UserServicePostDto userServicePostDto = new UserServiceMapper().toUserPost(postDto);
		userService.join(userServicePostDto);
	}
}
