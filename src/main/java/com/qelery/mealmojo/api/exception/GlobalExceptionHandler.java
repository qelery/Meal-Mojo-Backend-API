package com.qelery.mealmojo.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Object> handleException(RestaurantNotFoundException ex, WebRequest request)  {
        ExceptionResponseBody body = new ExceptionResponseBody(ex, HttpStatus.NOT_FOUND, request);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<Object> handleException(MenuItemNotFoundException ex, WebRequest request) {
        ExceptionResponseBody body = new ExceptionResponseBody(ex, HttpStatus.NOT_FOUND, request);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleException(OrderNotFoundException ex, WebRequest request) {
        ExceptionResponseBody body = new ExceptionResponseBody(ex, HttpStatus.NOT_FOUND, request);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderLineNotFoundException.class)
    public ResponseEntity<Object> handleException(OrderLineNotFoundException ex, WebRequest request) {
        ExceptionResponseBody body = new ExceptionResponseBody(ex, HttpStatus.NOT_FOUND, request);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<Object> handleException(EmptyCartException ex, WebRequest request) {
        ExceptionResponseBody body = new ExceptionResponseBody(ex, HttpStatus.BAD_REQUEST, request);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleException(BadCredentialsException ex, WebRequest request) {
        ExceptionResponseBody body = new ExceptionResponseBody(ex, HttpStatus.FORBIDDEN, request);
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Object> handleException(EmailExistsException ex, WebRequest request) {
        ExceptionResponseBody body = new ExceptionResponseBody(ex, HttpStatus.CONFLICT, request);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleException(DataIntegrityViolationException ex) {
        if (Objects.requireNonNull(ex.getMessage()).contains("not-null property references a null")) {
            return new ResponseEntity<>("Bad request. Check that you supplied all non-null properties", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
