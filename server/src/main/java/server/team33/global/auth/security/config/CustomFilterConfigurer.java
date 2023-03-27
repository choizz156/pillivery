package server.team33.global.auth.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;
import server.team33.global.auth.security.filter.JwtLoginFilter;
import server.team33.global.auth.security.filter.JwtVerificationFilter;
import server.team33.global.auth.security.handler.UserAuthFailureHandler;
import server.team33.global.auth.security.handler.UserAuthSuccessHandler;
import server.team33.domain.user.service.Logout;
import server.team33.global.auth.security.jwt.JwtTokenProvider;

@RequiredArgsConstructor
@Component
public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
    private final JwtTokenProvider jwtTokenProvider;
    private final Logout logout;

    @Override
    public void configure( HttpSecurity builder ) throws Exception{
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager, jwtTokenProvider);
        jwtLoginFilter.setFilterProcessesUrl("/users/login");

        jwtLoginFilter.setAuthenticationFailureHandler(new UserAuthFailureHandler());
        jwtLoginFilter.setAuthenticationSuccessHandler(new UserAuthSuccessHandler(jwtTokenProvider));

        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenProvider,logout);

        builder.addFilter(jwtLoginFilter)
                .addFilterAfter(jwtVerificationFilter, JwtLoginFilter.class);
    }
}
