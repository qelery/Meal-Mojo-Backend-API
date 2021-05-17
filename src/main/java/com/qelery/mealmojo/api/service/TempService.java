package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.Order;
import com.qelery.mealmojo.api.model.OrderLine;
import com.qelery.mealmojo.api.model.Restaurant;
import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.repository.OrderLineRepository;
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
public class TempService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final RestaurantRepository restaurantRepository;
    private final PropertyCopier propertyCopier;

    @Autowired
    public TempService(OrderRepository orderRepository,
                       OrderLineRepository orderLineRepository,
                       RestaurantRepository restaurantRepository,
                       PropertyCopier propertyCopier) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.restaurantRepository = restaurantRepository;
        this.propertyCopier = propertyCopier;
    }


//    public Order createOrder(Long restaurantId, Order order) {
//        Restaurant restaurant = getRestaurant(restaurantId);
//        order.setRestaurant(restaurant);
//        order.setUser(getUser());
//        orderRepository.save(order);
//        System.out.println("\n\n\n\n\n AAAAAAAAAAAAAAAAAAAAA \n\n\n\n\n\n");
//        for (OrderLine orderLine: order.getOrderLines()) {
//            System.out.println("\n\n\n\n\n BBBBBBBBBBBBBBBBBBBBB \n\n\n\n\n\n");
//            orderLine.setOrder(order);
//            System.out.println("\n\n\n\n\n CCCCCCCCCCCCCCCCCCc \n\n\n\n\n\n");
//            orderLineRepository.save(orderLine);
//            System.out.println("\n\n\n\n\n DDDDDDDDDDDDDDDDDDDDDDDDDDd \n\n\n\n\n\n");
//        }
//        return orderRepository.save(order);
//    }


//    public Order addOrderLineToCart(Long restaurantId, Long menuItemId, Integer quantity) {
//
//    }

//    public Order updateOrder(Long restaurantId, Long orderId, Order newOrder) {
//        Order oldOrder = getOrderByRestaurant(restaurantId, orderId);
//        propertyCopier.copyNonNull(newOrder, oldOrder);
//        return orderRepository.save(oldOrder);
//    }
//
//    public ResponseEntity<String> changeOrderCompletionStatus(Long restaurantId, Long orderId, Boolean completionStatus) {
//        Order order = getOrderByRestaurant(restaurantId, orderId);
//        order.setCompleted(completionStatus);
//        return ResponseEntity.ok("Order marked " + (completionStatus ? "complete" : "incomplete"));
//    }

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
