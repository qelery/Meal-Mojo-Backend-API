package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MerchantController {

    private MerchantService merchantService;

    @Autowired
    public void setMerchantService(MerchantService merchantService) {
        this.merchantService = merchantService;
    }


    @GetMapping("/merchant/restaurants/all")
    public List<Restaurant> getAllRestaurantOwned() {
        return merchantService.getAllRestaurantsByOwner();
    }

    @GetMapping("/merchant/restaurants/{restaurantId}")
    public Restaurant getRestaurantOwned(@PathVariable Long restaurantId) {
        return merchantService.getSingleRestaurantByOwner(restaurantId);
    }

    @PostMapping("/merchant/restaurants")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant newRestaurant = merchantService.createRestaurant(restaurant);
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("/merchant/restaurants/{restaurantId}")
    public Restaurant updateRestaurant(@PathVariable Long restaurantId,
                                                   @RequestBody Restaurant restaurant) {
        return merchantService.updateRestaurant(restaurantId, restaurant);
    }

    @PatchMapping("/merchant/restaurants/{restaurantId}/hours")
    public ResponseEntity<String> updateRestaurantHours(@PathVariable Long restaurantId,
                                                        @RequestBody List<OperatingHours> hoursList) {
        return merchantService.updateRestaurantHours(restaurantId, hoursList);
    }

    @PatchMapping("/merchant/restaurants/{restaurantId}/address")
    public ResponseEntity<String> updateRestaurantAddress(@PathVariable Long restaurantId,
                                                          @RequestBody Address address) {
        return merchantService.updateRestaurantAddress(restaurantId, address);
    }

    @PostMapping("/merchant/restaurants/{restaurantId}/menuitems")
    public ResponseEntity<MenuItem> createMenuItem(@PathVariable Long restaurantId,
                                   @RequestBody MenuItem menuItem) {
        return merchantService.createMenuItem(restaurantId, menuItem);
    }

    @PutMapping("/merchant/restaurants/{restaurantId}/menuitems/{menuItemId}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long restaurantId,
                                                 @PathVariable Long menuItemId,
                                                 @RequestBody MenuItem menuItem) {
        return merchantService.updateMenuItem(restaurantId, menuItemId, menuItem);
    }


    @GetMapping("/merchant/restaurants/{restaurantId}/orders")
    public List<Order> getOwnedRestaurantOrders(@PathVariable Long restaurantId) {
        return merchantService.getAllOrdersForOwnedRestaurant(restaurantId);
    }

    @GetMapping("/merchant/restaurants/{restaurantId}/orders/{orderId}")
    public Order getOwnedRestaurantOrder(@PathVariable Long restaurantId,
                                                @PathVariable Long orderId) {
        return merchantService.getSingleOrderForOwnedRestaurant(restaurantId, orderId);
    }

    @PatchMapping("/merchant/restaurants/{restaurantId}/orders/{orderId}/complete")
    public ResponseEntity<Order> markOrderComplete(@PathVariable Long restaurantId,
                                                    @PathVariable Long orderId) {
        return merchantService.markOrderComplete(restaurantId, orderId);
    }
}
