package com.team33.moduleapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.team33.moduleapi.ui.user.UserServiceMapper;
import com.team33.moduleapi.ui.user.dto.UserPostDto;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.user.dto.UserServicePostDto;

public class UserAccountFactory implements WithSecurityContextFactory<UserAccount> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserServiceMapper userServiceMapper;

    @Override
    public SecurityContext createSecurityContext(UserAccount annotation) {

        UserPostDto dto = getUserDto(annotation);
        UserServicePostDto userServicePostDto = userServiceMapper.toUserPost(dto);
        userService.join(userServicePostDto);

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
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

    private UserPostDto getUserDto(UserAccount annotation) {
        String displayName = annotation.value()[0];
        String email = displayName + "@test.com";
        String password = "1234";
        String city = "서울";
        String detailAddress = "압구정동";
        String realName = "name";
        String phone = annotation.value()[1];
        return UserPostDto.builder().detailAddress(detailAddress).password(password).phone(phone)
            .city(city).realName(realName).email(email).displayName(displayName).build();

    }
}