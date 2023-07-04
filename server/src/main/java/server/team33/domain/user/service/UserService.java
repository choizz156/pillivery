package server.team33.domain.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.team33.domain.cart.entity.Cart;
import server.team33.domain.cart.repository.CartRepository;
import server.team33.domain.user.dto.UserPatchDto;
import server.team33.domain.user.dto.UserPostDto;
import server.team33.domain.user.dto.UserPostOauthDto;
import server.team33.domain.user.entity.User;
import server.team33.domain.user.repository.UserRepository;
import server.team33.global.exception.BusinessLogicException;
import server.team33.global.exception.ExceptionCode;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DuplicationVerifier duplicationVerifier;
    private final CartRepository cartRepository;

    @Transactional
    public void join(UserPostDto userDto) {
        duplicationVerifier.checkUserInfo(userDto);

        User user = User.createUser(userDto);
        String encryptedPwd = encryptPassword(userDto.getPassword());
        user.applyEncryptPassword(encryptedPwd);
        makeCart(user);

        userRepository.save(user);
    }

    @Transactional
    public User deleteUser() {
        User loginUser = getLoginUser();
        loginUser.withdrawal();
        return loginUser;
    }

    @Transactional
    public User updateUser(UserPatchDto userDto) {
        duplicationVerifier.checkDuplicationOnUpdate(userDto);

        User loginUser = getLoginUser();
        loginUser.updateUserInfo(userDto);
        String encodedPwd = passwordEncoder.encode(userDto.getPassword());
        loginUser.applyEncryptPassword(encodedPwd);

        return loginUser;
    }

    @Transactional
    public User addOAuthInfo(UserPostOauthDto userDto) {

        duplicationVerifier.checkOauthAdditionalInfo(userDto);

        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            makeCart(optionalUser.get());
            optionalUser.get().addAdditionalOauthUserInfo(userDto);
            return optionalUser.get();
        }

        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    public User getLoginUser() {
        String principal = (String) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        Optional<User> userOptional = userRepository.findByEmail(principal);
        return userOptional.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    private void makeCart(User user) {
        Cart cart = Cart.createCart(user);
        cartRepository.save(cart);
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
