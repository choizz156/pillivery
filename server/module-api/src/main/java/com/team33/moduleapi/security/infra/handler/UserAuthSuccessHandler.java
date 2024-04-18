package com.team33.moduleapi.security.infra.handler;


import com.team33.moduleapi.security.domain.UserDetailsEntity;
import com.team33.moduleapi.security.application.ResponseTokenService;
import com.team33.modulecore.user.domain.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


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

