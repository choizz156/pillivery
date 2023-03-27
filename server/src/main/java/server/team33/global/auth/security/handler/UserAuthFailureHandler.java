package server.team33.global.auth.security.handler;

import static server.team33.global.auth.security.handler.ErrorResponser.errorToJson;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class UserAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure( HttpServletRequest request, HttpServletResponse response, AuthenticationException exception ) throws IOException, ServletException{
        log.error("로그인 실패");
        errorToJson(response, exception, HttpStatus.UNAUTHORIZED);
    }
}
