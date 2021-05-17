package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final AddressRepository addressRepository;
    private final PropertyCopier propertyCopier;

    @Autowired
    public MerchantService(RestaurantRepository restaurantRepository,
                           MenuItemRepository menuItemRepository,
                           OrderRepository orderRepository,
                           OperatingHoursRepository operatingHoursRepository,
                           AddressRepository addressRepository,
                           PropertyCopier propertyCopier) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.addressRepository = addressRepository;
        this.propertyCopier = propertyCopier;
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setUser(getLoggedInUser());
        return restaurantRepository.save(restaurant);
    }

    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    public List<Restaurant> getAllRestaurantsOwned() {
        return restaurantRepository.findAllByUserId(getLoggedInUser().getId());
    }

    public Restaurant getRestaurantOwned(Long restaurantId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByIdAndUserId(restaurantId, getLoggedInUser().getId());
        if (optionalRestaurant.isPresent()) {
            return optionalRestaurant.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
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

    public ResponseEntity<String> updateRestaurant(Long restaurantId, Restaurant newRestaurant) {
        Restaurant oldRestaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        propertyCopier.copyNonNull(newRestaurant, oldRestaurant);
        restaurantRepository.save(oldRestaurant);
        return ResponseEntity.ok("Restaurant updated");
    }

    public ResponseEntity<String> updateRestaurantHours(Long restaurantId, List<OperatingHours> newHoursList) {
        Role roleOfLoggedInUser = getLoggedInUser().getRole();

        Restaurant restaurant;
        if (roleOfLoggedInUser.equals(Role.ADMIN)) {
            restaurant = getRestaurant(restaurantId);
        } else {
            restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        }

        for (OperatingHours newHours: newHoursList) {
            Optional<OperatingHours> hours = operatingHoursRepository.findByRestaurantIdAndDayOfWeek(restaurantId, newHours.getDayOfWeek());
            if (hours.isPresent()) {
                OperatingHours oldHours = hours.get();
                propertyCopier.copyNonNull(newHours, oldHours);
                operatingHoursRepository.save(oldHours);
            } else {
                newHours.setRestaurant(restaurant);
                operatingHoursRepository.save(newHours);
            }
        }
        return ResponseEntity.ok("Hours updated");
    }

    public ResponseEntity<String> updateRestaurantAddress(Long restaurantId, Address newAddress) {
        Role roleOfLoggedInUser = getLoggedInUser().getRole();

        Restaurant restaurant;
        if (roleOfLoggedInUser.equals(Role.ADMIN)) {
            restaurant = getRestaurant(restaurantId);
        } else {
            restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        }

        Address oldAddress = restaurant.getAddress();
        propertyCopier.copyNonNull(newAddress, oldAddress);
        addressRepository.save(oldAddress);

        return ResponseEntity.ok("Address updated");
    }

    public MenuItem createMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }

    public MenuItem getMenuItemByRestaurantAndUser(Long menuItemId, Long restaurantId, Long userId) {
        Restaurant restaurant = getRestaurantByUser(restaurantId, userId);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        return optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
    }

    public MenuItem getMenuItemByRestaurant(Long menuItemId, Long restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        return optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
    }


    public ResponseEntity<String> updateMenuItem(Long restaurantId, Long menuItemId, MenuItem newMenuItem) {
        Role roleOfLoggedInUser = getLoggedInUser().getRole();

        MenuItem oldMenuItem;
        if (roleOfLoggedInUser.equals(Role.ADMIN)) {
            oldMenuItem = getMenuItemByRestaurant(menuItemId, restaurantId); // handles RestaurantNotFound and MenuItemNotFound exceptions
        } else {
            oldMenuItem = getMenuItemByRestaurantAndUser(menuItemId, restaurantId, getLoggedInUser().getId()); // handles RestaurantNotFound and MenuItemNotFound exceptions
        }
        propertyCopier.copyNonNull(newMenuItem, oldMenuItem);
        menuItemRepository.save(oldMenuItem);
        return ResponseEntity.ok("Menu Item updated");
    }

    public List<Order> getOwnedRestaurantOrders(Long restaurantId) {
        Restaurant restaurant = getRestaurantOwned(restaurantId);
        return orderRepository.findAllByRestaurantId(restaurant.getId());
    }

    public Order getOwnedRestaurantOrder(Long restaurantId, Long orderId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        Optional<Order> optionalOrder = orderRepository.findByIdAndRestaurantId(orderId, restaurant.getId());
        return optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public ResponseEntity<String> markOrderComplete(Long restaurantId, Long orderId) {

    }
}
