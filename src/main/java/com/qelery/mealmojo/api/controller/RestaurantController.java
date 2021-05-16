package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    private RestaurantService restaurantService;

    @Autowired
    public void setRestaurantService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }


    // Restaurant endpoints

    @GetMapping("/restaurants")
    public List<Restaurant> getRestaurants() {
        return restaurantService.getRestaurants();
    }

//    @GetMapping("/restaurants")
//    public List<Restaurant> getRestaurantsWithinDistance(@RequestParam double latitude,
//                                                  @RequestParam double longitude,
//                                                  @RequestParam(defaultValue="15") int maxDistance) {
//        return restaurantService.getRestaurantsWithinDistance(latitude, longitude, maxDistance);
//    }

    @GetMapping("/restaurants/{restaurantId}")
    public Restaurant getRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.getRestaurant(restaurantId);
    }

    @PostMapping("/restaurants")
    public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.createRestaurant(restaurant);
    }

    @PutMapping("/restaurants/{restaurantId}")
    public ResponseEntity<String> updateRestaurant(@PathVariable Long restaurantId,
                                       @RequestBody Restaurant restaurant) {
        return restaurantService.updateRestaurant(restaurantId, restaurant);
    }

    @PatchMapping("/restaurants/{restaurantId}/hours")
    public ResponseEntity<String> updateRestaurantHours(@PathVariable Long restaurantId,
                                                        @RequestBody List<OperatingHours> hoursList) {
        return restaurantService.updateRestaurantHours(restaurantId, hoursList);
    }

    @PatchMapping("/restaurants/{restaurantId}/address")
    public ResponseEntity<String> updateRestaurantAddress(@PathVariable Long restaurantId,
                                                          @RequestBody Address address) {
        return restaurantService.updateRestaurantAddress(restaurantId, address);
    }


    // Menu Item endpoints

    @GetMapping("/restaurants/{restaurantId}/menuitems")
    public List<MenuItem> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.getMenuItemsByRestaurant(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/menuitems/{menuitemId}")
    public MenuItem getMenuItemByRestaurant(@PathVariable Long restaurantId,
                                            @PathVariable Long menuitemId) {
        return restaurantService.getMenuItemByRestaurant(restaurantId, menuitemId);
    }

    @PostMapping("/restaurants/{restaurantId}/menuitems")
    public MenuItem createMenuItem(@PathVariable Long restaurantId,
                                   @RequestBody MenuItem menuItem) {
        return restaurantService.createMenuItem(restaurantId, menuItem);
    }

    @PutMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}")
    public ResponseEntity<String> updateMenuItem(@PathVariable Long restaurantId,
                                   @PathVariable Long menuItemId,
                                   @RequestBody MenuItem menuItem) {
        return restaurantService.updateMenuItem(restaurantId, menuItemId, menuItem);
    }

    @PostMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines/{quantity}")
    public OrderLine addOrderLineToCart(@PathVariable Long restaurantId,
                                    @PathVariable Long menuItemId,
                                    @PathVariable Integer quantity) {
        return this.restaurantService.addOrderLineToCart(restaurantId, menuItemId, quantity);
    }

    @PutMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines/{quantity}")
    public OrderLine editOrderLineInCart(@PathVariable Long restaurantId,
                                        @PathVariable Long menuItemId,
                                        @PathVariable Integer quantity) {
        return this.restaurantService.editOrderLineInCart(restaurantId, menuItemId, quantity);
    }

    @DeleteMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines")
    public ResponseEntity<String> editOrderLineFromCart(@PathVariable Long restaurantId,
                                                        @PathVariable Long menuItemId) {
        return this.restaurantService.deleteOrderLineFromCart(restaurantId, menuItemId);
    }

    @GetMapping(value="/orders")
    public List<Order> getOrders(@RequestParam(required=false) Long restaurantId,
                                 @RequestParam(required=false) Long userId) {
        return this.restaurantService.getOrders(restaurantId, userId);
    }

    @PostMapping("/cart/checkout")
    public Order checkoutCart(@RequestBody Order order) {
        return this.restaurantService.checkoutCart(order);
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<String> clearCart() {
        return this.restaurantService.clearCart();
    }
}
