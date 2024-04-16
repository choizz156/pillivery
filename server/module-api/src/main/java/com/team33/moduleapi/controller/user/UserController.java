package com.team33.moduleapi.controller.user;


import com.team33.moduleapi.security.jwt.JwtTokenProvider;
import com.team33.moduleapi.security.jwt.Logout;
import com.team33.moduleapi.security.refreshtoken.ResponseTokenService;
import com.team33.modulecore.domain.user.UserServiceDto;
import com.team33.modulecore.domain.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.domain.user.dto.UserPostDto;
import com.team33.modulecore.domain.user.dto.UserPostOauthDto;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.service.UserService;
import com.team33.modulecore.global.response.SingleResponseDto;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final Logout logout;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseTokenService responseTokenService;
    private static final String JOIN_COMPLETE = "회원 가입 완료";
    private static final String LOGOUT_COMPLETE = "로그아웃 완료";
    private static final String OAUTH_JOIN_COMPLETE = "소셜 회원 추가 정보 기입 완료";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponseDto<String> singUpUser(@Valid @RequestBody UserPostDto userPostDto) {

        UserServiceDto userServiceDto = UserServiceDto.to(userPostDto);
        userService.join(userServiceDto);
        return new SingleResponseDto<>(JOIN_COMPLETE);
    }

    @PostMapping("/more-info")
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponseDto<String> moreInfo(
        @Valid @RequestBody UserPostOauthDto userDto,
        HttpServletResponse response
    ) {
        OAuthUserServiceDto oAuthUserServiceDto = OAuthUserServiceDto.to(userDto);
        User user = userService.addOAuthInfo(oAuthUserServiceDto);
        responseTokenService.delegateToken(response, user);

        return new SingleResponseDto<>(OAUTH_JOIN_COMPLETE);
    }

//    @PatchMapping
//    public SingleResponseDto<UserResponse> updateInfo(@Valid @RequestBody UserPatchDto userDto) {
//        User user = userService.updateUser(userDto);
//        return new SingleResponseDto<>(UserResponse.of(user));
//    }
//
//    @GetMapping
//    public SingleResponseDto<UserResponse> getUserInfo() {
//        User loginUser = userService.getLoginUser();
//        return new SingleResponseDto<>(UserResponse.of(loginUser));
//    }
//
//    @PostMapping("/logout")
//    public SingleResponseDto<String> handleLogout(HttpServletRequest request) {
//        logout.doLogout(request);
//        return new SingleResponseDto<>(LOGOUT_COMPLETE);
//    }
//
//    @DeleteMapping
//    public SingleResponseDto<String> deleteUser(HttpServletRequest request) {
//        User user = userService.deleteUser();
//        logout.doLogout(request);
//        return new SingleResponseDto<>(user.getUserStatus().name());
//    }
}


