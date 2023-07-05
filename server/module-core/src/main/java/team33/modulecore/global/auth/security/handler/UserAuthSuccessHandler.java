package team33.modulecore.global.auth.security.handler;


import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team33.modulecore.domain.user.entity.User;
import team33.modulecore.global.auth.security.details.UserDetailsEntity;
import team33.modulecore.global.auth.security.jwt.JwtTokenProvider;



@Slf4j
@RequiredArgsConstructor
@Component
public class UserAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String LOGIN_COMPLETE = "로그인 완료";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        UserDetailsEntity principal = (UserDetailsEntity) authentication.getPrincipal();
        User user = principal.getUser();
        jwtTokenProvider.addTokenInResponse(response, user);

        response.getWriter().write(LOGIN_COMPLETE);
    }
}

