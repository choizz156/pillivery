package com.team33.moduleapi.security.config;


import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

import com.team33.moduleapi.security.infra.handler.UserAccessDeniedHandler;
import com.team33.moduleapi.security.infra.handler.UserAuthFailureHandler;
import com.team33.moduleapi.security.infra.handler.UserAuthenticationEntryPoint;
import com.team33.moduleapi.security.infra.handler.UserOAuthSuccessHandler;
import com.team33.moduleapi.security.infra.JwtTokenProvider;
import com.team33.moduleapi.security.application.Logout;
import com.team33.moduleapi.security.repository.RefreshTokenRepository;
import com.team33.moduleapi.security.application.ResponseTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseTokenService responseTokenService;
    private final RefreshTokenRepository repository;
    private final Logout logout;
    private static final String USER_URL = "/users/**";
    private static final String CART_URL = "/carts/**";
    private static final String WISHS_URL = "/wishes/**";
    private static final String ORDERS = "/orders/**";
    private static final String REVIEWS = "/reviews/**";
    private static final String SCHEDULE_URL = "/schedule";
    private static final String PAYMENTS_URL = "/payments/{orderId}";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .formLogin().disable()
            .httpBasic().disable()
            .csrf().disable()
            .cors(Customizer.withDefaults())
            .exceptionHandling()
            .accessDeniedHandler(new UserAccessDeniedHandler())
            .authenticationEntryPoint(new UserAuthenticationEntryPoint(responseTokenService))

            .and()
            .apply(new CustomFilterConfigurer(jwtTokenProvider, responseTokenService, logout))

            .and()
            .oauth2Login()
            .successHandler(new UserOAuthSuccessHandler(jwtTokenProvider, repository))
            .failureHandler(new UserAuthFailureHandler())

            .and()
            .authorizeHttpRequests(authorize -> authorize
                .antMatchers(GET, USER_URL).hasRole("USER")
                .antMatchers(PATCH, USER_URL).hasRole("USER")
                .antMatchers(DELETE, USER_URL).hasRole("USER")
                .antMatchers(GET, CART_URL).hasRole("USER")
                .antMatchers(POST, CART_URL).hasRole("USER")
                .antMatchers(DELETE, CART_URL).hasRole("USER")
                .antMatchers(GET, WISHS_URL).hasRole("USER")
                .antMatchers(POST, WISHS_URL).hasRole("USER")
                .antMatchers(WISHS_URL).hasRole("USER")
                .antMatchers(ORDERS).hasRole("USER")
                .antMatchers(REVIEWS).hasRole("USER")
                .antMatchers(PATCH, SCHEDULE_URL).hasRole("USER")
                .antMatchers(DELETE, SCHEDULE_URL).hasRole("USER")
                .antMatchers(GET, PAYMENTS_URL).hasRole("USER")
                .anyRequest().permitAll()
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
