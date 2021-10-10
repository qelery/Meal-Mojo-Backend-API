package com.qelery.mealmojo.api.exception.global;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponseBody {

    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponseBody(RuntimeException ex, HttpStatus httpStatus, WebRequest request) {
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = ex.getMessage();
        this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
