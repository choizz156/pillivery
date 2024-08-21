package com.team33.moduleapi.mockuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.user.domain.UserRoles;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;

public class OauthAccountFactory implements WithSecurityContextFactory<OAuthAccount> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public SecurityContext createSecurityContext(OAuthAccount annotation) {
        User user = oauthUser(annotation);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
            );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        return context;
    }

    private User oauthUser(OAuthAccount annotation) {
        String displayName = annotation.value()[0];
        String email = "test@test.com";
        String password = "sdfsdf232!";
        String city = "서울";
        String detailAddress = "압구정동";
        String realName = "name";
        String phone = annotation.value()[1];
        String oauthId = "sub";

        return User.builder()
            .displayName(displayName)
            .email(email)
            .password(password)
            .address(new Address(city, detailAddress))
            .realName(realName)
            .phone(phone)
            .oauthId(oauthId)
            .roles(UserRoles.USER)
            .build();
    }
}