package com.qelery.mealmojo.api.exception;

public class EmptyOrderException extends RuntimeException {

    public EmptyOrderException() {
        super("Cannot submit order that contains no line items");
    }
}
