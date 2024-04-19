package com.team33.modulecore.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.team33.modulecore.domain.EnableUserTest;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.order.domain.Address;
import com.team33.modulecore.user.application.DuplicationVerifier;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.domain.UserRoles;
import com.team33.modulecore.user.domain.UserStatus;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserPatchDto;
import com.team33.modulecore.user.dto.UserPostDto;
import com.team33.modulecore.user.dto.UserPostOauthDto;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.dto.UserServicePostDto;
import com.team33.modulecore.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@EnableUserTest
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DuplicationVerifier duplicationVerifier;


    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void 회원가입() throws Exception {
        //given
        UserServicePostDto userServicePostDto = getUserServiceDto();

        //when
        userService.join(userServicePostDto);
        List<User> users = userRepository.findAll();

        //then
        assertThat(users).hasSize(1);
        User user = users.get(0);
        assertThat(user.getEmail()).isEqualTo("test1@gmail.com");
        assertThat(user.getDisplayName()).isEqualTo("test22");
        assertThat(user.getPhone()).isEqualTo("010-1112-1111");
        assertThat(user.getAddress().getCity()).isEqualTo("서울시 부평구 송도동");
        assertThat(user.getRealName()).isEqualTo("홍길동");
        assertThat(user.getAddress().getDetailAddress()).isEqualTo("101 번지");
    }

    private static Stream<Arguments> provideDuplicateUserInfoOnJoin() {
        UserPostDto duplicateEmail = join("test@test.com", "test", "010-0000-0000");
        UserPostDto duplicateDisplayName = join("test1@test1.com", "test1", "010-0000-0000");
        UserPostDto duplicatePhone = join("test2@test.com", "test2", "010-0000-0001");

        return Stream.of(
            Arguments.arguments(UserServicePostDto.to(duplicateEmail)),
            Arguments.arguments(UserServicePostDto.to(duplicateDisplayName)),
            Arguments.arguments(UserServicePostDto.to(duplicatePhone))
        );
    }

    @DisplayName("이메일, 닉네임, 전화번호가 중복될 시 예외를 던진다.")
    @ParameterizedTest
    @MethodSource("provideDuplicateUserInfoOnJoin")
    void 회원_가입_중복_예외(UserServicePostDto userPostDto) throws Exception {
        //given
        UserPostDto test1 = join("test@test.com", "test1", "010-0000-0001");
        UserServicePostDto userServicePostDto = UserServicePostDto.to(test1);
        userService.join(userServicePostDto);
        //then
        assertThatThrownBy(() -> userService.join(userPostDto))
            .isInstanceOf(BusinessLogicException.class);
    }

    @DisplayName("소셜로그인 시 추가 정보를 기입하면 회원정보가 업데이트된다.")
    @Test
    void 추가_정보_기입() throws Exception {
        //given
        User user = User.builder().realName("testset").email("test@gmail.com").roles(UserRoles.USER)
            .build();
        userRepository.save(user);
        OAuthUserServiceDto oAuthUserServiceDto = OAuthUserServiceDto.to(oauthJoin());

        //when
        User result = userService.addOAuthInfo(oAuthUserServiceDto);

        //then
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
        assertThat(result.getAddress()).isEqualTo(oAuthUserServiceDto.getAddress());
        assertThat(result.getDisplayName()).isEqualTo(oAuthUserServiceDto.getDisplayName());
        assertThat(result.getPhone()).isEqualTo(oAuthUserServiceDto.getPhone());
    }

    private static Stream<Arguments> provideDuplicateUserInfoOnOauth() {
        UserPostOauthDto duplicatePhone = UserPostOauthDto.builder()
            .city("서울")
            .detailAddress("한국아파트")
            .email("test5@test.com")
            .displayName("test")
            .phone("010-0000-0000")
            .build();

        UserPostOauthDto duplicateDisplayName =
            UserPostOauthDto.builder()
                .city("서울")
                .detailAddress("한국아파트")
                .email("test4@test.com")
                .displayName("test1")
                .phone("010-0000-0001")
                .build();

        return Stream.of(
            Arguments.arguments(duplicatePhone),
            Arguments.arguments(duplicateDisplayName)
        );
    }

    @DisplayName("소셜 회원 주가정보 기입시 전화번호, 닉네임에 중복이 있을 경우 예외를 던진다.")
    @ParameterizedTest
    @MethodSource("provideDuplicateUserInfoOnOauth")
    void 소셜_회원_중복(UserPostOauthDto oauthDto) throws Exception {
        //given
        UserPostDto test1 = join("test@test.com", "test1", "010-0000-0000");
        UserServicePostDto userServicePostDto = UserServicePostDto.to(test1);
        userService.join(userServicePostDto);
        OAuthUserServiceDto oAuthUserServiceDto = OAuthUserServiceDto.to(oauthDto);
        //when then
        assertThatThrownBy(() ->  userService.addOAuthInfo(oAuthUserServiceDto) )
        	.isInstanceOf(BusinessLogicException.class)
        	.hasMessageContaining("");
    }

    @DisplayName("회원 탈퇴시 회원 상태가 withdrawal로 변경된다.")
    @Test
    void 회원_탈퇴() throws Exception {

        //given
        UserServicePostDto userServicePostDto = getUserServiceDto();
        long userId = userService.join(userServicePostDto);
        //when
        User user = userService.deleteUser(userId);
        //then
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.USER_WITHDRAWAL);
    }

    @DisplayName("회원 정보를 조회할 수 있다.")
    @Test
    void 회원_정보_조회() throws Exception {
        //given
        UserServicePostDto userServicePostDto = getUserServiceDto();
        long userId = userService.join(userServicePostDto);

        //when
        User loginUser1 = userService.getLoginUser1(userId);
        //then
        User userOptional = userRepository.findById(userId).orElse(null);
        assertThat(loginUser1).isEqualTo(userOptional);
    }

    @DisplayName("회원 정보 수정를 수정할 수 있다.")
    @Test
    void 회원_정보_수정() throws Exception {
        //given
        UserServicePostDto userServicePostDto = getUserServiceDto();
        long userId = userService.join(userServicePostDto);

        UserPatchDto userPatchDto = UserPatchDto.builder()
            .city("인천 부평구")
            .password("sdfsdfe!1")
            .detailAddress("한국아파트 101")
            .phone("010-0000-000")
            .realName("홍홍홍")
            .build();

        UserServicePatchDto userServicePatchDto = UserServicePatchDto.to(userPatchDto);

        //when
        User user = userService.updateUser(userServicePatchDto, userId);

        //then
        assertThat(user.getAddress())
            .isEqualTo(new Address(userPatchDto.getCity(), userPatchDto.getDetailAddress()));
        assertThat(user.getRealName()).isEqualTo("홍홍홍");
        assertThat(user.getPhone()).isEqualTo("010-0000-000");
    }

    private UserServicePostDto getUserServiceDto() {
        UserPostDto postDto = join("test1@gmail.com", "test22", "010-1112-1111");
        return UserServicePostDto.to(postDto);
    }

    private static UserPostDto join(String email, String displayName, String phone) {

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

        return UserPostOauthDto.builder()
            .phone(phone)
            .detailAddress(detailAddress)
            .city(city)
            .email(email)
            .displayName(displayName)
            .build();
    }

}