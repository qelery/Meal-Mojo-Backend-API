package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.MenuItem;
import com.qelery.mealmojo.api.model.RestaurantProfile;
import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.RestaurantProfileRepository;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantProfileRepository restaurantProfileRepository;
    private final MenuItemRepository menuItemRepository;
    private final DistanceCalculationService distanceCalculationService;

    @Autowired
    public RestaurantService(RestaurantProfileRepository restaurantProfileRepository,
                             MenuItemRepository menuItemRepository,
                             DistanceCalculationService distanceCalculationService) {
        this.restaurantProfileRepository = restaurantProfileRepository;
        this.menuItemRepository = menuItemRepository;
        this.distanceCalculationService = distanceCalculationService;
    }


//    public List<RestaurantProfile> getRestaurants(double latitude, double longitude, int maxDistance) {
//        return distanceCalculationService.findRestaurantsWithinDistance(latitude, longitude, maxDistance);
//    }

    public List<RestaurantProfile> getRestaurants() {
        return restaurantProfileRepository.findAll();
    }

    public RestaurantProfile getRestaurant(Long restaurantId) {
        Optional<RestaurantProfile> restaurantProfile = restaurantProfileRepository.findById(restaurantId);
        if (restaurantProfile.isPresent()) {
            return restaurantProfile.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

    public RestaurantProfile createRestaurant(RestaurantProfile restaurantProfile) {
        restaurantProfile.setUser(getUser());
        return restaurantProfileRepository.save(restaurantProfile);
    }

    public RestaurantProfile updateRestaurant(RestaurantProfile restaurantProfile, Long restaurantId) {
        RestaurantProfile oldRestaurantProfile = this.getRestaurant(restaurantId);
        oldRestaurantProfile.setBusinessName(restaurantProfile.getBusinessName());
        oldRestaurantProfile.setDescription(restaurantProfile.getBusinessName());
        oldRestaurantProfile.setTimeZone(restaurantProfile.getTimeZone());
        oldRestaurantProfile.setDeliveryAvailable(restaurantProfile.getDeliveryAvailable());
        oldRestaurantProfile.setDeliveryFee(restaurantProfile.getDeliveryFee());
        oldRestaurantProfile.setDeliveryEtaMinutes(restaurantProfile.getDeliveryEtaMinutes());
        oldRestaurantProfile.setPickupEtaMinutes(restaurantProfile.getPickupEtaMinutes());
        oldRestaurantProfile.setCuisineSet(restaurantProfile.getCuisineSet());
        return restaurantProfile;
    }

    public ResponseEntity<String> deleteRestaurant(Long restaurantId) {
        return ResponseEntity.ok("");
    }

    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        return new ArrayList<>();
    }

    public MenuItem getMenuItemByRestaurant(Long restaurantId, Long menuitemsId) {
        return new MenuItem();
    }

    public MenuItem createMenuItem(Long restaurantId, Long menuitemsId) {
        return new MenuItem();
    }

    public MenuItem updateMenuItem(Long restaurantId, Long menuitemsId) {
        return new MenuItem();
    }

    public MenuItem changeMenuItemAvailability(Long restaurantId, Long menuitemsId) {
        return new MenuItem();
    }

    private User getUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}
