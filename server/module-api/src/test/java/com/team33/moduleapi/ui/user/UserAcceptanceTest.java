package com.team33.moduleapi.ui.user;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.mockuser.UserAccount;
import com.team33.moduleapi.security.dto.LoginDto;
import com.team33.moduleapi.ui.user.dto.UserPatchDto;
import com.team33.moduleapi.ui.user.dto.UserPostDto;
import com.team33.moduleapi.ui.user.dto.UserPostOauthDto;
import com.team33.moduleapi.ui.user.mapper.UserServiceMapper;
import com.team33.modulecore.core.user.application.UserService;
import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.user.domain.UserRoles;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.dto.UserServicePostDto;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

/**
 * {@Code @UserAccount}는 회원가입을 하여 security Context에 정보를 저장합니다.
 */
class UserAcceptanceTest extends ApiTest {

	@Autowired
	private UserService userService;

	@DisplayName("회원 가입 시 userId, normalCartId, subscriptionCartId를 api 응답합니다.")
	@Test
	void 회원가입() throws Exception {
		//given
		UserPostDto joinDto = join("test@gmail.com", "test1", "010-1111-1111");

		given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(joinDto)
			.when()
			.post("/users")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body("data.userId", equalTo(1))
			.body("data.normalCartId", equalTo(1))
			.body("data.subscriptionCartId", equalTo(2));
	}

	@DisplayName("회원 가입 시 닉네임 중복의 경우 예외가 발생합니다.")
	@Test
	void 회원가입_닉네임_중복() throws Exception {
		회원_가입("test","010-1111-000", "test@gmail.com");
		UserPostDto dto = join("teset2@gmail.com", "test", "010-1111-1111");

		//@formatter:off
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(dto)
        .when()
                .post("/users")
        .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString("이미 존재하는 닉네임입니다."));
        //@formatter:on
	}

	@DisplayName("회원 가입 시 연락처가 중복될 경우 예외가 발생합니다. ")
	@Test
	void 회원가입_연락처_중복() throws Exception {
		회원_가입("test","010-0000-0000", "test@gmail.com");
		UserPostDto dto2 = join("test1@gmail.com", "test112", "010-0000-0000");

		given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto2)
			.when()
			.post("/users")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(containsString("이미 존재하는 연락처입니다."));
	}

	@DisplayName("oauth 로그인 시 추가 정보를 기입하면, 토큰이 발급됩니다.")
	@Test
	void oauth_토큰_발행() throws Exception {
		User user = User.builder().email("test@gmail.com").roles(UserRoles.USER).build();
		userRepository.save(user);

		UserPostOauthDto dto = oauthJoin();

		given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto)
			.when()
			.post("/users/more-info")
			.then()
			.statusCode(HttpStatus.CREATED.value())
			.header("Authorization", notNullValue())
			.body("data.userId", equalTo(1))
			.body("data.normalCartId", equalTo(1))
			.body("data.subscriptionCartId", equalTo(2))
			.log()
			.all();
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 탈퇴 시 상태가 변경됩니다.")
	@Test
	void 회원_탈퇴() throws Exception {

		String token = getToken();

		//@formatter:off
            given()
                    .log().all()
                    .header("Authorization", token)
            .when()
                    .delete("/users/1")
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(containsString("USER_WITHDRAWAL"))
                    .log().all();
        //@formatter:on
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보를 수정하고 결과를 api 반환합니다.")
	@Test
	void 회원_정보_수정() throws Exception {

		String token = getToken();
		UserPatchDto userPatchDto = updateUser("test2", "010-1121-1111");

		given()
			.log().all()
			.header("Authorization", token)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(userPatchDto)
			.when()
			.patch("/users/1")
			.then()
			.statusCode(HttpStatus.OK.value())
			.body(containsString(userPatchDto.getEmail()))
			.body(containsString(userPatchDto.getEmail()))
			.body(containsString(userPatchDto.getCity()))
			.body(containsString(userPatchDto.getDetailAddress()))
			.body(containsString(userPatchDto.getPhone()))
			.log().all();
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보 수정 시 닉네임 중복의 경우 예외가 발생합니다.")
	@Test
	void 정보_수정_닉네임_중복_예외() throws Exception {

		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		String token = getToken();
		UserPatchDto userPatchDto = updateUser("test22", "010-1111-1111");

		given()
			.log().all()
			.header("Authorization", token)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(userPatchDto)
			.when()
			.patch("/users/1")
			.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(containsString("이미 존재하는 닉네임입니다."))
			.log().all();
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보 수정 시 연락처 중복의 경우 예외가 발생합니다.")
	@Test
	void 정보_수정_연락처_중복_예외() throws Exception {

		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		String token = getToken();
		UserPatchDto userPatchDto = updateUser("test1", "010-1112-1111");

		//@formatter:off
            given()
                    .log().all()
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(userPatchDto)
            .when()
                    .patch("/users/1")
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(containsString("이미 존재하는 연락처입니다."))
                    .log().all();
        //@formatter:on
	}

	@UserAccount({"test", "010-0000-0000"})
	@DisplayName("회원 정보를 조회 (회원 가입을 통한 로그인일 경우 social false)")
	@Test
	void 회원_조회() throws Exception {

		String token = getToken();

		given()
			.log().all()
			.header("Authorization", token)
			.when()
			.get("/users/1")
			.then()
			.statusCode(HttpStatus.OK.value())
			.body("data.email", equalTo("test@test.com"))
			.body("data.displayName", equalTo("test"))
			.body("data.city", equalTo("서울"))
			.body("data.detailAddress", equalTo("압구정동"))
			.body("data.realName", equalTo("name"))
			.body("data.phone", equalTo("010-0000-0000"))
			.body("data.social", equalTo(false))
			.log().all();
	}

	@DisplayName("회원 정보를 조회 (oauth를 통한 로그인일 경우 social true)")
	@Test
	void 회원_조회2() throws Exception {
		userRepository.save(oauthUser());
		String token = "Bearer " + jwtTokenProvider.delegateAccessToken(oauthUser());

		given()
			.log().all()
			.header("Authorization", token)
			.when()
			.get("/users/1")
			.then()
			.body("data.email", equalTo("test@test.com"))
			.body("data.displayName", equalTo("test"))
			.body("data.city", equalTo("서울"))
			.body("data.detailAddress", equalTo("압구정동"))
			.body("data.realName", equalTo("name"))
			.body("data.phone", equalTo("010-0000-0000"))
			.body("data.social", equalTo(true))
			.log().all();
	}

	@Test
	void 로그인() throws Exception {

		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		LoginDto dto = LoginDto.builder().username("test@gmail.com").password("sdfsdfe!1").build();

		ExtractableResponse<Response> response =
			given()
				.log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(dto)
				.when()
				.post("/users/auth")
				.then()
				.statusCode(HttpStatus.OK.value())
				.header("Authorization", notNullValue())
				.log().all().extract();

	}

	@UserAccount({"test", "010-0000-0000"})
	@Test
	void 로그아웃() throws Exception {

		String token = getToken();

		given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.header("Authorization", token)
			.when()
			.post("/users/logout")
			.then()
			.statusCode(HttpStatus.OK.value())
			.log().all().extract();
	}

	@DisplayName("비밀번호 오류로 인한 로그인 실패")
	@Test
	void 로그인_오류1() throws Exception {
		//given
		회원_가입("test22", "010-1112-1111", "test1@gmail.com");

		LoginDto dto = LoginDto.builder().username("test1@gmail.com").password("sdfsd1").build();

		given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto)
			.when()
			.post("/users/auth")
			.then()
			.statusCode(HttpStatus.UNAUTHORIZED.value())
			.body("status", equalTo(401))
			.body("message", Matchers.in(new String[] {"자격 증명에 실패하였습니다.", "Bad credentials"}))
			.log().all();
	}

	@DisplayName("이메일 오류로 인한 로그인 실패")
	@Test
	void 로그인_오류2() throws Exception {
		//given
		회원_가입("test22", "010-1112-1111", "test1@gmail.com");

		LoginDto dto = LoginDto.builder().username("test31@gmail.com").password("sdfsdfe!1")
			.build();

		given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(dto)
			.when()
			.post("/users/auth")
			.then()
			.statusCode(HttpStatus.UNAUTHORIZED.value())
			.body("status", equalTo(401))
			.body("message", Matchers.in(new String[] {"자격 증명에 실패하였습니다.", "Bad credentials"}))
			.log().all();
	}

	@DisplayName("회원 가입 시 이메일 중복의 경우 예외가 발생합니다.")
	@Test
	void 회원가입_이메일_중복() throws Exception {
		회원_가입("test22", "010-1112-1111", "test@gmail.com");

		UserPostDto dto2 = join("test@gmail.com", "test112", "010-1111-1111");

        given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(dto2)
            .when()
                    .post("/users")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(containsString("이미 가입한 e-mail입니다."));
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
		String password = "1231asd!!23";
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

	private void 회원_가입(String displayName, String phone, String mail) {
		UserPostDto postDto = join(mail, displayName, phone);
		UserServicePostDto userServicePostDto = new UserServiceMapper().toUserPost(postDto);
		userService.join(userServicePostDto);
	}
}
