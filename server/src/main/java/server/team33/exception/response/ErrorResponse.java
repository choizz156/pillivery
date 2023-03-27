package server.team33.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import server.team33.exception.bussiness.ExceptionCode;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final int status;
    private final String message;
    private final List<CustomFieldError> customFieldErrors;

    @Builder
    public ErrorResponse(int status, String message, List<CustomFieldError> customFieldErrors) {
        this.status = status;
        this.message = message;
        this.customFieldErrors = customFieldErrors;
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        return ErrorResponse.builder().customFieldErrors(CustomFieldError.of(bindingResult))
            .build();
    }

    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return ErrorResponse.builder().status(exceptionCode.getCode())
            .message(exceptionCode.getMessage()).build();
    }

    public static ErrorResponse of(HttpStatus httpStatus) {//저장되어 있는 문구
        return ErrorResponse.builder().status(httpStatus.value())
            .message(httpStatus.getReasonPhrase()).build();
    }

    public static ErrorResponse of(ConstraintViolationException e) {
        List<CustomFieldError> errors = e.getConstraintViolations().stream()
            .map(v -> new CustomFieldError(v.getPropertyPath().toString(),
                null,
                v.getMessage()))
            .collect(Collectors.toList());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), errors);
    }

    public static ErrorResponse of(String message) { //직접 쓰는 문장
        return ErrorResponse.builder().message(message).build();
    }

    @Getter
    public static class CustomFieldError {

        private String field;
        private Object rejectedValue;
        private String reason;

        private CustomFieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<CustomFieldError> of(BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                .map(error -> new CustomFieldError(error.getField(),
                    error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                    error.getDefaultMessage()))
                .collect(Collectors.toList());
        }
    }
}