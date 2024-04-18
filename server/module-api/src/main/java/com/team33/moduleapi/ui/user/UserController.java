package com.team33.moduleapi.ui.user;


import com.team33.moduleapi.security.infra.JwtTokenProvider;
import com.team33.moduleapi.security.application.Logout;
import com.team33.moduleapi.security.application.ResponseTokenService;
import com.team33.modulecore.user.dto.UserServiceDto;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserPatchDto;
import com.team33.modulecore.user.dto.UserPostDto;
import com.team33.modulecore.user.dto.UserPostOauthDto;
import com.team33.modulecore.user.dto.UserResponse;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.common.SingleResponseDto;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public SingleResponseDto<Long> singUpUser(@Valid @RequestBody UserPostDto userPostDto) {

        UserServiceDto userServiceDto = UserServiceDto.to(userPostDto);
        long userId = userService.join(userServiceDto);
        return new SingleResponseDto<>(userId);
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

    @PatchMapping("/{userId}")
    public SingleResponseDto<UserResponse> updateInfo(
        @Valid @RequestBody UserPatchDto userDto,
        @PathVariable long userId
    ) {
        UserServicePatchDto userServicePatchDto = UserServicePatchDto.to(userDto);
        User user = userService.updateUser(userServicePatchDto, userId);
        return new SingleResponseDto<>(UserResponse.of(user));
    }

    @GetMapping("/{userId}")
    public SingleResponseDto<UserResponse> getUserInfo(@PathVariable long userId) {
        User loginUser = userService.getLoginUser1(userId);
        return new SingleResponseDto<>(UserResponse.of(loginUser));
    }

    @PostMapping("/logout")
    public SingleResponseDto<String> handleLogout(HttpServletRequest request) {
        logout.doLogout(request);
        return new SingleResponseDto<>(LOGOUT_COMPLETE);
    }

    @DeleteMapping("/{userId}")
    public SingleResponseDto<String> deleteUser(@PathVariable long userId, HttpServletRequest request) {
        User user = userService.deleteUser(userId);
        logout.doLogout(request);
        return new SingleResponseDto<>(user.getUserStatus().name());
    }
}


