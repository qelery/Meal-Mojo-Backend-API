package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.Address;
import com.qelery.mealmojo.api.model.MenuItem;
import com.qelery.mealmojo.api.model.OperatingHours;
import com.qelery.mealmojo.api.model.Restaurant;
import com.qelery.mealmojo.api.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return merchantService.getAllRestaurantsOwned();
    }

    @GetMapping("/merchant/restaurants/{restaurantId}")
    public Restaurant getRestaurantOwned(@PathVariable Long restaurantId) {
        return merchantService.getRestaurant(restaurantId);
    }

    @PostMapping("/merchant/restaurants")
    public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
        return merchantService.createRestaurant(restaurant);
    }

    @PutMapping("/merchant/restaurants/{restaurantId}")
    public ResponseEntity<String> updateRestaurant(@PathVariable Long restaurantId,
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
    public MenuItem createMenuItem(@PathVariable Long restaurantId,
                                   @RequestBody MenuItem menuItem) {
        return merchantService.createMenuItem(restaurantId, menuItem);
    }

    @PutMapping("/merchant/restaurants/{restaurantId}/menuitems/{menuItemId}")
    public ResponseEntity<String> updateMenuItem(@PathVariable Long restaurantId,
                                                 @PathVariable Long menuItemId,
                                                 @RequestBody MenuItem menuItem) {
        return merchantService.updateMenuItem(restaurantId, menuItemId, menuItem);
    }
}
