package com.team33.modulecore.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.cart.application.CartService;
import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.order.domain.Address;
import com.team33.modulecore.user.FakeUserRepository;
import com.team33.modulecore.user.application.DuplicationVerifier;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.dto.UserServicePostDto;

class UserServiceTest1 {

	@DisplayName("회원가입을 할 수 있다.")
	@Test
	void 회원가입() throws Exception {
		//given
		PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
		DuplicationVerifier duplicationVerifier = mock(DuplicationVerifier.class);
		CartService cartService = mock(CartService.class);

		Cart cart = FixtureMonkeyFactory.get()
			.giveMeBuilder(Cart.class)
			.set("id", 1L)
			.set("subscriptionCartItems", new HashSet<>())
			.set("normalCartItems", new HashSet<>())
			.sample();

		doNothing().when(duplicationVerifier).checkUserInfo(any(UserServicePostDto.class));
		when(passwordEncoder.encode(anyString())).thenReturn("password");
		when(cartService.create()).thenReturn(cart);

		UserServicePostDto userServicePostDto = getUserServicePostDto("test1@gmail.com", "test22", "010-1112-1111");

		UserService userService = new UserService(new FakeUserRepository(), cartService, passwordEncoder,
			duplicationVerifier);

		//when
		User user1 = userService.join(userServicePostDto);

		//then
		assertThat(user1.getEmail()).isEqualTo("test1@gmail.com");
		assertThat(user1.getDisplayName()).isEqualTo("test22");
		assertThat(user1.getPhone()).isEqualTo("010-1112-1111");

		verify(passwordEncoder, times(1)).encode(anyString());
		verify(duplicationVerifier, times(1)).checkUserInfo(any(UserServicePostDto.class));
		verify(cartService, times(1)).create();
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

	private static Stream<Arguments> provideDuplicateUserInfoOnJoinDto() {
		var duplicateEmail = getUserServicePostDto("test1@gmail.com", "test", "010-0000-0000");
		var duplicateDisplayName = getUserServicePostDto("test1@test1.com", "test22", "010-0000-0000");
		var duplicatePhone = getUserServicePostDto("test2@test.com", "test2", "010-1112-1111");

		return Stream.of(
			Arguments.arguments(duplicateEmail),
			Arguments.arguments(duplicateDisplayName),
			Arguments.arguments(duplicatePhone)
		);
	}

	@DisplayName("이메일, 닉네임, 전화번호가 중복될 시 예외를 던진다.")
	@ParameterizedTest
	@MethodSource("provideDuplicateUserInfoOnJoinDto")
	void 회원_가입_중복_예외(UserServicePostDto userPostDto) throws Exception {
		//given
		DuplicationVerifier duplicationVerifier = new DuplicationVerifier(new FakeUserRepository());
		UserService userService = new UserService(null, null, null, duplicationVerifier);
		//then
		assertThatThrownBy(() -> userService.join(userPostDto))
			.isInstanceOf(BusinessLogicException.class);
	}
	//
	// @DisplayName("소셜로그인 시 추가 정보를 기입하면 회원정보가 업데이트된다.")
	// @Test
	// void 추가_정보_기입() throws Exception {
	//     //given
	//     User user = User.builder()
	//         .realName("testset")
	//         .email("test@gmail.com")
	//         .roles(UserRoles.USER)
	//         .build();
	//
	//     userRepository.save(user);
	//     var oAuthUserServiceDto = OAuthUserServiceDto.to(oauthJoinDto());
	//
	//     //when
	//     User result = userService.addOAuthInfo(oAuthUserServiceDto);
	//
	//     //then
	//     assertThat(result.getEmail()).isEqualTo("test@gmail.com");
	//     assertThat(result.getAddress()).isEqualTo(oAuthUserServiceDto.getAddress());
	//     assertThat(result.getDisplayName()).isEqualTo(oAuthUserServiceDto.getDisplayName());
	//     assertThat(result.getPhone()).isEqualTo(oAuthUserServiceDto.getPhone());
	// }
	//
	// private static Stream<Arguments> provideDuplicateUserInfoOnOauth() {
	//     var duplicatePhone = UserPostOauthDto.builder()
	//         .city("서울")
	//         .detailAddress("한국아파트")
	//         .email("test5@test.com")
	//         .displayName("test")
	//         .phone("010-0000-0000")
	//         .build();
	//
	//     var duplicateDisplayName =
	//         UserPostOauthDto.builder()
	//             .city("서울")
	//             .detailAddress("한국아파트")
	//             .email("test4@test.com")
	//             .displayName("test1")
	//             .phone("010-0000-0001")
	//             .build();
	//
	//     return Stream.of(
	//         Arguments.arguments(duplicatePhone),
	//         Arguments.arguments(duplicateDisplayName)
	//     );
	// }
	//
	// @DisplayName("소셜 회원 주가정보 기입시 전화번호, 닉네임에 중복이 있을 경우 예외를 던진다.")
	// @ParameterizedTest
	// @MethodSource("provideDuplicateUserInfoOnOauth")
	// void 소셜_회원_중복(UserPostOauthDto oauthDto) throws Exception {
	//     //given
	//     var oAuthUserServiceDto = OAuthUserServiceDto.to(oauthDto);
	//     //when then
	//     assertThatThrownBy(() ->  userService.addOAuthInfo(oAuthUserServiceDto) )
	//     	.isInstanceOf(BusinessLogicException.class);
	// }
	//
	// @DisplayName("회원 탈퇴시 회원 상태가 withdrawal로 변경된다.")
	// @Test
	// void 회원_탈퇴() throws Exception {
	//     //given
	//     var userServicePostDto = getUserServiceDto();
	//     long userId = userService.join(userServicePostDto);
	//
	//     //when
	//     User user = userService.deleteUser(userId);
	//
	//     //then
	//     assertThat(user.getUserStatus()).isEqualTo(UserStatus.USER_WITHDRAWAL);
	// }
	//
	// @DisplayName("회원 정보를 조회할 수 있다.")
	// @Test
	// void 회원_정보_조회() throws Exception {
	//     //given
	//     var userServicePostDto = getUserServiceDto();
	//     long userId = userService.join(userServicePostDto);
	//
	//     //when
	//     User loginUser1 = userService.getLoginUser1(userId);
	//     //then
	//     User userOptional = userRepository.findById(userId).orElse(null);
	//     assertThat(loginUser1.getId()).isEqualTo(userOptional.getId());
	// }
	//
	// @DisplayName("회원 정보 수정를 수정할 수 있다.")
	// @Test
	// void 회원_정보_수정() throws Exception {
	//     //given
	//     var userServicePostDto = getUserServiceDto();
	//     long userId = userService.join(userServicePostDto);
	//
	//     UserPatchDto userPatchDto = UserPatchDto.builder()
	//         .city("인천 부평구")
	//         .password("sdfsdfe!1")
	//         .detailAddress("한국아파트 101")
	//         .phone("010-0000-000")
	//         .realName("홍홍홍")
	//         .build();
	//
	//     var userServicePatchDto = UserServicePatchDto.to(userPatchDto);
	//
	//     //when
	//     User user = userService.updateUser(userServicePatchDto, userId);
	//
	//     //then
	//     assertThat(user.getAddress())
	//         .isEqualTo(new Address(userPatchDto.getCity(), userPatchDto.getDetailAddress()));
	//     assertThat(user.getRealName()).isEqualTo("홍홍홍");
	//     assertThat(user.getPhone()).isEqualTo("010-0000-000");
	// }
	//
	// private UserServicePostDto getUserServiceDto() {
	//     var postDto = joinDto("test1@gmail.com", "test22", "010-1112-1111");
	//     return UserServicePostDto.to(postDto);
	// }
	//
	// private static UserPostDto joinDto(String email, String displayName, String phone) {
	//
	//     String password = "sdfsdfe!1";
	//     String city = "서울시 부평구 송도동";
	//     String detailAddress = "101 번지";
	//     String realName = "홍길동";
	//
	//     return UserPostDto.builder()
	//         .detailAddress(detailAddress)
	//         .city(city)
	//         .email(email)
	//         .phone(phone)
	//         .realName(realName)
	//         .password(password)
	//         .displayName(displayName)
	//         .build();
	// }
	//
	// private UserPostOauthDto oauthJoinDto() {
	//
	//     String email = "test@gmail.com";
	//     String displayName = "test2";
	//     String city = "서울";
	//     String detailAddress = "압구정동";
	//     String phone = "010-3333-333";
	//
	//     return UserPostOauthDto.builder()
	//         .phone(phone)
	//         .detailAddress(detailAddress)
	//         .city(city)
	//         .email(email)
	//         .displayName(displayName)
	//         .build();
	// }
}