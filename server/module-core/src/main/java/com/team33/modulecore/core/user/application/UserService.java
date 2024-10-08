package com.team33.modulecore.core.user.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.application.CartService;
import com.team33.modulecore.core.user.domain.repository.UserRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.core.user.dto.UserServicePatchDto;
import com.team33.modulecore.core.user.dto.UserServicePostDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

	private final UserRepository userRepository;
	private final CartService cartService;
	private final PasswordEncoder passwordEncoder;
	private final DuplicationVerifier duplicationVerifier;

	public User join(UserServicePostDto userServicePostDto) {
		duplicationVerifier.checkUserInfo(userServicePostDto);

		String encryptedPassword = encryptPassword(userServicePostDto.getPassword());
		User user = User.createUser(userServicePostDto, encryptedPassword);
		makeCart(user);

		return userRepository.save(user);
	}

	public User updateUser(UserServicePatchDto userDto, long userId) {
		return getUpdatedUser(userDto, userId);
	}

	public User deleteUser(long userId) {
		return withdrawal(userId);
	}

	public User addOAuthInfo(OAuthUserServiceDto userDto) {
		duplicationVerifier.checkOauthAdditionalInfo(userDto);
		return addOauthUserInfo(userDto);
	}

	public User findUser(long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
	}

	private void makeCart(User user) {
		Long normalCartId = cartService.createNormalCart();
		Long subsCartId = cartService.createSubsCart();
		user.addCart(normalCartId, subsCartId);
	}

	private String encryptPassword(String password) {
		return passwordEncoder.encode(password);
	}

	private User addOauthUserInfo(OAuthUserServiceDto userDto) {
		User user = userRepository.findByEmail(userDto.getEmail())
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

		makeCart(user);
		user.addAdditionalOauthUserInfo(userDto);
		return user;
	}

	private User withdrawal(long userId) {
		User user = findUser(userId);

		cartService.deleteCart(user.getNormalCartId(), user.getSubscriptionCartId());
		user.withdrawal();

		return user;
	}

	private User updateUserInfo(UserServicePatchDto userDto, long userId) {
		User user = findUser(userId);

		user.updateUserInfo(userDto);

		String encodedPwd = passwordEncoder.encode(userDto.getPassword());
		user.applyEncryptPassword(encodedPwd);

		return user;
	}

	private User getUpdatedUser(UserServicePatchDto userDto, long userId) {
		User user = findUser(userId);
		duplicationVerifier.checkDuplicationOnUpdate(userDto, user);
		return updateUserInfo(userDto, userId);
	}

	public void addReviewId(Long userId, Long reviewId) {
		User user = findUser(userId);
		user.addReviewId(reviewId);
	}

	public void deleteReviewId(Long userId, Long reviewId) {
		findUser(userId).deleteReviewId(reviewId);
	}
}
