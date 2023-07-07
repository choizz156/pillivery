package com.team33.modulecore.global.security.security.filter;

import com.team33.modulecore.domain.user.service.Logout;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.team33.modulecore.global.security.security.jwt.JwtTokenProvider;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final Logout logout;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        log.error("doFilterInteral 메서드 =======");
        putAuthToSecurityContext(request);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        log.info("shouldNotFilter 진입");
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            log.error(NullPointerException.class.getSimpleName());
            return true;
        }

        if (!authorization.startsWith("Bearer ")) {
            log.error(MalformedJwtException.class.getSimpleName());
            return true;
        }

        if (logout.isLogoutAlready(request)) {
            log.error(ExpiredJwtException.class.getSimpleName());
            return true;
        }
        return false;
    }

    private void putAuthToSecurityContext(HttpServletRequest request) {
        try {
            setAuthToSecurityContext(jwtTokenProvider.getJwsBody(request));
        } catch (InsufficientAuthenticationException e1) {
            log.error(InsufficientAuthenticationException.class.getSimpleName());
        } catch (MalformedJwtException e1) {
            log.error(MalformedJwtException.class.getSimpleName());
        } catch (SignatureException e1) {
            log.error(SignatureException.class.getSimpleName());
        } catch (ExpiredJwtException e1) {
            log.error(ExpiredJwtException.class.getSimpleName());
        } catch (Exception e1) {
            log.error(Exception.class.getSimpleName());
        }
    }

    private void setAuthToSecurityContext(Map<String, Object> claims) {

        String username = (String) claims.get("username");
        String roles = (String) claims.get("roles");

        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_"+ roles);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(username, null, authorityList);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.warn("{}",SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }
}
