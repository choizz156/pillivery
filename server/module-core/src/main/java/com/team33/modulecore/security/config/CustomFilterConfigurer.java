package com.team33.modulecore.security.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.security.application.LogoutService;
import com.team33.modulecore.security.application.ResponseTokenService;
import com.team33.modulecore.security.infra.JwtTokenProvider;
import com.team33.modulecore.security.infra.filter.JwtLoginFilter;
import com.team33.modulecore.security.infra.filter.JwtVerificationFilter;
import com.team33.modulecore.security.infra.filter.LogTraceFilter;
import com.team33.modulecore.security.infra.handler.ErrorResponser;
import com.team33.modulecore.security.infra.handler.UserAuthFailureHandler;
import com.team33.modulecore.security.infra.handler.UserAuthSuccessHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomFilterConfigurer extends
	AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {

	private final JwtTokenProvider jwtTokenProvider;
	private final ResponseTokenService responseTokenService;
	private final LogoutService logoutService;
	private final ObjectMapper objectMapper;
	private final ErrorResponser errorResponser;

	@Override
	public void configure(HttpSecurity builder) {
		AuthenticationManager authenticationManager =
			builder.getSharedObject(AuthenticationManager.class);

		JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager, objectMapper);
		jwtLoginFilter.setFilterProcessesUrl("/users/auth");
		jwtLoginFilter.setAuthenticationFailureHandler(new UserAuthFailureHandler(errorResponser));
		jwtLoginFilter.setAuthenticationSuccessHandler(new UserAuthSuccessHandler(responseTokenService));

		JwtVerificationFilter jwtVerificationFilter =
			new JwtVerificationFilter(jwtTokenProvider, responseTokenService, logoutService);

		builder.addFilter(jwtLoginFilter)
			.addFilterBefore(new LogTraceFilter(), JwtLoginFilter.class)
			.addFilterAfter(jwtVerificationFilter, JwtLoginFilter.class);
	}
}
