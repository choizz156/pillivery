package server.team33.auth.security.config;


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
import server.team33.auth.security.handler.UserAccessDeniedHandler;
import server.team33.auth.security.handler.UserAuthFailureHandler;
import server.team33.auth.security.handler.UserAuthSuccessHandler;
import server.team33.auth.security.handler.UserAuthenticationEntryPoint;
import server.team33.auth.security.jwt.JwtTokenProvider;
import server.team33.user.service.Logout;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final Logout logout;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
            .successHandler(new UserAuthSuccessHandler(jwtTokenProvider))
            .failureHandler(new UserAuthFailureHandler())

            .and()
            .authorizeHttpRequests(authorize -> authorize
                .antMatchers(HttpMethod.GET, "/users/**").hasRole("USER")
                .antMatchers(HttpMethod.PATCH, "/users/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/users/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/carts").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/carts/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/carts/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/wishes/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/wishes/**").hasRole("USER")
                .antMatchers("/wishes/**").hasRole("USER")
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
