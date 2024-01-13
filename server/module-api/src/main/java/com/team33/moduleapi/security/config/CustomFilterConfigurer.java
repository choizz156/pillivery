package com.team33.moduleapi.security.config;


import com.team33.moduleapi.security.filter.JwtLoginFilter;
import com.team33.moduleapi.security.filter.JwtVerificationFilter;
import com.team33.moduleapi.security.handler.UserAuthFailureHandler;
import com.team33.moduleapi.security.handler.UserAuthSuccessHandler;
import com.team33.moduleapi.security.jwt.JwtTokenProvider;
import com.team33.moduleapi.security.jwt.Logout;
import com.team33.moduleapi.security.refreshtoken.ResponseTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomFilterConfigurer extends
    AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseTokenService responseTokenService;
    private final Logout logout;

    @Override
    public void configure(HttpSecurity builder) {
        AuthenticationManager authenticationManager =
            builder.getSharedObject(AuthenticationManager.class);

        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager);
        jwtLoginFilter.setFilterProcessesUrl("/users/login");

        jwtLoginFilter.setAuthenticationFailureHandler(new UserAuthFailureHandler());
        jwtLoginFilter.setAuthenticationSuccessHandler(
            new UserAuthSuccessHandler(responseTokenService)
        );

        JwtVerificationFilter jwtVerificationFilter =
            new JwtVerificationFilter(jwtTokenProvider, responseTokenService, logout);

        builder.addFilter(jwtLoginFilter)
            .addFilterAfter(jwtVerificationFilter, JwtLoginFilter.class);
    }
}
