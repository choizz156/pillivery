package team33.modulecore.domain.user.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team33.modulecore.domain.user.dto.UserPatchDto;
import team33.modulecore.domain.user.dto.UserPostDto;
import team33.modulecore.domain.user.dto.UserPostOauthDto;
import team33.modulecore.domain.user.dto.UserResponse;
import team33.modulecore.domain.user.entity.User;
import team33.modulecore.domain.user.service.Logout;
import team33.modulecore.domain.user.service.UserService;
import team33.modulecore.global.auth.security.jwt.JwtTokenProvider;
import team33.modulecore.global.response.SingleResponseDto;


@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final Logout logout;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String JOIN_COMPLETE = "회원 가입 완료";
    private static final String LOGOUT_COMPLETE = "로그아웃 완료";
    private static final String OAUTH_JOIN_COMPLETE = "소셜 회원 추가 정보 기입 완료";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponseDto<String> singUpUser(@Valid @RequestBody UserPostDto userDto) {
        userService.join(userDto);
        return new SingleResponseDto<>(JOIN_COMPLETE);
    }

    @PostMapping("/more-info")
    @ResponseStatus(HttpStatus.CREATED)
    public void moreInfo(
        @Valid @RequestBody UserPostOauthDto userDto,
        HttpServletResponse response
    ) throws IOException {
        User user = userService.addOAuthInfo(userDto);
        jwtTokenProvider.addTokenInResponse(response, user);

        response.getWriter().write(OAUTH_JOIN_COMPLETE);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SingleResponseDto<UserResponse> updateInfo(@Valid @RequestBody UserPatchDto userDto) {
        User user = userService.updateUser(userDto);
        return new SingleResponseDto<>(UserResponse.of(user));
    }

    @GetMapping
    public SingleResponseDto<UserResponse> getUserInfo() {
        User loginUser = userService.getLoginUser();
        return new SingleResponseDto<>(UserResponse.of(loginUser));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SingleResponseDto<String> handleLogout(HttpServletRequest request) {
        logout.doLogout(request);
        return new SingleResponseDto<>(LOGOUT_COMPLETE);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SingleResponseDto<String> deleteUser(HttpServletRequest request) {
        User user = userService.deleteUser();
        logout.doLogout(request);
        return new SingleResponseDto<>(user.getUserStatus().name());
    }
}

