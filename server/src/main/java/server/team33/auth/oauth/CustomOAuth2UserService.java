package server.team33.auth.oauth;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import server.team33.auth.security.details.UserDetailsEntity;
import server.team33.user.entity.User;
import server.team33.user.repository.UserRepository;

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
        log.warn("{}", attributes.getEmail());
        checkExistEmail(attributes);

        User user = attributes.toEntity();
        user.applyEncryptPassword("password");
        userRepository.save(user);
        return UserDetailsEntity.builder().user(user).attributes(oAuth2User.getAttributes()).build();
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
