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


    @GetMapping(value="/orders")
    public List<Order> getOrders(@RequestParam(required=false) Long restaurantId,
                                 @RequestParam(required=false) Long userId) {
        return this.orderService.getOrders(restaurantId, userId);
    }

    @GetMapping(value="/restaurants/{restaurantId}/orders")
    public List<Order> getOrdersByRestaurant(@PathVariable Long restaurantId) {
        return this.orderService.getOrdersByRestaurant(restaurantId);
    }

    // Is this needed ????
    // Is this needed ????
    // Is this needed ???
    // Is this needed ???
    @GetMapping("/restaurants/{restaurantId}/orders/{orderId}")
    public Order getOrderByRestaurant(@PathVariable Long restaurantId,
                          @PathVariable Long orderId) {
        return this.orderService.getOrderByRestaurant(restaurantId, orderId);
    }

    // Is this needed ????
    // Is this needed ????
    // Is this needed ????
    // Is this needed ????
    @PostMapping("/restaurants/{restaurantId}/orders")
    public Order createOrder(@PathVariable Long restaurantId,
                             @RequestBody Order order) {
        return this.orderService.createOrder(restaurantId, order);
    }

//    @PostMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines/{quantity}")
//    public Order addOrderLineToCart(@PathVariable Long restaurantId,
//                                    @PathVariable Long menuItemId,
//                                    @PathVariable Integer quantity) {
//        return this.orderService.addOrderLineToCart(restaurantId, menuItemId, quantity);
//    }

    @PutMapping("/restaurants/{restaurantId}/orders/{orderId}")
    public Order updateOrder(@PathVariable Long restaurantId,
                             @PathVariable Long orderId,
                             @RequestBody Order order) {
        return this.orderService.updateOrder(restaurantId, orderId, order);
    }

    @PatchMapping("/restaurants/{restaurantId}/orders/{orderId}")
    public ResponseEntity<String> changeOrderCompletionStatus(@PathVariable Long restaurantId,
                                                              @PathVariable Long orderId,
                                                              @RequestBody Boolean completionStatus) {
        return this.orderService.changeOrderCompletionStatus(restaurantId, orderId, completionStatus);
    }
}
