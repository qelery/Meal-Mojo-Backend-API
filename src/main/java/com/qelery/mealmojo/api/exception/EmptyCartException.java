package com.qelery.mealmojo.api.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super("Cannot perform checkout with an empty cart");
    }
}
