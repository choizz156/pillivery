package com.team33.modulecore.user.application;

import com.team33.modulecore.user.dto.UserServiceDto;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.repository.UserRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class DuplicationVerifier {

    private final UserRepository userRepository;

    public void checkUserInfo(UserServiceDto user) {
        checkExistEmail(user.getEmail());
        checkExistDisplayName(user.getDisplayName());
        checkExistPhoneNum(user.getPhone());
    }

    public void checkOauthAdditionalInfo(OAuthUserServiceDto userDto) {
        checkExistDisplayName(userDto.getDisplayName());
        checkExistPhoneNum(userDto.getPhone());
    }

    public void checkDuplicationOnUpdate(UserServicePatchDto dto, long userId) {
        Optional<User> loginUser = userRepository.findById(userId);
        if (loginUser.isPresent()) {
            checkExistPhoneOnUpdate(loginUser.get(), dto);
            checkExistDisplayNameOnUpdate(loginUser.get(), dto);
        }
    }

    private void checkExistPhoneOnUpdate(User loginUser, UserServicePatchDto userDto) {
        if (isTheSameMyPhone(loginUser, userDto)) {
            return;
        }
        checkExistPhoneNum(userDto.getPhone());
    }

    private void checkExistDisplayNameOnUpdate(User loginUser, UserServicePatchDto userDto) {
        if (isTheSameMyDisplayName(loginUser, userDto)) {
            return;
        }
        checkExistDisplayName(userDto.getDisplayName());
    }

    private boolean isTheSameMyDisplayName(User loginUser, UserServicePatchDto userDto) {
        return loginUser.getDisplayName().equals(userDto.getDisplayName());
    }

    private boolean isTheSameMyPhone(User loginUser, UserServicePatchDto userDto) {
        return loginUser.getPhone().equals(userDto.getPhone());
    }

    private void checkExistDisplayName(String displayName) {
        log.info("displayName = {}", displayName);
        Optional<User> user = userRepository.findByDisplayName(displayName);
        if (user.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EXIST_DISPLAY_NAME);
        }
    }

    private void checkExistPhoneNum(String phoneNum) {
        log.info("phone = {}", phoneNum);
        Optional<User> user = userRepository.findByPhone(phoneNum);
        if (user.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EXIST_PHONE_NUMBER);
        }
    }

    private void checkExistEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EXIST_EMAIL);
        }
    }
}
