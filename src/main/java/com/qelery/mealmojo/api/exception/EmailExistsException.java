package com.qelery.mealmojo.api.exception;

public class EmailExistsException extends RuntimeException {

    public EmailExistsException(String email) {
        super("User already exists with email " + email);
    }
}
