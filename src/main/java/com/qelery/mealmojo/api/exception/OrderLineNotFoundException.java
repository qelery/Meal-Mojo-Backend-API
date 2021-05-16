package com.qelery.mealmojo.api.exception;

public class OrderLineNotFoundException extends RuntimeException {

    public OrderLineNotFoundException(Long restaurantId, Long menuItemId) {
        super("Could not find OrderLine with restaurant id " + restaurantId + " and menu item id " + menuItemId);
    }
}
