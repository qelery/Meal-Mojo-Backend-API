package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.Address;
import com.qelery.mealmojo.api.model.MenuItem;
import com.qelery.mealmojo.api.model.OperatingHours;
import com.qelery.mealmojo.api.model.Restaurant;
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
}
