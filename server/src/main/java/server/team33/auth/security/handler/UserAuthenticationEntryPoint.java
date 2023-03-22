package server.team33.auth.security.handler;

import static server.team33.auth.security.handler.ErrorResponser.errorToJson;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException, ServletException{
        log.info("AuthenticationEntryPoint");
        log.info(authException.getMessage());
        errorToJson(response, authException, HttpStatus.UNAUTHORIZED);
    }
}
