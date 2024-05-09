package com.team33.moduleapi.ui.user;

import java.util.List;

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
import com.team33.moduleapi.security.application.Logout;
import com.team33.moduleapi.security.application.ResponseTokenService;
import com.team33.moduleapi.security.infra.JwtTokenProvider;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserPatchDto;
import com.team33.modulecore.user.dto.UserPostDto;
import com.team33.modulecore.user.dto.UserPostOauthDto;
import com.team33.modulecore.user.dto.UserResponse;
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
	private static final String OAUTH_JOIN_COMPLETE = "소셜 회원 추가 정보 기입 완료";

	private final Logout logout;
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;
	private final ResponseTokenService responseTokenService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public SingleResponseDto<List<Long>> singUpUser(@Valid @RequestBody UserPostDto userPostDto) {

		UserServicePostDto userServicePostDto = UserServicePostDto.to(userPostDto);
		User user = userService.join(userServicePostDto);
		return new SingleResponseDto<>(List.of(user.getId(), user.getNormalCartId(), user.getSubscriptionCartId()));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/more-info")
	public SingleResponseDto<List<Long>> moreInfo(
		@Valid @RequestBody UserPostOauthDto userDto,
		HttpServletResponse response
	) {
		OAuthUserServiceDto oAuthUserServiceDto = OAuthUserServiceDto.to(userDto);
		User user = userService.addOAuthInfo(oAuthUserServiceDto);
		responseTokenService.delegateToken(response, user);

		return new SingleResponseDto<>(List.of(user.getId(), user.getNormalCartId(), user.getSubscriptionCartId()));
	}

	@PatchMapping("/{userId}")
	public SingleResponseDto<UserResponse> updateInfo(
		@Valid @RequestBody UserPatchDto userDto,
		@PathVariable Long userId
	) {
		UserServicePatchDto userServicePatchDto = UserServicePatchDto.to(userDto);
		User user = userService.updateUser(userServicePatchDto, userId);
		return new SingleResponseDto<>(UserResponse.of(user));
	}

	@GetMapping("/{userId}")
	public SingleResponseDto<UserResponse> getUserInfo(@PathVariable Long userId) {
		User loginUser = userService.getLoginUser1(userId);
		return new SingleResponseDto<>(UserResponse.of(loginUser));
	}

	@PostMapping("/logout")
	public SingleResponseDto<String> handleLogout(HttpServletRequest request) {
		String jws = getJws(request);
		logout.doLogout(jws);
		return new SingleResponseDto<>(LOGOUT_COMPLETE);
	}

	@DeleteMapping("/{userId}")
	public SingleResponseDto<String> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
		User user = userService.deleteUser(userId);
		String jws = getJws(request);
		logout.doLogout(jws);
		return new SingleResponseDto<>(user.getUserStatus().name());
	}

	private String getJws(HttpServletRequest request) {
		return jwtTokenProvider.extractJws(request);
	}
}


