package com.team33.modulecore.security.application.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.security.application.ResponseTokenService;
import com.team33.modulecore.security.domain.UserDetailsEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Component
public class UserAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ResponseTokenService responseTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        UserDetailsEntity principal = (UserDetailsEntity) authentication.getPrincipal();
        User user = principal.getUser();
        responseTokenService.delegateToken(response,user);
    }
}

