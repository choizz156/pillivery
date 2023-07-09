package com.team33.modulecore.global.security.handler;

import com.team33.modulecore.global.exception.response.ErrorResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.team33.modulecore.global.util.JsonMapper;


public class ErrorResponser {

    private ErrorResponser() {
    }

    public static void errorToJson(
        HttpServletResponse response,
        Exception exception,
        HttpStatus status
    ) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());

        ErrorResponse exceptions = ErrorResponse.builder()
            .status(status.value()).message(exception.getMessage()).build();

        String errorResponse = JsonMapper.objToString(exceptions);

        response.getWriter().write(errorResponse);
    }
}
