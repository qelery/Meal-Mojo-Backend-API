package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    public List<Order> getOrders(Long restaurantId, Long userId) {
        return new ArrayList<>();
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return new ArrayList<>();
    }

    public List<Order> getOrdersByUser(Long userId) {
        return new ArrayList<>();
    }

    public Order getOrder(Long orderId) {
        return new Order();
    }

    public Order createOrder(Order order) {
    }

    public Order updateOrder(Order order) {
    }

    public ResponseEntity<String> changeOrderCompletionStatus(Long orderId, Boolean completionStatus) {
    }
}
