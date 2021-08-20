package com.qelery.mealmojo.api.unitTests.service.utility;

import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.Restaurant;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.service.utility.DistanceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class DistanceUtilsTest {

    @InjectMocks
    DistanceUtils distanceUtils;

    @Mock
    RestaurantRepository restaurantRepository;

    @Test
    @DisplayName("Should filter restaurants whose coordinates are within the specified distance")
    void filterWithinDistance() {
        // About 9 miles away
        Address address1 = new Address();
        address1.setLatitude(41.90062674742116);
        address1.setLongitude(-87.62507288205651);
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setAddress(address1);

        // About 15 miles away
        Address address2 = new Address();
        address2.setLatitude(41.82062674742116);
        address2.setLongitude(-87.52507288205651);
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setAddress(address2);

        List<Restaurant> activeRestaurants = List.of(restaurant1, restaurant2);


        double latitude = 41.8995656315562;
        double longitude = -87.80507288205651;
        List<Restaurant> actualRestaurants = distanceUtils.filterWithinDistance(activeRestaurants, latitude, longitude, 10);


        assertEquals(1, actualRestaurants.size());
        assertSame(restaurant1, actualRestaurants.get(0));
    }
}
