package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.model.enums.Cuisine;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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


    public List<Restaurant> getAllRestaurantsByOwner() {
        return restaurantRepository.findAllByUserId(getLoggedInUser().getId());
    }

    public Restaurant getSingleRestaurantByOwner(Long restaurantId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByIdAndUserId(restaurantId, getLoggedInUser().getId());
        if (optionalRestaurant.isPresent()) {
            return optionalRestaurant.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

    public Restaurant getRestaurantById(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return restaurant.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }


    public Restaurant updateRestaurant(Long restaurantId, Restaurant newRestaurant) {
        Restaurant oldRestaurant = getRestaurantByUserId(restaurantId, getLoggedInUser().getId());
        Set<Cuisine> oldCuisines = oldRestaurant.getCuisineSet();
        propertyCopier.copyNonNull(newRestaurant, oldRestaurant);
        if (newRestaurant.getCuisineSet().isEmpty()) {
            oldRestaurant.setCuisineSet(oldCuisines);
        }
        return restaurantRepository.save(oldRestaurant);
    }


    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setUser(getLoggedInUser());
        return restaurantRepository.save(restaurant);
    }


    public ResponseEntity<String> updateRestaurantHours(Long restaurantId, List<OperatingHours> newHoursList) {
        Restaurant restaurant = getRestaurantByUserId(restaurantId, getLoggedInUser().getId());

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
        Restaurant restaurant = getRestaurantByUserId(restaurantId, getLoggedInUser().getId());


        Address oldAddress = restaurant.getAddress();
        propertyCopier.copyNonNull(newAddress, oldAddress);
        addressRepository.save(oldAddress);

        return ResponseEntity.ok("Address updated");
    }

    public ResponseEntity<MenuItem> createMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = getRestaurantByUserId(restaurantId, getLoggedInUser().getId());
        menuItem.setRestaurant(restaurant);
        return new ResponseEntity<>(menuItemRepository.save(menuItem), HttpStatus.CREATED);
    }

    public ResponseEntity<MenuItem> updateMenuItem(Long restaurantId, Long menuItemId, MenuItem newMenuItem) {
        Restaurant restaurant = getRestaurantByUserId(restaurantId, getLoggedInUser().getId());
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        MenuItem oldMenuItem = optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        propertyCopier.copyNonNull(newMenuItem, oldMenuItem);
        return new ResponseEntity<>(menuItemRepository.save(oldMenuItem), HttpStatus.OK);
    }

    public List<Order> getAllOrdersForOwnedRestaurant(Long restaurantId) {
        Restaurant restaurant = getSingleRestaurantByOwner(restaurantId);
        return orderRepository.findAllByRestaurantId(restaurant.getId());
    }

    public Order getSingleOrderForOwnedRestaurant(Long restaurantId, Long orderId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        Optional<Order> optionalOrder = orderRepository.findByIdAndRestaurantId(orderId, restaurant.getId());
        return optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public ResponseEntity<Order> markOrderComplete(Long restaurantId, Long orderId) {
        Order order = getSingleOrderForOwnedRestaurant(restaurantId, orderId);
        order.setCompleted(true);
        return new ResponseEntity<>(orderRepository.save(order), HttpStatus.OK);
    }


    private Restaurant getRestaurantByUserId(Long restaurantId, Long userId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByIdAndUserId(restaurantId, userId);
        return optionalRestaurant.orElseThrow(() ->  new RestaurantNotFoundException(restaurantId));
    }


    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}
