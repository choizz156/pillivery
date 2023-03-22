package server.team33.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import server.team33.exception.bussiness.BusinessLogicException;
import server.team33.exception.bussiness.ExceptionCode;
import server.team33.user.dto.UserPatchDto;
import server.team33.user.dto.UserPostDto;
import server.team33.user.dto.UserPostOauthDto;
import server.team33.user.entity.User;
import server.team33.user.repository.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserInfoVerifier {

    private final UserRepository userRepository;

    public void checkDuplicationUserInfo(UserPostDto userDto) {
        checkExistEmail(userDto.getEmail());
        checkExistDisplayName(userDto.getDisplayName());
        checkExistPhoneNum(userDto.getPhone());
    }

    public void checkDuplicationOauthAdditionalInfo(UserPostOauthDto userDto) {
        checkExistDisplayName(userDto.getDisplayName());
        checkExistPhoneNum(userDto.getPhone());
    }

    public void checkDuplicationUserInfoOnUpdate(UserPatchDto userDto) {
        Optional<User> loginUser = userRepository.findByEmail(userDto.getEmail());
        if (loginUser.isPresent()) {
            checkExistPhoneOnUpdate(loginUser.get(), userDto);
            checkExistDisplayNameOnUpdate(loginUser.get(), userDto);
        }
    }

    private void checkExistPhoneOnUpdate(User loginUser, UserPatchDto userDto) {
        if (isTheSameMyPhone(loginUser, userDto)) {
            return;
        }
        checkExistPhoneNum(userDto.getPhone());
    }

    private void checkExistDisplayNameOnUpdate(User loginUser, UserPatchDto userDto) {
        if (isTheSameMyDisplayName(loginUser, userDto)) {
            return;
        }
        checkExistDisplayName(userDto.getDisplayName());
    }

    private boolean isTheSameMyDisplayName(User loginUser, UserPatchDto userDto) {
        return loginUser.getDisplayName().equals(userDto.getDisplayName());
    }

    private boolean isTheSameMyPhone(User loginUser, UserPatchDto userDto) {
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
