package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.Restaurant;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public LocationService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> findRestaurantsWithinDistance(double originLatitude, double originLongitude, int maxDistance) {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        return allRestaurants.stream().filter(restaurant -> {
            double restaurantLongitude = restaurant.getAddress().getLongitude();
            double restaurantLatitude = restaurant.getAddress().getLatitude();
            double distance = distanceApartInMiles(originLatitude, originLongitude, restaurantLatitude, restaurantLongitude);
            return distance <= maxDistance;
        }).collect(Collectors.toList());
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
        System.out.println(radiusOfEarth * c);
        return distance * 0.621371; // miles
    }
}


