package com.team33.modulecore.user.application;

import com.team33.modulecore.cart.domain.Cart;
import com.team33.modulecore.cart.repository.CartRepository;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.domain.repository.UserRepository;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.dto.UserServicePostDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserFindHelper helper;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final DuplicationVerifier duplicationVerifier;

    public long join(UserServicePostDto userServicePostDto) {
        duplicationVerifier.checkUserInfo(userServicePostDto);

        String encryptedPassword = encryptPassword(userServicePostDto.getPassword());
        User user = User.createUser(userServicePostDto, encryptedPassword);
        makeCart(user);

        return userRepository.save(user).getId();
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

    public User getLoginUser1(long userId) {
        return helper.findUser(userId);
    }

//    public User getLoginUser() {
//        String principal = (String) SecurityContextHolder
//            .getContext()
//            .getAuthentication()
//            .getPrincipal();
//
//        Optional<User> userOptional = userRepository.findByEmail(principal);
//        return userOptional.orElseThrow(
//            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
//    }

    private void makeCart(User user) {
        Cart cart = Cart.createCart(user);
        cartRepository.save(cart);
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private User addOauthUserInfo(OAuthUserServiceDto userDto) {
        User user = helper.findUserByEmail(userDto.getEmail());
        makeCart(user);
        user.addAdditionalOauthUserInfo(userDto);
        return user;
    }

    private User withdrawal(long userId) {
        User user = helper.findUser(userId);
        user.withdrawal();
        return user;
    }

    private User updateUserInfo(UserServicePatchDto userDto, long userId) {
        User user = helper.findUser(userId);
        user.updateUserInfo(userDto);
        String encodedPwd = passwordEncoder.encode(userDto.getPassword());
        user.applyEncryptPassword(encodedPwd);
        return user;
    }

    private User getUpdatedUser(UserServicePatchDto userDto, long userId) {
        User user = helper.findUser(userId);
        duplicationVerifier.checkDuplicationOnUpdate(userDto, user);
        return updateUserInfo(userDto, userId);
    }
}
