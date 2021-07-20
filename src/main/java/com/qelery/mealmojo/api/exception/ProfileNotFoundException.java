package com.qelery.mealmojo.api.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException() {
        super("Could not find profile for logged in user");
    }
}
