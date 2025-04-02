package com.team33.modulecore.core.user.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.team33.modulecore.core.cart.application.CartService;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.user.domain.UserStatus;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.core.user.dto.UserServicePatchDto;
import com.team33.modulecore.core.user.dto.UserServicePostDto;
import com.team33.modulecore.core.user.mock.FakeUserRepository;

class UserServiceTest {

	@DisplayName("회원가입을 할 수 있다.")
	@Test
	void 회원가입() throws Exception {
		//given
		PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
		DuplicationVerifier duplicationVerifier = mock(DuplicationVerifier.class);
		CartService cartService = mock(CartService.class);

		doNothing().when(duplicationVerifier).checkUserInfo(any(UserServicePostDto.class));
		when(passwordEncoder.encode(anyString())).thenReturn("password");
		when(cartService.createNormalCart()).thenReturn(1L);
		when(cartService.createSubsCart()).thenReturn(2L);

		UserServicePostDto userServicePostDto = getUserServicePostDto("test1@gmail.com", "test22", "010-1112-1111");

		UserService userService = new UserService(
			new FakeUserRepository(),
			cartService,
			passwordEncoder,
			duplicationVerifier
		);

		//when
		User user1 = userService.join(userServicePostDto);

		//then
		assertThat(user1.getEmail()).isEqualTo("test1@gmail.com");
		assertThat(user1.getDisplayName()).isEqualTo("test22");
		assertThat(user1.getPhone()).isEqualTo("010-1112-1111");

		verify(passwordEncoder, times(1)).encode(anyString());
		verify(duplicationVerifier, times(1)).checkUserInfo(any(UserServicePostDto.class));
		verify(cartService, times(1)).createNormalCart();
		verify(cartService, times(1)).createSubsCart();
	}

	@DisplayName("이메일, 닉네임, 전화번호가 중복될 시 예외를 던진다.")
	@ParameterizedTest(name = "{index} => {0}")
	@MethodSource("provideDuplicateUserInfoOnJoinDto")
	void 회원_가입_중복_예외(String reason, UserServicePostDto userPostDto) throws Exception {
		//given
		DuplicationVerifier duplicationVerifier = new DuplicationVerifier(new FakeUserRepository());
		UserService userService = new UserService(null, null, null, duplicationVerifier);
		//then
		assertThatThrownBy(() -> userService.join(userPostDto))
			.isInstanceOf(BusinessLogicException.class);
	}

	@DisplayName("소셜로그인 시 추가 정보를 기입하면 회원정보가 업데이트된다.")
	@Test
	void 추가_정보_기입() throws Exception {
		//given
		OAuthUserServiceDto oAuthUserServiceDto = OAuthUserServiceDto.builder()
			.address(new Address("서울시 부평구 송도동", "101 번지"))
			.displayName("test")
			.email("test1@gmail.com")
			.phone("010-0000-0000")
			.build();

		DuplicationVerifier duplicationVerifier = mock(DuplicationVerifier.class);
		CartService cartService = mock(CartService.class);

		when(cartService.createNormalCart()).thenReturn(1L);
		when(cartService.createSubsCart()).thenReturn(2L);
		doNothing().when(duplicationVerifier).checkOauthAdditionalInfo(any(OAuthUserServiceDto.class));

		UserService userService = new UserService(
			new FakeUserRepository(),
			cartService,
			null,
			duplicationVerifier
		);

		//when
		User result = userService.addOAuthInfo(oAuthUserServiceDto);

		//then
		assertThat(result.getEmail()).isEqualTo("test1@gmail.com");
		assertThat(result.getAddress()).isEqualTo(oAuthUserServiceDto.getAddress());
		assertThat(result.getDisplayName()).isEqualTo(oAuthUserServiceDto.getDisplayName());
		assertThat(result.getPhone()).isEqualTo(oAuthUserServiceDto.getPhone());

		verify(cartService, times(1)).createNormalCart();
		verify(cartService, times(1)).createSubsCart();
		verify(duplicationVerifier, times(1)).checkOauthAdditionalInfo(any(OAuthUserServiceDto.class));
	}

	@DisplayName("소셜 회원 주가정보 기입시 전화번호, 닉네임에 중복이 있을 경우 예외를 던진다.")
	@ParameterizedTest(name = "{index} => {0}")
	@MethodSource("provideDuplicateUserInfoOnOauth")
	void 소셜_회원_중복(String reason, OAuthUserServiceDto oauthDto) throws Exception {
		//given
		DuplicationVerifier duplicationVerifier = new DuplicationVerifier(new FakeUserRepository());
		UserService userService = new UserService(null, null, null, duplicationVerifier);

		//when then
		assertThatThrownBy(() -> userService.addOAuthInfo(oauthDto))
			.isInstanceOf(BusinessLogicException.class);
	}

	@DisplayName("회원 탈퇴시 회원 상태가 withdrawal로 변경된다.")
	@Test
	void 회원_탈퇴() throws Exception {
		//given
		CartService cartService = mock(CartService.class);
		doNothing().when(cartService).deleteCart(anyLong(), anyLong());

		UserService userService = new UserService(new FakeUserRepository(), cartService, null, null);

		//when
		User user = userService.deleteUser(1L);

		//then
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.USER_WITHDRAWAL);
		assertThat(user.getSubscriptionCartId()).isNull();
		assertThat(user.getNormalCartId()).isNull();

		verify(cartService, times(1)).deleteCart(anyLong(), anyLong());
	}

	@DisplayName("회원 정보 수정를 수정할 수 있다.")
	@Test
	void 회원_정보_수정() throws Exception {
		//given
		UserServicePatchDto userServicePatchDto = UserServicePatchDto.builder()
			.address(new Address("서울시 부평구 송도동", "101 번지"))
			.displayName("test")
			.phone("010-0000-0000")
			.realName("홍홍홍")
			.password("sdfsdfe!1")
			.build();

		DuplicationVerifier duplicationVerifier = mock(DuplicationVerifier.class);
		PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

		doNothing().when(duplicationVerifier).checkDuplicationOnUpdate(any(UserServicePatchDto.class), any(User.class));
		when(passwordEncoder.encode(anyString())).thenReturn("password");

		UserService userService = new UserService(new FakeUserRepository(), null, passwordEncoder, duplicationVerifier);

		//when
		User user = userService.updateUser(userServicePatchDto, 1L);

		//then
		assertThat(user.getAddress())
			.isEqualTo(new Address("서울시 부평구 송도동", "101 번지"));
		assertThat(user.getRealName()).isEqualTo("홍홍홍");
		assertThat(user.getPhone()).isEqualTo("010-0000-0000");
		assertThat(user.getPassword()).isEqualTo("password");

		verify(duplicationVerifier, times(1))
			.checkDuplicationOnUpdate(any(UserServicePatchDto.class), any(User.class));
		verify(passwordEncoder, times(1))
			.encode(anyString());
	}

	@DisplayName("리뷰아이디를 추가할 수 있다.")
	@Test
	void 리뷰_추가() throws Exception {
		//given
		FakeUserRepository userRepository = new FakeUserRepository();
		UserService userService = new UserService(userRepository, null, null, null);

		//when
		userService.addReviewId(1L, 1L);

		//then
		User user = userRepository.findById(1L).orElse(null);

		assertThat(user.getReviewIds()).hasSize(1)
			.containsOnly(1L);
	}

	@DisplayName("리뷰 아이디를 삭제할 수 있다.")
	@Test
	void 리뷰_삭제() throws Exception {
		//given
		FakeUserRepository userRepository = new FakeUserRepository();
		UserService userService = new UserService(userRepository, null, null, null);

		//when
		userService.deleteReviewId(1L, 1L);

		//then
		User user = userRepository.findById(1L).orElse(null);

		assertThat(user.getReviewIds()).hasSize(0);
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
			Arguments.arguments("전화번호 중복", duplicatePhone),
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
}