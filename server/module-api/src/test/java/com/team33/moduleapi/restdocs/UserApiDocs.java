package com.team33.moduleapi.restdocs;

import static org.assertj.core.api.SoftAssertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.moduleapi.mockuser.OAuthAccount;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.moduleapi.api.user.dto.UserPatchDto;
import com.team33.moduleapi.api.user.dto.UserPostDto;
import com.team33.moduleapi.api.user.dto.UserPostOauthDto;
import com.team33.moduleapi.api.user.mapper.UserServiceMapper;
import com.team33.modulecore.core.user.application.UserService;
import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.user.domain.UserRoles;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.dto.UserServicePostDto;

import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.response.ExtractableResponse;

class UserApiDocs extends WebRestDocsSupport {
	@Autowired
	private UserService userService;

	@DisplayName("회원 가입")
	@Test
	void 회원가입() throws Exception {

		UserPostDto joinDto = join("test@gmail.com", "test1", "010-1111-1111");

		super.webSpec
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(joinDto)
			.when()
			.post("/api/users")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.apply(document("user-create",
					preprocessRequest(modifyUris()
						.scheme("http")
						.host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
						.removePort()
					),
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
						fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
						fieldWithPath("data.normalCartId").type(JsonFieldType.NUMBER).description("일반 카트 아이디"),
						fieldWithPath("data.subscriptionCartId").type(JsonFieldType.NUMBER).description("구독 카트 아이디"),
						fieldWithPath("createTime").type(JsonFieldType.STRING).description("생성 시간")
					)
				)
			);

	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 가입 시 닉네임 중복의 경우 예외가 발생합니다.")
	@Test
	void 회원가입_닉네임_중복() throws Exception {

		UserPostDto dto2 = join("teset2@gmail.com", "test", "010-1111-1111");

		super.webSpec
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto2)
			.when().post("/api/users")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(containsString("이미 존재하는 닉네임입니다."))
			.apply(document("user-error1-dn",
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
			);
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 가입 시 이메일 중복의 경우 예외가 발생합니다.")
	@Test
	void 회원가입_이메일_중복() throws Exception {

		UserPostDto dto2 = join("test@test.com", "test112", "010-1111-1111");

		super.webSpec
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto2)
			.when().post("/api/users")
			.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(containsString("이미 가입한 e-mail입니다."))
			.apply(document("user-error2-email",
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
			);
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 가입 시 연락처가 중복될 경우 예외 처리 ")
	@Test
	void 회원가입_연락처_중복() throws Exception {

		UserPostDto dto2 = join("test@gmail.com", "test112", "010-0000-0000");

		super.webSpec
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto2)
			.when()
			.post("/api/users")
			.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(containsString("이미 존재하는 연락처입니다."))
			.apply(document("user-error3-phone",
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
			);
	}

	@DisplayName("oauth 로그인 시 추가 정보를 기입하면, 토큰이 발급됩니다.")
	@Test
	void oauth_토큰_발행() throws Exception {
		User user = User.builder().email("test@gmail.com").roles(UserRoles.USER).build();
		userRepository.save(user);

		UserPostOauthDto dto = oauthJoin();

		super.webSpec
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto)
			.when()
			.post("/api/users/more-info")
			.then()
			.statusCode(HttpStatus.CREATED.value())
			.header("Authorization", notNullValue())
			.apply(document("user-more-info",
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
						fieldWithPath("data.userId").description("사용자 아이디"),
						fieldWithPath("data.normalCartId").description("일반 카트 아이디"),
						fieldWithPath("data.subscriptionCartId").description("구독 카트 아이디"),
						fieldWithPath("createTime").description("카트 생성 시간")
					)
				)
			);
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 탈퇴")
	@Test
	void 회원_탈퇴() throws Exception {

		String token = getToken();

		super.webSpec
			.header("Authorization", token)
			.when()
			.delete("/api/users/{userId}", 1L)
			.then()
			.statusCode(HttpStatus.OK.value())
			.body(Matchers.containsString("USER_WITHDRAWAL"))
			.apply(document("user-withdrawal",
					preprocessRequest(modifyUris()
						.scheme("http")
						.host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
						.removePort(), prettyPrint()
					),
					preprocessResponse(prettyPrint()),
					responseFields(
						fieldWithPath("data").type(JsonFieldType.STRING)
							.description("회원 탈퇴"),
						fieldWithPath("createTime").description("카트 생성 시간")
					)
				)
			);

	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보 수정")
	@Test
	void 회원_정보_수정() throws Exception {

		String token = getToken();
		UserPatchDto userPatchDto = updateUser("test2", "010-1121-1111");

		super.webSpec
			.header("Authorization", token)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(userPatchDto)
			.when()
			.patch("/api/users/{userId}", 1L)
			.then()
			.statusCode(HttpStatus.OK.value())
			.body(containsString(userPatchDto.getEmail()))
			.body(containsString(userPatchDto.getCity()))
			.body(containsString(userPatchDto.getRealName()))
			.body(containsString(userPatchDto.getDetailAddress()))
			.body(containsString(userPatchDto.getPhone()))
			.apply(document("user-update",
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
						fieldWithPath("data.createAt").type(JsonFieldType.STRING).description("회원가입 일"),
						fieldWithPath("createTime").description("카트 생성 시간")
					)
				)
			);

	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보 수정 시 닉네임 중복의 경우 예외가 발생합니다.")
	@Test
	void 정보_수정_닉네임_중복_예외() throws Exception {

		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		String token = getToken();
		UserPatchDto userPatchDto = updateUser("test22", "010-1111-1111");

		super.webSpec
			.header("Authorization", token)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(userPatchDto)
			.when()
			.patch("/api/users/{userId}", 1L)
			.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(containsString("이미 존재하는 닉네임입니다."))
			.apply(document("user-update-error1-dn",
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
			);

	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보 수정 시 연락처 중복의 경우 예외가 발생합니다.")
	@Test
	void 정보_수정_연락처_중복_예외() throws Exception {

		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		String token = getToken();
		UserPatchDto userPatchDto = updateUser("test", "010-1112-1111");

		super.webSpec
			.header("Authorization", token)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(userPatchDto)
			.when()
			.patch("/api/users/{userId}", 1L)
			.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(containsString("이미 존재하는 연락처입니다."))
			.apply(document("user-update-error2-phone",
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
			);
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보를 조회 (회원 가입을 통한 로그인일 경우 social false)")
	@Test
	void 회원_조회() throws Exception {
		String token = getToken();

		super.webSpec
			.header("Authorization", token)
			.when()
			.get("/api/users/{userId}", 1L)
			.then()
			.statusCode(HttpStatus.OK.value())
			.apply(
				document("user-general",
					preprocessRequest(modifyUris()
						.scheme("http")
						.host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
						.removePort(), prettyPrint()
					),
					preprocessResponse(prettyPrint()),
					responseFields(
						fieldWithPath("data.email")
							.type(JsonFieldType.STRING)
							.description("이메일"),
						fieldWithPath("data.city")
							.type(JsonFieldType.STRING)
							.description("도시"),
						fieldWithPath("data.realName")
							.type(JsonFieldType.STRING)
							.description("이름"),
						fieldWithPath("data.detailAddress")
							.type(JsonFieldType.STRING)
							.description("상세 주소"),
						fieldWithPath("data.phone")
							.type(JsonFieldType.STRING)
							.description("연락처"),
						fieldWithPath("data.displayName")
							.type(JsonFieldType.STRING)
							.description("닉네임"),
						fieldWithPath("data.social")
							.type(JsonFieldType.BOOLEAN)
							.description("소셜 로그인(false)"),
						fieldWithPath("data.updatedAt")
							.type(JsonFieldType.STRING)
							.description("최근 수정일"),
						fieldWithPath("data.createAt")
							.type(JsonFieldType.STRING)
							.description("회원가입일"),
						fieldWithPath("createTime")
							.type(JsonFieldType.STRING)
							.description("생성 시간")
					)
				)
			);
	}

	@OAuthAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보를 조회 (oauth를 통한 로그인일 경우 social true)")
	@Test
	void 회원_조회2() throws Exception {

		String token = super.getToken();

		//@formatter:off
        ExtractableResponse<MockMvcResponse> response =
            super.webSpec
                .header("Authorization", token)
                .when()
                .get("/api/users/{userId}", 1L)
                .then()
				.log().all()
                .apply(
                    document("user-general",
                        preprocessRequest(modifyUris()
                            .scheme("http")
                            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
                            .removePort(), prettyPrint()
                        ),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("data.email")
								.type(JsonFieldType.STRING)
								.description("이메일"),
                            fieldWithPath("data.city")
								.type(JsonFieldType.STRING)
								.description("도시"),
                            fieldWithPath("data.realName")
								.type(JsonFieldType.STRING)
								.description("이름"),
                            fieldWithPath("data.detailAddress")
								.type(JsonFieldType.STRING)
                                .description("상세 주소"),
                            fieldWithPath("data.phone")
								.type(JsonFieldType.STRING)
								.description("연락처"),
                            fieldWithPath("data.displayName")
								.type(JsonFieldType.STRING)
                                .description("닉네임"),
                            fieldWithPath("data.social")
								.type(JsonFieldType.BOOLEAN)
                                .description("소셜 로그인(false)"),
                            fieldWithPath("data.updatedAt")
								.type(JsonFieldType.STRING)
                                .description("최근 수정일"),
                            fieldWithPath("data.createAt")
								.type(JsonFieldType.STRING)
                                .description("회원가입일"),
                            fieldWithPath("createTime")
								.type(JsonFieldType.STRING)
								.description("생성 시간")
                        )
                    )
                )
                .extract();
        //@formatter:on

		assertSoftly(soft -> {
			soft.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			soft.assertThat((Boolean)response.jsonPath().getMap("data").get("social")).isTrue();
			soft.assertThat(response.jsonPath().getMap("data").get("email"))
				.isEqualTo("test@test.com");
			soft.assertThat(response.jsonPath().getMap("data").get("displayName"))
				.isEqualTo("test");
			soft.assertThat(response.jsonPath().getMap("data").get("detailAddress"))
				.isEqualTo("압구정동");
			soft.assertThat(response.jsonPath().getMap("data").get("realName"))
				.isEqualTo("name");
			soft.assertThat(response.jsonPath().getMap("data").get("phone"))
				.isEqualTo("010-0000-0000");
			soft.assertThat(response.jsonPath().getMap("data").get("city"))
				.isEqualTo("서울");
		});
	}

	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 로그아웃() throws Exception {

		String token = getToken();

		super.webSpec
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.header("Authorization", token)
			.when()
			.post("/api/users/logout")
			.then()
			.statusCode(HttpStatus.OK.value())
			.apply(
				document("user-logout",
					preprocessRequest(modifyUris().scheme("http")
						.host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
						.removePort(), prettyPrint())
				)
			);
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

		return UserPatchDto.builder()
			.phone(phone)
			.city(city)
			.detailAddress(detailAddress)
			.displayName(displayName)
			.email(email)
			.password(password)
			.realName(realName)
			.build();
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

		return User.builder()
			.displayName(displayName)
			.email(email)
			.password(password)
			.address(new Address(city, detailAddress))
			.realName(realName)
			.phone(phone)
			.oauthId(oauthId)
			.roles(UserRoles.USER)
			.build();
	}

	private void 회원_가입(String displayName, String phone, String mail) {
		UserPostDto postDto = join(mail, displayName, phone);
		UserServicePostDto userServicePostDto = new UserServiceMapper().toUserPost(postDto);
		userService.join(userServicePostDto);
	}
}
