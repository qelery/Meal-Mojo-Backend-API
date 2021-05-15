package com.qelery.mealmojo.api.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(Long id) {
        super("Could not find Restaurant with id " + id);
    }
}
