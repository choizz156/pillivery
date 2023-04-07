package server.team33.global.exception.controller;


import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import server.team33.global.exception.bussiness.BusinessLogicException;
import server.team33.global.exception.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return ErrorResponse.of(e.getBindingResult());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationExceptionHandler(ConstraintViolationException ex) {
        return ErrorResponse.of(ex);
    }

    @ExceptionHandler(BusinessLogicException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse businessLogicExceptionHandler(BusinessLogicException e) {
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentTypeMismatchExceptionHandler(
        MethodArgumentNotValidException e) {
        return ErrorResponse.of(e.getBindingResult());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingServletRequestParameterExceptionHandler(
        MissingServletRequestParameterException e) {
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentExceptionHandler(IllegalArgumentException e) {
        return ErrorResponse.of(e.getMessage());
    }
}



