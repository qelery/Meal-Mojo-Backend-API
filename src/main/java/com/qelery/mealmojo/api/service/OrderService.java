package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.Order;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PropertyCopier propertyCopier;

    @Autowired
    public OrderService(OrderRepository orderRepository, PropertyCopier propertyCopier) {
        this.orderRepository = orderRepository;
        this.propertyCopier = propertyCopier;
    }

    public List<Order> getOrders(Long restaurantId, Long userId) {
        orderRepository.findAll().
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
