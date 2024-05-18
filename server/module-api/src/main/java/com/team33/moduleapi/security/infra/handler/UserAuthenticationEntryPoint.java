package com.team33.moduleapi.security.infra.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.team33.moduleapi.security.application.ResponseTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResponseTokenService responseTokenService;
    private final ErrorResponser errorResponser;

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException, ServletException{
        log.info("AuthenticationEntryPoint");
        String email = (String) request.getAttribute("refresh");
        if (StringUtils.hasLength(email)) {
            responseTokenService.reissueToken(response, email);
            return;
        }

        errorResponser.errorToJson(response, authException, HttpStatus.UNAUTHORIZED);
    }
}
