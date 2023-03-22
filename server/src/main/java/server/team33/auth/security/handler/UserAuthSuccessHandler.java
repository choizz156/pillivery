package server.team33.auth.security.handler;


import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import server.team33.auth.security.details.UserDetailsEntity;
import server.team33.auth.security.jwt.JwtTokenProvider;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        redirectInfo(request, response, authentication);
    }

    private void redirectInfo(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        UserDetailsEntity userDetailsEntity = getPrincipalDetails(authentication);
        String uri = createInfoURI(userDetailsEntity).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private UserDetailsEntity getPrincipalDetails(Authentication authentication) {
        return (UserDetailsEntity) authentication.getPrincipal();
    }

    private URI createInfoURI(UserDetailsEntity userDetailsEntity) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("email", userDetailsEntity.getUsername());
        queryParams.add("userId", String.valueOf(userDetailsEntity.getUser().getUserId()));

        return UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
            .path("/signup")
            .queryParams(queryParams)
            .build()
            .toUri();
    }

}
