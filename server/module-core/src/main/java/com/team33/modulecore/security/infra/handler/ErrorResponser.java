package com.team33.modulecore.security.infra.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.security.dto.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ErrorResponser {

    private final ObjectMapper objectMapper;

    public void errorToJson(
        HttpServletResponse response,
        Exception exception,
        HttpStatus status
    ) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");

        ErrorResponse exceptions = ErrorResponse.builder()
            .status(status.value())
            .message(exception.getMessage())
            .build();

        log.error("{}", exceptions.getMessage());

        String errorResponse = objectMapper.writeValueAsString(exceptions);

        response.getWriter().write(errorResponse);
    }
}

