package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.repository.AddressRepository;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OperatingHoursRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final AddressRepository addressRepository;
    private final MenuItemRepository menuItemRepository;
    private final LocationService locationService;
    private final PropertyCopier propertyCopier;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository,
                             OperatingHoursRepository operatingHoursRepository,
                             AddressRepository addressRepository,
                             MenuItemRepository menuItemRepository,
                             LocationService locationService,
                             PropertyCopier propertyCopier) {
        this.restaurantRepository = restaurantRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.addressRepository = addressRepository;
        this.menuItemRepository = menuItemRepository;
        this.locationService = locationService;
        this.propertyCopier = propertyCopier;
    }

    public List<Restaurant> getRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsWithinDistance(double latitude, double longitude, int maxDistance) {
        return locationService.findRestaurantsWithinDistance(latitude, longitude, maxDistance);
    }

    public Restaurant getRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return restaurant.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

    public Restaurant getRestaurantByUser(Long restaurantId, Long userId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByIdAndUserId(restaurantId, userId);
        return optionalRestaurant.orElseThrow(() ->  new RestaurantNotFoundException(restaurantId));
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setUser(getLoggedInUser());
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long restaurantId, Restaurant newRestaurant) {
        Restaurant oldRestaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        propertyCopier.copyNonNull(newRestaurant, oldRestaurant);
        return restaurantRepository.save(oldRestaurant);
    }

    public Restaurant updateRestaurantHours(Long restaurantId, List<OperatingHours> newHoursList) {
        Restaurant restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());

        for (OperatingHours newHours: newHoursList) {
            Optional<OperatingHours> hours = operatingHoursRepository.findByRestaurantIdAndDayOfWeek(restaurantId, newHours.getDayOfWeek());
            if (hours.isPresent()) {
                OperatingHours oldHours = hours.get();
                oldHours.setOpenTime(newHours.getOpenTime());
                oldHours.setCloseTime(newHours.getCloseTime());
                operatingHoursRepository.save(oldHours);
            }
        }
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurantAddress(Long restaurantId, Address newAddress) {
        Restaurant restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());

        Address oldAddress = restaurant.getAddress();
        propertyCopier.copyNonNull(newAddress, oldAddress);
        addressRepository.save(oldAddress);

        return restaurantRepository.save(restaurant);
    }

    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId); // handles RestaurantNotFoundException
        return restaurant.getMenuItems();
    }

    public MenuItem getMenuItemByRestaurant(Long restaurantId, Long menuItemId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        return optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
    }

    public MenuItem createMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Long restaurantId, Long menuItemId, MenuItem newMenuItem) {
        MenuItem oldMenuItem = getMenuItemByRestaurant(restaurantId, menuItemId); // handles RestaurantNotFound and MenuItemNotFound exceptions
        propertyCopier.copyNonNull(newMenuItem, oldMenuItem);
        return menuItemRepository.save(oldMenuItem);
    }

    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}
