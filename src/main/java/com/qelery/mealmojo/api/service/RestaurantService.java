package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OperatingHoursRepository;
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
    private final OperatingHoursRepository operatingHoursRepository;
    private final MenuItemRepository menuItemRepository;
    private final LocationService locationService;


    @Autowired
    public RestaurantService(RestaurantProfileRepository restaurantProfileRepository,
                             OperatingHoursRepository operatingHoursRepository,
                             MenuItemRepository menuItemRepository,
                             LocationService locationService) {
        this.restaurantProfileRepository = restaurantProfileRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.menuItemRepository = menuItemRepository;
        this.locationService = locationService;
    }

    public List<RestaurantProfile> getRestaurants() {
        return restaurantProfileRepository.findAll();
    }

//    public List<RestaurantProfile> getRestaurantsWithinDistance(double latitude, double longitude, int maxDistance) {
//        return distanceCalculationService.findRestaurantsWithinDistance(latitude, longitude, maxDistance);
//    }

    public RestaurantProfile getRestaurant(Long restaurantId) {
        Optional<RestaurantProfile> restaurantProfile = restaurantProfileRepository.findById(restaurantId);
        if (restaurantProfile.isPresent()) {
            return restaurantProfile.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

    public RestaurantProfile getRestaurantByUser(Long restaurantId, Long userId) {
        Optional<RestaurantProfile> restaurantProfile = restaurantProfileRepository.findByIdAndUserId(restaurantId, userId);
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

    public RestaurantProfile updateRestaurantBasicInfo(Long restaurantId, RestaurantProfile restaurantProfile) {
        RestaurantProfile oldRestaurantProfile = getRestaurantByUser(restaurantId, getUser().getId());

        oldRestaurantProfile.setBusinessName(restaurantProfile.getBusinessName());
        oldRestaurantProfile.setDescription(restaurantProfile.getBusinessName());
        oldRestaurantProfile.setTimeZone(restaurantProfile.getTimeZone());
        oldRestaurantProfile.setDeliveryAvailable(restaurantProfile.getDeliveryAvailable());
        oldRestaurantProfile.setDeliveryFee(restaurantProfile.getDeliveryFee());
        oldRestaurantProfile.setDeliveryEtaMinutes(restaurantProfile.getDeliveryEtaMinutes());
        oldRestaurantProfile.setPickupEtaMinutes(restaurantProfile.getPickupEtaMinutes());
        oldRestaurantProfile.setCuisineSet(restaurantProfile.getCuisineSet());
        return restaurantProfileRepository.save(oldRestaurantProfile);
    }

    public RestaurantProfile updateRestaurantHours(Long restaurantId, List<OperatingHours> newHoursList) {
        RestaurantProfile restaurantProfile = getRestaurantByUser(restaurantId, getUser().getId());

        for (OperatingHours newHours: newHoursList) {
            Optional<OperatingHours> hours = operatingHoursRepository.findByRestaurantProfileIdAndDayOfWeek(restaurantId, newHours.getDayOfWeek());
            if (hours.isPresent()) {
                OperatingHours oldHours = hours.get();
                oldHours.setOpenTime(newHours.getOpenTime());
                oldHours.setCloseTime(newHours.getCloseTime());
                operatingHoursRepository.save(oldHours);
            }
        }
        return restaurantProfileRepository.save(restaurantProfile);
    }

    public RestaurantProfile updateRestaurantAddress(Long restaurantId, Address newAddress) {
        RestaurantProfile restaurantProfile = getRestaurantByUser(restaurantId, getUser().getId());
        Address oldAddress = restaurantProfile.getAddress();

        oldAddress.setStreet1(newAddress.getStreet1());
        oldAddress.setStreet2(newAddress.getStreet2());
        oldAddress.setCity(newAddress.getCity());
        oldAddress.setZipcode(newAddress.getZipcode());
        oldAddress.setLatitude(newAddress.getLatitude());
        oldAddress.setLongitude(newAddress.getLongitude());
        add

        return restaurantProfileRepository.save(restaurantProfile);
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
