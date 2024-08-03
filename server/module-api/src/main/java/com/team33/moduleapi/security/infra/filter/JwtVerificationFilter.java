package com.team33.moduleapi.security.infra.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.team33.moduleapi.security.application.LogoutService;
import com.team33.moduleapi.security.application.ResponseTokenService;
import com.team33.moduleapi.security.infra.JwtTokenProvider;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private static final String EXCEPTION_KEY = "exception";
    private static final String AUTHORIZATION = "Authorization";


    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseTokenService responseTokenService;
    private final LogoutService logoutService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        putAuthToSecurityContext(request);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (isRequestRefreshToken(request)) {
            return true;
        }

        String authorization = request.getHeader(AUTHORIZATION);

        if (checkNull(request, authorization)) {
            return true;
        }

        if (checkTokenForm(request, authorization)) {
            return true;
        }

        return checkLogout(request);
    }

    private boolean checkLogout(final HttpServletRequest request) {
        if (logoutService.isLogoutAlready(request)) {
            log.error(ExpiredJwtException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, new BusinessLogicException(ExceptionCode.ALREADY_LOGOUT));
            return true;
        }
        return false;
    }

    private void putAuthToSecurityContext(HttpServletRequest request) {
        String jws = request.getHeader(AUTHORIZATION).replace("Bearer ", "");
        setAuthToSecurityContext(verifyJws(request, jws));
    }

    private void setAuthToSecurityContext(Map<String, Object> claims) {

        String username = (String) claims.get("username");
        String roles = (String) claims.get("roles");

        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_" + roles);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(username, null, authorityList);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.warn("{}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    private boolean isRequestRefreshToken(final HttpServletRequest request) {
        String refreshJws = request.getHeader("Refresh");
        return !isNullOrEmpty(refreshJws) && isDelegatePossible(request, refreshJws);
    }

    private boolean isDelegatePossible(final HttpServletRequest request, final String refreshJws) {
        var subject = (String) verifyJws(request, refreshJws).get("sub");
        var refreshToken = responseTokenService.checkToken(subject);
        if (refreshToken.isPresent()) {
            request.setAttribute("refresh", subject);
            return true;
        }
        return false;
    }

    private Map<String, Object> verifyJws(final HttpServletRequest request, final String jws) {
        Map<String, Object> claims = new HashMap<>();
        try {
            claims = jwtTokenProvider.getJwsBody(jws);
        } catch (SignatureException e) {
            log.info(SignatureException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
        } catch (ExpiredJwtException e) {
            log.info(ExpiredJwtException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
        } catch (JwtException e) {
            log.info(JwtException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, e);
        }
        return claims;
    }

    private boolean checkNull(final HttpServletRequest request, final String authorization) {
        if (authorization == null) {
            log.warn(NullPointerException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY, new NullPointerException("No Token"));
            return true;
        }
        return false;
    }

    private boolean checkTokenForm(final HttpServletRequest request, final String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            log.info(MalformedJwtException.class.getSimpleName());
            request.setAttribute(EXCEPTION_KEY,  new MalformedJwtException("Malformed Token"));
            return true;
        }
        return false;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
