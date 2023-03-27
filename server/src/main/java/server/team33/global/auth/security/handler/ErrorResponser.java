package server.team33.global.auth.security.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import server.team33.global.exception.response.ErrorResponse;
import server.team33.global.util.JsonMapper;


public class ErrorResponser {

    private ErrorResponser() {
    }

    public static void errorToJson(HttpServletResponse response, Exception exception,
        HttpStatus status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        ErrorResponse exceptions = ErrorResponse.builder()
            .status(status.value()).message(exception.getMessage()).build();

        String errorResponse = JsonMapper.objToString(exceptions);

        response.getWriter().write(errorResponse);
    }
}
