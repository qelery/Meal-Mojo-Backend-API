package com.qelery.mealmojo.api.exception;

public class ProhibitedByRoleException extends RuntimeException {

    public ProhibitedByRoleException() {
        super("User's role does not allow for requested action");
    }
}