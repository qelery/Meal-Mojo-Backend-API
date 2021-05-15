package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.Order;
import com.qelery.mealmojo.api.model.Restaurant;
import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final PropertyCopier propertyCopier;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        PropertyCopier propertyCopier) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.propertyCopier = propertyCopier;
    }

    public List<Order> getOrders(Long restaurantId, Long userId) {
        if (restaurantId == null && userId == null) {
            return orderRepository.findAll();
        } else if (restaurantId == null) {
            return orderRepository.findAllByUserId(userId);
        } else if (userId == null) {
            return getRestaurant(restaurantId).getOrders();
        } else {
            return getRestaurant(restaurantId).getOrders()
                    .stream()
                    .filter(o -> o.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        }
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return getRestaurant(restaurantId).getOrders();
    }

    public Order getOrderByRestaurant(Long restaurantId, Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findByIdAndRestaurantId(orderId, restaurantId);
        return optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public Order createOrder(Long restaurantId, Order order) {
        Restaurant restaurant = getRestaurant(restaurantId);
        order.setRestaurant(restaurant);
        order.setUser(getUser());
        return orderRepository.save(order);
    }

    public Order updateOrder(Long restaurantId, Long orderId, Order newOrder) {
        Order oldOrder = getOrderByRestaurant(restaurantId, orderId);
        propertyCopier.copyNonNull(newOrder, oldOrder);
        return orderRepository.save(oldOrder);
    }

    public ResponseEntity<String> changeOrderCompletionStatus(Long restaurantId, Long orderId, Boolean completionStatus) {
        Order order = getOrderByRestaurant(restaurantId, orderId);
        order.setComplete(completionStatus);
        return ResponseEntity.ok("Order marked " + (completionStatus ? "complete" : "incomplete"));
    }

    private User getUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    private Restaurant getRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return restaurant.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }
}
