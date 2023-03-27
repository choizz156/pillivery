package server.team33.auth.security.filter;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import server.team33.auth.security.dto.LoginDto;
import server.team33.auth.security.jwt.JwtTokenProvider;
import server.team33.util.JsonMapper;

@RequiredArgsConstructor
@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication attemptAuthentication(
                                                    HttpServletRequest request,
                                                    HttpServletResponse response
    ) throws AuthenticationException{
        log.info("로그인 시도");
        LoginDto loginDto = getLoginDto(request);
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    public void successfulAuthentication(
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult
    ) throws ServletException, IOException {
        log.info("로그인 성공");

        successOAuth(request, response, authResult);
    }

    private void successOAuth(HttpServletRequest request, HttpServletResponse response,
        Authentication authResult) throws IOException, ServletException {
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    private LoginDto getLoginDto(HttpServletRequest request) {
        try {
            return JsonMapper.stringToObj(request,new LoginDto());
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }
}
