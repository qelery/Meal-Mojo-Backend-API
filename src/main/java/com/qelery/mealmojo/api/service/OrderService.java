package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmptyOrderException;
import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.ProfileNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final LocationService locationService;
    private final ObjectMapperUtils mapperUtils;

    @Autowired
    public OrderService(RestaurantRepository restaurantRepository,
                        MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository,
                        LocationService locationService,
                        ObjectMapperUtils mapperUtils) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.locationService = locationService;
        this.mapperUtils = mapperUtils;
    }

    public List<RestaurantThinDtoOut> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return mapperUtils.mapAll(restaurants, RestaurantThinDtoOut.class);
    }

    public List<RestaurantThinDtoOut> getRestaurantsWithinDistance(double latitude, double longitude, int maxDistance) {
        List<Restaurant> restaurants = locationService.findRestaurantsWithinDistance(latitude, longitude, maxDistance);
        return mapperUtils.mapAll(restaurants, RestaurantThinDtoOut.class);
    }

    public RestaurantDtoOut getRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return mapperUtils.map(restaurant.get(), RestaurantDtoOut.class);
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

    public List<MenuItemDto> getAllMenuItemsByRestaurant(Long restaurantId) {
        RestaurantDtoOut restaurant = getRestaurant(restaurantId);
        return mapperUtils.mapAll(restaurant.getMenuItems(), MenuItemDto.class);
    }

    public MenuItemDto getMenuItemByRestaurant(Long restaurantId, Long menuItemId) {
        RestaurantDtoOut restaurant = getRestaurant(restaurantId);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        MenuItem menuItem = optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        return mapperUtils.map(menuItem, MenuItemDto.class);
    }

    public List<OrderDtoOut> getPlacedOrders(Long restaurantId) {
        CustomerProfile customerProfile = getLoggedInUserProfile();
        if (restaurantId == null) {
            return mapperUtils.mapAll(customerProfile.getPlacedOrders(), OrderDtoOut.class);
        } else {
            return customerProfile.getPlacedOrders()
                    .stream().filter(order -> order.getRestaurant().getId().equals(restaurantId))
                    .map(order -> mapperUtils.map(order, OrderDtoOut.class))
                    .collect(Collectors.toList());
        }
    }

    public OrderDtoOut submitOrder(OrderDtoIn orderDtoIn) {
        List<OrderLine> orderLines = new ArrayList<>();
        orderDtoIn.getMenuItemIdQuantityMap().forEach((menuItemId, quantity) -> {
            Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(menuItemId);
            MenuItem menuItem = optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
            OrderLine orderLine = new OrderLine();
            orderLine.setMenuItem(menuItem);
            orderLine.setQuantity(quantity);
            orderLines.add(orderLine);
        });

        if (orderLines.isEmpty()) {
            throw new EmptyOrderException();
        }

        Order order = mapperUtils.map(orderDtoIn, Order.class);
        order.setCustomerProfile(getLoggedInUserProfile());
        order.setRestaurant(orderLines.get(0).getMenuItem().getRestaurant());

        orderRepository.save(order);
        return mapperUtils.map(order, OrderDtoOut.class);
    }

    private CustomerProfile getLoggedInUserProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        CustomerProfile customerProfile = userDetails.getUser().getCustomerProfile();
        if (customerProfile == null) {
            throw new ProfileNotFoundException();
        }
        else {
            return customerProfile;
        }
    }
}
