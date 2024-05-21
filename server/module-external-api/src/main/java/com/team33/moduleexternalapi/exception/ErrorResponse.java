package com.team33.moduleexternalapi.exception;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final int status;
    private final String message;

    @Builder
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(HttpStatus httpStatus, String msg) {//저장되어 있는 문구
        return ErrorResponse.builder()
            .status(httpStatus.value())
            .message(msg)
            .build();
    }

    public static ErrorResponse of(String message) {
        return ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(message).build();
    }
}