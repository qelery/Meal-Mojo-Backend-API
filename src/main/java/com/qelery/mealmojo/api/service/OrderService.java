package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmptyOrderException;
import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.OrderCreationDto;
import com.qelery.mealmojo.api.model.dto.OrderDto;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;
    private final UserService userService;
    private final MapperUtils mapperUtils;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        MenuItemRepository menuItemRepository,
                        RestaurantService restaurantService,
                        UserService userService,
                        MapperUtils mapperUtils) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantService = restaurantService;
        this.userService = userService;
        this.mapperUtils = mapperUtils;
    }

    public List<OrderDto> getOrders(Long restaurantId) {
        Role role = userService.getLoggedInUserRole();
        switch (role) {
            case MERCHANT:
                if (restaurantId == null) {
                    return getAllMerchantOrders();
                } else {
                    return getMerchantOrdersByRestaurantId(restaurantId);
                }
            case CUSTOMER:
                if (restaurantId == null) {
                    return getAllCustomerOrders();
                } else {
                    return getCustomerOrdersByRestaurantId(restaurantId);
                }
            case ADMIN:
                if (restaurantId == null) {
                    return getAllOrders();
                } else {
                    return getAllOrdersByRestaurantId(restaurantId);
                }
            default:
                return new ArrayList<>();
        }
    }

    public OrderDto getSingleOrder(Long orderId) {
        Role role = userService.getLoggedInUserRole();
        switch (role) {
            case MERCHANT:
                return getOrderByIdForLoggedInMerchant(orderId);
            case CUSTOMER:
                return getOrderForLoggedInCustomer(orderId);
            case ADMIN:
                return getOrderById(orderId);
            default:
                throw new OrderNotFoundException(orderId);
        }
    }

    public OrderDto submitOrder(OrderCreationDto orderCreationDto) {
        CustomerProfile customerProfile = userService.getLoggedInCustomerProfile();
        List<OrderLine> orderLines = new ArrayList<>();
        orderCreationDto.getMenuItemQuantitiesMap().forEach((menuItemId, quantity) -> {
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

        Order order = mapperUtils.map(orderCreationDto, Order.class);
        Restaurant restaurant = orderLines.get(0).getMenuItem().getRestaurant();
        order.setOrderLines(orderLines);
        order.setCustomerProfile(customerProfile);
        order.setRestaurant(restaurant);
        order.setDeliveryFee(restaurant.getDeliveryFee());

        orderRepository.save(order);
        return mapperUtils.map(order, OrderDto.class);
    }

    public OrderDto markOrderComplete(Long orderId) {
        Optional<Order> optionalOrder = userService.getLoggedInUserMerchantProfile()
                .getRestaurantsOwned()
                .stream()
                .map(Restaurant::getOrders)
                .flatMap(Collection::stream)
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst();
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setIsCompleted(true);
        orderRepository.save(order);
        return mapperUtils.map(order, OrderDto.class);
    }

    private Restaurant getRestaurantByMerchantProfile(Long restaurantId) {
        MerchantProfile merchantProfile = userService.getLoggedInUserMerchantProfile();
        Optional<Restaurant> optionalRestaurant = merchantProfile.getRestaurantsOwned()
                .stream()
                .filter(restaurant -> restaurant.getRestaurantId().equals(restaurantId))
                .findFirst();
        return optionalRestaurant.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private List<OrderDto> getAllMerchantOrders() {
        MerchantProfile merchantProfile = userService.getLoggedInUserMerchantProfile();
        List<Restaurant> ownedRestaurants = merchantProfile.getRestaurantsOwned();
        List<Order> orders = ownedRestaurants.stream()
                .map(Restaurant::getOrders)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return mapperUtils.mapAll(orders, OrderDto.class);
    }

    private List<OrderDto> getMerchantOrdersByRestaurantId(Long restaurantId) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        return mapperUtils.mapAll(restaurant.getOrders(), OrderDto.class);
    }

    private List<OrderDto> getAllCustomerOrders() {
        CustomerProfile customerProfile = userService.getLoggedInCustomerProfile();
        List<Order> orders = customerProfile.getPlacedOrders();
        return mapperUtils.mapAll(orders, OrderDto.class);
    }

    private List<OrderDto> getCustomerOrdersByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantEntity(restaurantId);
        CustomerProfile customerProfile = userService.getLoggedInCustomerProfile();
        List<Order> orders = customerProfile.getPlacedOrders()
                .stream().filter(order -> order.getRestaurant().getRestaurantId().equals(restaurant.getRestaurantId()))
                .collect(Collectors.toList());
        return mapperUtils.mapAll(orders, OrderDto.class);
    }

    private List<OrderDto> getAllOrdersByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantEntity(restaurantId);
        return mapperUtils.mapAll(restaurant.getOrders(), OrderDto.class);
    }

    private List<OrderDto> getAllOrders() {
        List<Order> allOrders = orderRepository.findAll();
        return mapperUtils.mapAll(allOrders, OrderDto.class);
    }

    private OrderDto getOrderByIdForLoggedInMerchant(Long orderId) {
        Optional<Order> optionalOrder = userService.getLoggedInUserMerchantProfile()
                .getRestaurantsOwned()
                .stream()
                .map(Restaurant::getOrders)
                .flatMap(Collection::stream)
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst();
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        return mapperUtils.map(order, OrderDto.class);
    }

    private OrderDto getOrderForLoggedInCustomer(Long orderId) {
        CustomerProfile customerProfile = userService.getLoggedInCustomerProfile();
        Optional<Order> optionalOrder = customerProfile
                .getPlacedOrders()
                .stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst();
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        return mapperUtils.map(order, OrderDto.class);
    }

    private OrderDto getOrderById(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        return mapperUtils.map(order, OrderDto.class);
    }
}
