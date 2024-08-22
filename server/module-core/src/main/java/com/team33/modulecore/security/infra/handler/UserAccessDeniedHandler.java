package com.team33.modulecore.security.infra.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorResponser errorResponser;
    @Override
    public void handle( HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException ) throws IOException, ServletException{
        log.error("권한 없는 사용자");
        errorResponser.errorToJson(response, accessDeniedException, HttpStatus.FORBIDDEN);
    }
}
