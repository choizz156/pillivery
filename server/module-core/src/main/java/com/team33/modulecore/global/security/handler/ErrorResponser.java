package com.team33.modulecore.global.security.handler;

import com.team33.modulecore.global.exception.response.ErrorResponse;
import com.team33.modulecore.global.util.Mapper;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponser {

    public static void errorToJson(
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

        String errorResponse = Mapper.getInstance().writeValueAsString(exceptions);

        response.getWriter().write(errorResponse);
    }
}
