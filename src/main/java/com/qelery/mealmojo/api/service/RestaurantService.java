package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.RestaurantProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    public List<RestaurantProfile> getRestaurants() {
        return new ArrayList<>();
    }
}
