package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.Order;
import com.qelery.mealmojo.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping(value="/orders", params="restaurantId, userId")
    public List<Order> getOrders(@RequestParam Long restaurantId,
                                 @RequestParam Long userId) {
        return this.orderService.getOrders(restaurantId, userId);
    }

    @GetMapping(value="/orders", params="restaurantId")
    public List<Order> getOrdersByRestaurant(@RequestParam Long restaurantId) {
        return this.orderService.getOrdersByRestaurant(restaurantId);
    }

    @GetMapping(value="/orders", params="userId")
    public List<Order> getOrdersByUser(@RequestParam Long userId) {
        return this.orderService.getOrdersByUser(userId);
    }

    @GetMapping("/orders/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return this.orderService.getOrder(orderId);
    }

    @PostMapping("/orders")
    public Order createOrder(@RequestBody Order order) {
        return this.orderService.createOrder(order);
    }

    @PutMapping("/orders/{orderId}")
    public Order updateOrder(@PathVariable Long orderId,
                             @RequestBody Order order) {
        return this.orderService.updateOrder(order);
    }

    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<String> changeOrderCompletionStatus(@PathVariable Long orderId,
                                                      @RequestBody Boolean completionStatus) {
        return this.orderService.changeOrderCompletionStatus(orderId, completionStatus);
    }
}
