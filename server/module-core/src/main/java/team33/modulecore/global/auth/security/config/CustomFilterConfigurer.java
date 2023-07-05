package team33.modulecore.global.auth.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;
import team33.modulecore.domain.user.service.Logout;
import team33.modulecore.global.auth.security.filter.JwtLoginFilter;
import team33.modulecore.global.auth.security.filter.JwtVerificationFilter;
import team33.modulecore.global.auth.security.handler.UserAuthFailureHandler;
import team33.modulecore.global.auth.security.handler.UserAuthSuccessHandler;
import team33.modulecore.global.auth.security.jwt.JwtTokenProvider;

@RequiredArgsConstructor
@Component
public class CustomFilterConfigurer extends
    AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;
    private final Logout logout;

    @Override
    public void configure(HttpSecurity builder) {
        AuthenticationManager authenticationManager = builder.getSharedObject(
            AuthenticationManager.class);
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager);
        jwtLoginFilter.setFilterProcessesUrl("/users/login");

        jwtLoginFilter.setAuthenticationFailureHandler(new UserAuthFailureHandler());
        jwtLoginFilter.setAuthenticationSuccessHandler(new UserAuthSuccessHandler(jwtTokenProvider));

        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenProvider,
            logout);

        builder.addFilter(jwtLoginFilter)
            .addFilterAfter(jwtVerificationFilter, JwtLoginFilter.class);
    }
}
