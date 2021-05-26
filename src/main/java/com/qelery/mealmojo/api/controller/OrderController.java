package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/order/cart")
    public List<OrderLine> getCart() {
        return this.orderService.getCart();
    }


    @PostMapping("/order/cart/checkout")
    public Order checkoutCart(@RequestBody Order order) {
        return this.orderService.checkoutCart(order);
    }

    @DeleteMapping("/order/cart/clear")
    public ResponseEntity<Void> clearCart() {
        return this.orderService.clearCart();
    }

    @GetMapping("/order/restaurants")
    public List<Restaurant> getRestaurants() {
        return orderService.getRestaurants();
    }

    @GetMapping(value="/order/restaurants", params={"latitude", "longitude", "maxDistance"})
    public List<Restaurant> getRestaurantsWithinDistance(@RequestParam double latitude,
                                              @RequestParam double longitude,
                                              @RequestParam(defaultValue="15") int maxDistance) {
        return orderService.getRestaurantsWithinDistance(latitude, longitude, maxDistance);
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
    public ResponseEntity<OrderLine> addOrderLineToCart(@PathVariable Long restaurantId,
                                        @PathVariable Long menuItemId,
                                        @PathVariable Integer quantity) {
        OrderLine orderLine = this.orderService.addOrderLineToCart(restaurantId, menuItemId, quantity);
        return new ResponseEntity<>(orderLine, HttpStatus.CREATED);
    }

    @PutMapping("/order/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines/{quantity}")
    public OrderLine editOrderLineInCart(@PathVariable Long restaurantId,
                                         @PathVariable Long menuItemId,
                                         @PathVariable Integer quantity) {
        return this.orderService.editOrderLineInCart(restaurantId, menuItemId, quantity);
    }

    @DeleteMapping("/order/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines")
    public ResponseEntity<Void> deleteOrderLineFromCart(@PathVariable Long restaurantId,
                                                          @PathVariable Long menuItemId) {
        return this.orderService.deleteOrderLineFromCart(restaurantId, menuItemId);
    }

    @GetMapping("/order/past")
    public List<Order> getOrders(@RequestParam(required=false) Long restaurantId) {
        return this.orderService.getOrders(restaurantId);
    }
}
