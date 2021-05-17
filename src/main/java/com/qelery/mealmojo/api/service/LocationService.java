package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.Restaurant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    public List<Restaurant> findRestaurantsWithinDistance(double latitude, double longitude, int maxDistance) {
        return new ArrayList<>();
    }


}


