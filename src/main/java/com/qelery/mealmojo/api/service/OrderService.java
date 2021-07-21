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
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(RestaurantRepository restaurantRepository,
                        MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository,
                        LocationService locationService,
                        ModelMapper modelMapper) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    public List<RestaurantThinDtoOut> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantThinDtoOut.class))
                .collect(Collectors.toList());
    }

    public List<RestaurantThinDtoOut> getRestaurantsWithinDistance(double latitude, double longitude, int maxDistance) {
        List<Restaurant> restaurants = locationService.findRestaurantsWithinDistance(latitude, longitude, maxDistance);
        return restaurants.stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantThinDtoOut.class))
                .collect(Collectors.toList());
    }

    public RestaurantDtoOut getRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return modelMapper.map(restaurant.get(), RestaurantDtoOut.class);
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

    public List<MenuItemDto> getAllMenuItemsByRestaurant(Long restaurantId) {
        RestaurantDtoOut restaurant = getRestaurant(restaurantId);
        return restaurant.getMenuItems()
                .stream().map(menuItem -> modelMapper.map(menuItem, MenuItemDto.class))
                .collect(Collectors.toList());
    }

    public MenuItemDto getMenuItemByRestaurant(Long restaurantId, Long menuItemId) {
        RestaurantDtoOut restaurant = getRestaurant(restaurantId);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        MenuItem menuItem = optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        return modelMapper.map(menuItem, MenuItemDto.class);
    }

    public List<OrderDtoOut> getPlacedOrders(Long restaurantId) {
        CustomerProfile customerProfile = getLoggedInUserProfile();
        if (restaurantId == null) {
            return customerProfile.getPlacedOrders().stream().map(order -> modelMapper.map(order, OrderDtoOut.class)).collect(Collectors.toList());
        } else {
            return customerProfile.getPlacedOrders()
                    .stream().filter(order -> order.getRestaurant().getId().equals(restaurantId))
                    .map(order -> modelMapper.map(order, OrderDtoOut.class))
                    .collect(Collectors.toList());
        }
    }

    public OrderDtoOut submitOrder(OrderDtoIn orderDtoIn) {
        List<OrderLine> orderLines = new ArrayList<>();
        orderDtoIn.getMenuItemQuantityMap().forEach((menuItemId, quantity) -> {
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

        Order order = modelMapper.map(orderDtoIn, Order.class);
        order.setCustomerProfile(getLoggedInUserProfile());
        order.setRestaurant(orderLines.get(0).getMenuItem().getRestaurant());

        orderRepository.save(order);
        return modelMapper.map(order, OrderDtoOut.class);
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
