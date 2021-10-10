package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService restaurantService) {
        this.orderService = restaurantService;
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getOrders(@RequestParam(name = "restaurant-id", required=false) Long restaurantId) {
        return this.orderService.getOrders(restaurantId);
    }

    @GetMapping("/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getSingleOrder(@PathVariable Long orderId) {
        return this.orderService.getSingleOrder(orderId);
    }

    @PostMapping("/orders/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto submitOrder(@RequestBody OrderCreationDto orderCreationDto) {
        return this.orderService.submitOrder(orderCreationDto);
    }

    @PatchMapping("/orders/{orderId}/complete")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto markOrderComplete(@PathVariable Long orderId) {
        return orderService.markOrderComplete(orderId);
    }
}
