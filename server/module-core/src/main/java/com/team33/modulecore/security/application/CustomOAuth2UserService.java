package com.team33.modulecore.security.application;

import com.team33.modulecore.security.domain.UserDetailsEntity;
import com.team33.modulecore.security.dto.OAuthAttributes;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthAttributes attributes = OAuthAttributes.of(
                                                        registrationId,
                                                        getUserNameAttributeName(userRequest),
                                                        oAuth2User.getAttributes()
        );
        log.info("{}", attributes.getEmail());
        checkExistEmail(attributes);

        User user = saveUser(attributes);
        return UserDetailsEntity.builder().user(user).attributes(oAuth2User.getAttributes()).build();
    }

    private User saveUser(final OAuthAttributes attributes) {
        User user = attributes.toEntity();
        user.applyEncryptPassword("password");
        userRepository.save(user);
        return user;
    }

    private String getUserNameAttributeName(OAuth2UserRequest userRequest) {
        return userRequest
            .getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
    }

    private void checkExistEmail(OAuthAttributes attributes) {
        Optional<User> optionalUser = userRepository.findByEmail(attributes.getEmail());
        if (optionalUser.isPresent()) {
            log.error("이미 가입한 이메일 = {}", attributes.getEmail());
            throw new OAuth2AuthenticationException(new OAuth2Error("이메일 중복"), "이미 가입된 이메일입니다.");
        }
    }
}
