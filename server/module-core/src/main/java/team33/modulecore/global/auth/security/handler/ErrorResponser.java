package team33.modulecore.global.auth.security.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import team33.modulecore.global.exception.response.ErrorResponse;
import team33.modulecore.global.util.JsonMapper;


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
