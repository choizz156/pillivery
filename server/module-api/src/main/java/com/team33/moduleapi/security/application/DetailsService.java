package com.team33.moduleapi.security.application;

import com.team33.moduleapi.security.domain.UserDetailsEntity;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.UserStatus;
import com.team33.modulecore.core.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Slf4j
@Component
public class DetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        Optional<User> userEntity = userRepository.findByEmail(username);

        User user = userEntity.orElseThrow(() -> new UsernameNotFoundException("Not Found User"));
        if (user.getUserStatus() == UserStatus.USER_WITHDRAWAL) {
            throw new AuthenticationServiceException("탈퇴한 회원입니다.");
        }

        return UserDetailsEntity.builder().user(user).build();
    }
}
