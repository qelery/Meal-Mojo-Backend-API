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

    private double distanceApartInMiles(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        double radiusOfEarth = 6373.0; // km

        latitudeA = Math.toRadians(latitudeA);
        longitudeA = Math.toRadians(longitudeA);
        latitudeB = Math.toRadians(latitudeB);
        longitudeB = Math.toRadians(longitudeB);

        double dLongitude = longitudeB - longitudeA;
        double dLatitude = latitudeB - latitudeA;
        double a = Math.pow(Math.sin(dLatitude / 2), 2) + Math.cos(latitudeA) * Math.cos(latitudeB) * Math.pow(Math.sin(dLongitude / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = radiusOfEarth * c; // km
        return distance * 0.621371; // miles
    }
}


