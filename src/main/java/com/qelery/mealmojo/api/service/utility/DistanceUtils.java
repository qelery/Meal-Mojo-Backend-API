package com.qelery.mealmojo.api.service.utility;

import com.qelery.mealmojo.api.model.entity.Restaurant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistanceUtils {

    public List<Restaurant> filterWithinDistance(List<Restaurant> restaurants,
                                                 double originLatitude,
                                                 double originLongitude,
                                                 int maxDistanceMiles) {
        return restaurants.stream().filter(restaurant -> {
            if (restaurant.getAddress() == null) return false;
            double restaurantLongitude = restaurant.getAddress().getLongitude();
            double restaurantLatitude = restaurant.getAddress().getLatitude();
            double distance = distanceApartInMiles(originLatitude, originLongitude, restaurantLatitude, restaurantLongitude);
            return distance <= maxDistanceMiles;
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

        double distanceInKilometers = radiusOfEarth * c;
        return distanceInKilometers * 0.621371;
    }
}


