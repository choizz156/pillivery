package com.team33.moduleapi.security.dto;

import java.util.Map;

import com.team33.modulecore.user.domain.entity.User;
import com.team33.modulecore.user.domain.UserRoles;
import com.team33.modulecore.user.domain.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name,
        String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of( String registrationId,
                                    String userNameAttributeName,
                                      Map<String, Object> attributes
    ) {
        if ("kakao".equals(registrationId))
            return ofKakao("id_kakao", attributes);

        return ofGoogle(userNameAttributeName, attributes);
    }

    @SuppressWarnings("unchecked")
	private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
            .nameAttributeKey(userNameAttributeName)
            .name(String.valueOf(profile.get("nickname")))
            .email((String) kakaoAccount.get("email"))
            .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes
    ) {
        return OAuthAttributes.builder()
            .name(String.valueOf(attributes.get("name")))
            .email(String.valueOf(attributes.get("email")))
            .nameAttributeKey(userNameAttributeName + "google")
            .build();
    }

    public User toEntity() {
        return User.builder()
            .realName(name)
            .email(email)
            .roles(UserRoles.USER)
            .oauthId(nameAttributeKey)
            .userStatus(UserStatus.USER_ACTIVE)
            .build();
    }
}
