package com.team33.moduleapi.response;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApiErrorResponse {

    private final int status;
    private final String message;
    private final List<CustomFieldError> customFieldErrors;

    @Builder
    public ApiErrorResponse(int status, String message, List<CustomFieldError> customFieldErrors) {
        this.status = status;
        this.message = message;
        this.customFieldErrors = customFieldErrors;
    }

    public static ApiErrorResponse of(HttpStatus httpStatus, BindingResult bindingResult) {
        return ApiErrorResponse.builder()
            .status(httpStatus.value())
            .customFieldErrors(CustomFieldError.of(bindingResult))
            .build();
    }

    public static ApiErrorResponse of(ExceptionCode exceptionCode) {
        return ApiErrorResponse.builder()
            .status(exceptionCode.getCode())
            .message(exceptionCode.getMessage())
            .build();
    }

    public static ApiErrorResponse of(HttpStatus httpStatus, String msg) {//저장되어 있는 문구
        return ApiErrorResponse.builder()
            .status(httpStatus.value())
            .message(msg)
            .build();
    }

    public static ApiErrorResponse of(ConstraintViolationException e) {
        List<CustomFieldError> errors = e.getConstraintViolations().stream()
            .map(v -> new CustomFieldError(v.getPropertyPath().toString(),
                null,
                v.getMessage()))
            .collect(Collectors.toList());
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), errors);
    }

    public static ApiErrorResponse of(String message) { //직접 쓰는 문장
        return ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(message).build();
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
                    error.getDefaultMessage())
                )
                .collect(Collectors.toList());
        }
    }
}