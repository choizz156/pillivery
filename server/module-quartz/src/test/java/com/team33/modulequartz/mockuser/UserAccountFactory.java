package com.team33.modulequartz.mockuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.team33.modulecore.core.user.application.UserService;
import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.user.dto.UserServicePostDto;

public class UserAccountFactory implements WithSecurityContextFactory<UserAccount> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public SecurityContext createSecurityContext(UserAccount annotation) {

        UserServicePostDto postDto = UserServicePostDto.builder()
            .phone(annotation.value()[1])
            .displayName(annotation.value()[0])
            .email(annotation.value()[0] + "@test.com")
            .password("1234")
            .address(new Address("city", "address"))
            .realName("name")
            .build();

        userService.join(postDto);

        UserDetails userDetails = userDetailsService.loadUserByUsername(postDto.getEmail());
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

}