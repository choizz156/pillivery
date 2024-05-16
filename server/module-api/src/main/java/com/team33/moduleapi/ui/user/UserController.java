package com.team33.moduleapi.ui.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.security.application.LogoutService;
import com.team33.moduleapi.security.application.ResponseTokenService;
import com.team33.moduleapi.security.infra.JwtTokenProvider;
import com.team33.moduleapi.ui.user.dto.UserIdDto;
import com.team33.moduleapi.ui.user.dto.UserPatchDto;
import com.team33.moduleapi.ui.user.dto.UserPostDto;
import com.team33.moduleapi.ui.user.dto.UserPostOauthDto;
import com.team33.moduleapi.ui.user.dto.UserResponse;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.dto.UserServicePostDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

	private static final String LOGOUT_COMPLETE = "로그아웃 완료";

	private final LogoutService logoutService;
	private final UserService userService;
	private final UserServiceMapper userServiceMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final ResponseTokenService responseTokenService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public SingleResponseDto<UserIdDto> singUpUser(@Valid @RequestBody UserPostDto userPostDto) {
		UserServicePostDto userPost = userServiceMapper.toUserPost(userPostDto);
		User user = userService.join(userPost);
		return new SingleResponseDto<>(UserIdDto.from(user));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/more-info")
	public SingleResponseDto<UserIdDto> moreInfo(
		@Valid @RequestBody UserPostOauthDto userDto,
		HttpServletResponse response
	) {
		OAuthUserServiceDto oauthPost = userServiceMapper.toOauthPost(userDto);
		User user = userService.addOAuthInfo(oauthPost);
		responseTokenService.delegateToken(response, user);

		return new SingleResponseDto<>(UserIdDto.from(user));
	}

	@PatchMapping("/{userId}")
	public SingleResponseDto<UserResponse> updateInfo(
		@Valid @RequestBody UserPatchDto userDto,
		@PathVariable Long userId
	) {
		UserServicePatchDto userPatch = userServiceMapper.toUserPatch(userDto);
		User user = userService.updateUser(userPatch, userId);
		return new SingleResponseDto<>(UserResponse.of(user));
	}

	@GetMapping("/{userId}")
	public SingleResponseDto<UserResponse> getUserInfo(@PathVariable Long userId) {
		User loginUser = userService.findUser(userId);
		return new SingleResponseDto<>(UserResponse.of(loginUser));
	}

	@PostMapping("/logout")
	public SingleResponseDto<String> handleLogout(HttpServletRequest request) {
		String jws = getJws(request);
		logoutService.doLogout(jws);
		return new SingleResponseDto<>(LOGOUT_COMPLETE);
	}

	@DeleteMapping("/{userId}")
	public SingleResponseDto<String> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
		User user = userService.deleteUser(userId);
		String jws = getJws(request);
		logoutService.doLogout(jws);
		return new SingleResponseDto<>(user.getUserStatus().name());
	}

	private String getJws(HttpServletRequest request) {
		return jwtTokenProvider.extractJws(request);
	}
}


