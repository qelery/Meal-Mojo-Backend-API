package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.*;
import com.qelery.mealmojo.api.model.dto.OrderDtoIn;
import com.qelery.mealmojo.api.model.dto.OrderDtoOut;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final MapperUtils mapperUtils;

    @Autowired
    public OrderService(RestaurantRepository restaurantRepository,
                        MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository,
                        MapperUtils mapperUtils) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.mapperUtils = mapperUtils;
    }

    public List<OrderDtoOut> getOrders(Long restaurantId) {
        Role role = getLoggedInUserRole();
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

    public OrderDtoOut getSingleOrder(Long orderId) {
        Role role = getLoggedInUserRole();
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

    public OrderDtoOut submitOrder(OrderDtoIn orderDtoIn) {
        CustomerProfile customerProfile = getLoggedInCustomerProfile();
        List<OrderLine> orderLines = new ArrayList<>();
        orderDtoIn.getMenuItemQuantitiesMap().forEach((menuItemId, quantity) -> {
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
        Restaurant restaurant = orderLines.get(0).getMenuItem().getRestaurant();
        order.setOrderLines(orderLines);
        order.setCustomerProfile(customerProfile);
        order.setRestaurant(restaurant);
        order.setDeliveryFee(restaurant.getDeliveryFee());

        orderRepository.save(order);
        return mapperUtils.map(order, OrderDtoOut.class);
    }

    public OrderDtoOut markOrderComplete(Long orderId) {
        Optional<Order> optionalOrder = getLoggedInUserMerchantProfile()
                .getRestaurantsOwned()
                .stream()
                .map(Restaurant::getOrders)
                .flatMap(Collection::stream)
                .filter(o -> o.getId().equals(orderId))
                .findFirst();
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setIsCompleted(true);
        orderRepository.save(order);
        return mapperUtils.map(order, OrderDtoOut.class);
    }

    private Restaurant getRestaurantByMerchantProfile(Long restaurantId) {
        MerchantProfile merchantProfile = getLoggedInUserMerchantProfile();
        Optional<Restaurant> optionalRestaurant = merchantProfile.getRestaurantsOwned()
                .stream()
                .filter(restaurant -> restaurant.getId().equals(restaurantId))
                .findFirst();
        return optionalRestaurant.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private List<OrderDtoOut> getAllMerchantOrders() {
        MerchantProfile merchantProfile = getLoggedInUserMerchantProfile();
        List<Restaurant> ownedRestaurants = merchantProfile.getRestaurantsOwned();
        List<Order> orders =  ownedRestaurants.stream()
                .map(Restaurant::getOrders)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return mapperUtils.mapAll(orders, OrderDtoOut.class);
    }

    private List<OrderDtoOut> getMerchantOrdersByRestaurantId(Long restaurantId) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        return mapperUtils.mapAll(restaurant.getOrders(), OrderDtoOut.class);
    }

    private List<OrderDtoOut> getAllCustomerOrders() {
        CustomerProfile customerProfile = getLoggedInCustomerProfile();
        List<Order> orders = customerProfile.getPlacedOrders();
        return mapperUtils.mapAll(orders, OrderDtoOut.class);
    }

    private List<OrderDtoOut> getCustomerOrdersByRestaurantId(Long restaurantId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        Restaurant restaurant = optionalRestaurant.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        CustomerProfile customerProfile = getLoggedInCustomerProfile();
        List<Order> orders = customerProfile.getPlacedOrders()
                .stream().filter(order -> order.getRestaurant().getId().equals(restaurant.getId()))
                .collect(Collectors.toList());
        return mapperUtils.mapAll(orders, OrderDtoOut.class);
    }

    private List<OrderDtoOut> getAllOrdersByRestaurantId(Long restaurantId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        Restaurant restaurant = optionalRestaurant.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        return mapperUtils.mapAll(restaurant.getOrders(), OrderDtoOut.class);
    }

    private List<OrderDtoOut> getAllOrders() {
        List<Order> allOrders = orderRepository.findAll();
        return mapperUtils.mapAll(allOrders, OrderDtoOut.class);
    }

    private OrderDtoOut getOrderByIdForLoggedInMerchant(Long orderId) {
        Optional<Order> optionalOrder = getLoggedInUserMerchantProfile()
                .getRestaurantsOwned()
                .stream()
                .map(Restaurant::getOrders)
                .flatMap(Collection::stream)
                .filter(o -> o.getId().equals(orderId))
                .findFirst();
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        return mapperUtils.map(order, OrderDtoOut.class);
    }

    private OrderDtoOut getOrderForLoggedInCustomer(Long orderId) {
        Optional<Order> optionalOrder = getLoggedInCustomerProfile()
                .getPlacedOrders()
                .stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst();
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        return  mapperUtils.map(order, OrderDtoOut.class);
    }

    private OrderDtoOut getOrderById(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        return  mapperUtils.map(order, OrderDtoOut.class);
    }

    private MerchantProfile getLoggedInUserMerchantProfile() {
        User user = (User) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        MerchantProfile merchantProfile = user.getMerchantProfile();
        if (merchantProfile == null) {
            throw new ProfileNotFoundException();
        }
        return merchantProfile;
    }

    private CustomerProfile getLoggedInCustomerProfile() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        CustomerProfile customerProfile = user.getCustomerProfile();
        if (customerProfile == null) {
            throw new ProfileNotFoundException();
        } else {
            return customerProfile;
        }
    }

    private Role getLoggedInUserRole() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return user.getRole();
    }
}
