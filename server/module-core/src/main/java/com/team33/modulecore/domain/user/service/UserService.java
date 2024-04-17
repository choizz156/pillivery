package com.team33.modulecore.domain.user.service;

import com.team33.modulecore.domain.cart.entity.Cart;
import com.team33.modulecore.domain.cart.repository.CartRepository;
import com.team33.modulecore.domain.user.UserServiceDto;
import com.team33.modulecore.domain.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.domain.user.dto.UserServicePatchDto;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.repository.UserRepository;
import com.team33.modulecore.global.exception.BusinessLogicException;
import com.team33.modulecore.global.exception.ExceptionCode;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DuplicationVerifier duplicationVerifier;
    private final CartRepository cartRepository;

    public long join(UserServiceDto userServiceDto) {
        duplicationVerifier.checkUserInfo(userServiceDto);

        String encryptedPassword = encryptPassword(userServiceDto.getPassword());
        User user = User.createUser(userServiceDto, encryptedPassword);
        makeCart(user);

        return userRepository.save(user).getId();
    }


    public User updateUser(UserServicePatchDto userDto, long userId) {
        duplicationVerifier.checkDuplicationOnUpdate(userDto,userId);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.updateUserInfo(userDto);
            String encodedPwd = passwordEncoder.encode(userDto.getPassword());
            user.applyEncryptPassword(encodedPwd);
            return user;
        }
        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    public User deleteUser(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.withdrawal();
            return user;
        }
        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    public User addOAuthInfo(OAuthUserServiceDto userDto) {
        duplicationVerifier.checkOauthAdditionalInfo(userDto);
        return addOauthUserInfo(userDto);
    }

    public User getLoginUser() {
        String principal = (String) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        Optional<User> userOptional = userRepository.findByEmail(principal);
        return userOptional.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    public User getLoginUser1(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    private void makeCart(User user) {
        Cart cart = Cart.createCart(user);
        cartRepository.save(cart);
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private User addOauthUserInfo(OAuthUserServiceDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            makeCart(user);
            user.addAdditionalOauthUserInfo(userDto);
            return user;
        }

        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }
}
