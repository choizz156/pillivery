package com.team33.modulecore.global.security.security.handler;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import com.team33.modulecore.global.security.security.details.UserDetailsEntity;
import com.team33.modulecore.global.security.security.jwt.JwtTokenProvider;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {

        UserDetailsEntity userDetailsEntity = getPrincipalDetails(authentication);

        if (needMoreInfo(request, response, userDetailsEntity)) {
            return;
        }
        redirectToMain(response, userDetailsEntity);
    }

    private void redirectToMain(HttpServletResponse response, UserDetailsEntity userDetailsEntity)
        throws IOException {
        List<String> tokens = new ArrayList<>();
        tokens.add(jwtTokenProvider.delegateAccessToken(userDetailsEntity.getUser()));
        response.sendRedirect(getURI(tokens, userDetailsEntity).toString());
    }

    private UserDetailsEntity getPrincipalDetails(Authentication authentication) {
        return (UserDetailsEntity) authentication.getPrincipal();
    }

    private URI getURI(List<String> tokens, UserDetailsEntity principalDetails) {
        MultiValueMap<String, String> queryParams = getQueryParams(tokens, principalDetails);

        return UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
            .queryParams(queryParams)
            .build()
            .toUri();
    }

    private static MultiValueMap<String, String> getQueryParams(
        List<String> tokens,
        UserDetailsEntity principalDetails
    ) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", "Bearer " + tokens.get(0));
        queryParams.add("refresh_token", tokens.get(1));
        queryParams.add("userId", String.valueOf(principalDetails.getUser().getUserId()));

        return queryParams;
    }

    private URI createInfoURI(UserDetailsEntity principalDetails) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("email", principalDetails.getUsername());
        queryParams.add("userId", String.valueOf(principalDetails.getUser().getUserId()));
        log.info("{}", queryParams);

        return UriComponentsBuilder.newInstance().scheme("http")
            .host("pillivery.s3-website.ap-northeast-2.amazonaws.com")
            .path("/signup")
            .queryParams(queryParams)
            .build()
            .toUri();
    }

    private boolean needMoreInfo(
        HttpServletRequest request,
        HttpServletResponse response,
        UserDetailsEntity principalDetails
    ) throws IOException {
        if (principalDetails.getUser().getDisplayName() == null) {
            log.info("추가 정보 기입");
            redirectToMoreInfo(request, response, principalDetails);
            return true;
        }
        return false;
    }

    private void redirectToMoreInfo(
        HttpServletRequest request,
        HttpServletResponse response,
        UserDetailsEntity principalDetails
    ) throws IOException {
        getRedirectStrategy()
            .sendRedirect(
                request,
                response,
                createInfoURI(principalDetails).toString()
            );
    }
}
