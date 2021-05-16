package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.service.OrderService;
import com.qelery.mealmojo.api.service.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService restaurantService) {
        this.orderService = restaurantService;
    }

    @PostMapping("/order/cart/checkout")
    public Order checkoutCart(@RequestBody Order order) {
        return this.orderService.checkoutCart(order);
    }

    @PostMapping("/order/cart/clear")
    public ResponseEntity<String> clearCart() {
        return this.orderService.clearCart();
    }

    @GetMapping("/order/restaurants")
    public List<Restaurant> getRestaurants() {
        return orderService.getRestaurants();
    }

    @GetMapping("/order/restaurants/{restaurantId}")
    public Restaurant getRestaurant(@PathVariable Long restaurantId) {
        return orderService.getRestaurant(restaurantId);
    }

    @GetMapping("/order/restaurants/{restaurantId}/menuitems")
    public List<MenuItem> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return orderService.getMenuItemsByRestaurant(restaurantId);
    }

    @GetMapping("/order/restaurants/{restaurantId}/menuitems/{menuitemId}")
    public MenuItem getMenuItemByRestaurant(@PathVariable Long restaurantId,
                                            @PathVariable Long menuitemId) {
        return orderService.getMenuItemByRestaurant(restaurantId, menuitemId);
    }

    @PostMapping("/order/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines/{quantity}")
    public OrderLine addOrderLineToCart(@PathVariable Long restaurantId,
                                        @PathVariable Long menuItemId,
                                        @PathVariable Integer quantity) {
        return this.orderService.addOrderLineToCart(restaurantId, menuItemId, quantity);
    }

    @PutMapping("/order/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines/{quantity}")
    public OrderLine editOrderLineInCart(@PathVariable Long restaurantId,
                                         @PathVariable Long menuItemId,
                                         @PathVariable Integer quantity) {
        return this.orderService.editOrderLineInCart(restaurantId, menuItemId, quantity);
    }

    @DeleteMapping("/order/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines")
    public ResponseEntity<String> deleteOrderLineFromCart(@PathVariable Long restaurantId,
                                                          @PathVariable Long menuItemId) {
        return this.orderService.deleteOrderLineFromCart(restaurantId, menuItemId);
    }

    @GetMapping("/order/orders")
    public List<Order> getOrders(@RequestParam(required=false) Long restaurantId,
                                 @RequestParam(required=false) Long userId) {
        return this.orderService.getOrders(restaurantId, userId);
    }

}
