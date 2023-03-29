package server.team33.global.auth.security.config;


import static org.springframework.security.config.Customizer.withDefaults;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import server.team33.global.auth.security.handler.UserAccessDeniedHandler;
import server.team33.global.auth.security.handler.UserAuthFailureHandler;
import server.team33.global.auth.security.handler.UserAuthenticationEntryPoint;
import server.team33.global.auth.security.handler.UserOAuthSuccessHandler;
import server.team33.global.auth.security.jwt.JwtTokenProvider;
import server.team33.domain.user.service.Logout;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final Logout logout;

    private static final String USER_URL = "/users/**";
    private static final String CART_URL = "/carts/**";
    private static final String WISHS_URL = "/wishes/**";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()

            .formLogin().disable()
            .httpBasic().disable()
            .csrf().disable()
            .cors(withDefaults())
            .exceptionHandling()
            .accessDeniedHandler(new UserAccessDeniedHandler())
            .authenticationEntryPoint(new UserAuthenticationEntryPoint())

            .and()
            .apply(new CustomFilterConfigurer(jwtTokenProvider, logout))

            .and()
            .oauth2Login()
            .successHandler(new UserOAuthSuccessHandler(jwtTokenProvider))
            .failureHandler(new UserAuthFailureHandler())

            .and()
            .authorizeHttpRequests(authorize -> authorize
                .antMatchers(HttpMethod.GET, USER_URL).hasRole("USER")
                .antMatchers(HttpMethod.PATCH, USER_URL).hasRole("USER")
                .antMatchers(HttpMethod.DELETE, USER_URL).hasRole("USER")
                .antMatchers(HttpMethod.GET, CART_URL).hasRole("USER")
                .antMatchers(HttpMethod.POST, CART_URL).hasRole("USER")
                .antMatchers(HttpMethod.DELETE, CART_URL).hasRole("USER")
                .antMatchers(HttpMethod.GET, WISHS_URL).hasRole("USER")
                .antMatchers(HttpMethod.POST, WISHS_URL).hasRole("USER")
                .antMatchers(WISHS_URL).hasRole("USER")
                .antMatchers("/orders/**").hasRole("USER")
                .antMatchers("/reviews/**").hasRole("USER")
                .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
