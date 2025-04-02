package com.team33.modulecore.core.user.application;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;
import com.team33.modulecore.core.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.core.user.dto.UserServicePatchDto;
import com.team33.modulecore.core.user.dto.UserServicePostDto;
import com.team33.modulecore.core.user.mock.FakeUserRepository;

class DuplicationVerifierTest {

	@DisplayName("회원 가입 시, 유저 정보가 중복되면 예외를 던진다.")
	@ParameterizedTest(name = "{index} ==> {0}")
	@MethodSource("provideDuplicateUserInfoOnJoinDto")
	void 유저_정보_중복_확인(String reason, UserServicePostDto dto) throws Exception {
		//given
		DuplicationVerifier duplicationVerifier = new DuplicationVerifier(new FakeUserRepository());

		//when//then
		assertThatThrownBy(() -> duplicationVerifier.checkUserInfo(dto))
			.describedAs(reason)
			.isInstanceOf(BusinessLogicException.class);
	}

	@DisplayName("추가 정보 기입 유저 중복을 검증할 수 있다.")
	@ParameterizedTest(name = "{index} ==> {0}")
	@MethodSource("provideDuplicateUserInfoOnOauth")
	void 유저_정보_중복_확인(String reason, OAuthUserServiceDto dto) throws Exception {
		//given
		DuplicationVerifier duplicationVerifier = new DuplicationVerifier(new FakeUserRepository());

		//when//then
		assertThatThrownBy(() -> duplicationVerifier.checkOauthAdditionalInfo(dto)).as(reason)
			.isInstanceOf(BusinessLogicException.class);
	}

	private static Stream<Arguments> provideDuplicateUserInfoOnJoinDto() {
		var duplicateEmail = getUserServicePostDto("test1@gmail.com", "test", "010-0000-0000");
		var duplicateDisplayName = getUserServicePostDto("test1@test1.com", "test22", "010-0000-0000");
		var duplicatePhone = getUserServicePostDto("test2@test.com", "test2", "010-1112-1111");

		return Stream.of(
			Arguments.arguments("이메일 중복", duplicateEmail),
			Arguments.arguments("닉네임 중복", duplicateDisplayName),
			Arguments.arguments("연락처 중복", duplicatePhone)
		);
	}

	private static Stream<Arguments> provideDuplicateUserInfoOnOauth() {
		OAuthUserServiceDto duplicatePhone = OAuthUserServiceDto.builder()
			.address(new Address("서울시 부평구 송도동", "101 번지"))
			.displayName("test")
			.email("test1@gmail.com")
			.phone("010-1112-1111")
			.build();

		OAuthUserServiceDto duplicateDisplayName = OAuthUserServiceDto.builder()
			.address(new Address("서울시 부평구 송도동", "101 번지"))
			.displayName("test22")
			.email("test1@gmail.com")
			.phone("010-0000-0000")
			.build();

		return Stream.of(
			Arguments.arguments("연락처 중복", duplicatePhone),
			Arguments.arguments("닉네임 중복", duplicateDisplayName)
		);
	}

	private static UserServicePostDto getUserServicePostDto(String email, String displayName, String phone) {
		return UserServicePostDto.builder()
			.email(email)
			.displayName(displayName)
			.phone(phone)
			.password("1234")
			.address(new Address("서울시 부평구 송도동", "101 번지"))
			.realName("홍길동")
			.build();
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@DisplayName("유저 정보 수정 시 닉네임, 연락처 중복 검증")
	class PatchChcek {

		private UserRepository userRepository;

		@BeforeAll
		void beforeAll() {
			userRepository = new FakeUserRepository();
			User user = FixtureMonkeyFactory.get().giveMeBuilder(User.class)
				.set("id", 1L)
				.set("email", "test2@gmail.com")
				.set("displayName", "test11")
				.set("phone", "010-1111-1111")
				.set("password", "password")
				.set("address", new Address("서울시 부평구 송도동", "101 번지"))
				.set("realName", "홍길순")
				.set("cartId", 2L)
				.set("reviewIds", new ArrayList<>())
				.sample();

			userRepository.save(user);
		}

		@DisplayName("다른 유저의 정보와 중복되는 경우")
		@ParameterizedTest(name = "{index} ==> {0}")
		@MethodSource("provideDuplicateUserInfoOnPatch")
		void 회원_정보_수정_중복_확인(String reason, UserServicePatchDto userServicePatchDto) throws Exception {

			//given
			DuplicationVerifier duplicationVerifier = new DuplicationVerifier(userRepository);
			User user = userRepository.findById(1L).orElse(null);

			//when//then
			assertThatThrownBy(() ->
				duplicationVerifier.checkDuplicationOnUpdate(userServicePatchDto, user)
			).isInstanceOf(BusinessLogicException.class);
		}

		private Stream<Arguments> provideDuplicateUserInfoOnPatch() {
			var duplicateDisplayName = UserServicePatchDto.builder()
				.address(new Address("서울시 부평구 송도동", "101 번지"))
				.displayName("test11")
				.phone("010-1112-1111")
				.realName("홍홍홍")
				.password("sdfsdfe!1")
				.build();

			var duplicatePhone = UserServicePatchDto.builder()
				.address(new Address("서울시 부평구 송도동", "101 번지"))
				.displayName("test22")
				.phone("010-1111-1111")
				.realName("홍홍홍")
				.password("sdfsdfe!1")
				.build();

			return Stream.of(
				Arguments.arguments("닉네임 중복", duplicateDisplayName),
				Arguments.arguments("연락처 중복", duplicatePhone)
			);
		}

	}
}