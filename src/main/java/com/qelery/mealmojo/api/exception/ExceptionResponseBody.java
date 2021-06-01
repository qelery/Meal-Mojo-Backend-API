package com.qelery.mealmojo.api.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Getter @Setter
public class ExceptionResponseBody {

    private final LocalDateTime timestamp = java.time.LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private String path;

    public ExceptionResponseBody(RuntimeException ex, HttpStatus httpStatus, WebRequest request) {
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = ex.getMessage();
        this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
