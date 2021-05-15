package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.MenuItem;
import com.qelery.mealmojo.api.model.RestaurantProfile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    public List<RestaurantProfile> getRestaurants() {
        return new ArrayList<>();
    }

    public RestaurantProfile getRestaurant(Long restaurantId) {
    }

    public RestaurantProfile createRestaurant(RestaurantProfile restaurantProfile) {
    }

    public RestaurantProfile updateRestaurant(RestaurantProfile restaurantProfile) {
    }

    public ResponseEntity<String> deleteRestaurant(Long restaurantId) {
    }

    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
    }

    public MenuItem getMenuItemByRestuarant(Long restaurantId, Long menuitemsId) {
    }

    public MenuItem createMenuItem(Long restaurantId, Long menuitemsId) {
    }

    public MenuItem updateMenuItem(Long restaurantId, Long menuitemsId) {
    }

    public MenuItem changeMenuItemAvailability(Long restaurantId, Long menuitemsId) {
    }
}
