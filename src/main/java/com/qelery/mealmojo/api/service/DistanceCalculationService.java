package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.RestaurantProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistanceCalculationService {

    public List<RestaurantProfile> findRestaurantsWithinDistance(double latitude, double longitude, int maxDistance) {
        return new ArrayList<>();
    }
}
