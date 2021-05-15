package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.MenuItem;
import com.qelery.mealmojo.api.model.RestaurantProfile;
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
    public List<RestaurantProfile> getRestaurants() {
        return restaurantService.getRestaurants();
    }

    @GetMapping("/restaurants/{restaurantId}")
    public RestaurantProfile getRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.getRestaurant(restaurantId);
    }

    @PostMapping("/restaurants")
    public RestaurantProfile createRestaurant(@RequestBody RestaurantProfile restaurantProfile) {
        return restaurantService.createRestaurant(restaurantProfile);
    }

    @PutMapping("/restaurants")
    public RestaurantProfile updateRestaurant(@RequestBody RestaurantProfile restaurantProfile) {
        return restaurantService.updateRestaurant(restaurantProfile);
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.deleteRestaurant(restaurantId);
    }



    // Menu Item endpoints

    @GetMapping("/restaurants/{restaurantId}/menuitems")
    public List<MenuItem> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.getMenuItemsByRestaurant(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/menuitems/{menuitemsId}")
    public MenuItem getMenuItemByRestaurant(@PathVariable Long restaurantId,
                                            @PathVariable Long menuitemsId) {
        return restaurantService.getMenuItemByRestuarant(restaurantId, menuitemsId);
    }

    @PostMapping("/restaurants/{restaurantId}/menuitems/{menuitemsId}")
    public MenuItem createMenuItem(@PathVariable Long restaurantId,
                                   @PathVariable Long menuitemsId) {
        return restaurantService.createMenuItem(restaurantId, menuitemsId);
    }

    @PutMapping("/restaurants/{restaurantId}/menuitems/{menuitemsId}")
    public MenuItem updateMenuItem(@PathVariable Long restaurantId,
                                   @PathVariable Long menuitemsId) {
        return restaurantService.updateMenuItem(restaurantId, menuitemsId);
    }

    @PatchMapping("/restaurants/{restaurantId}/menuitems/{menuitemsId}")
    public MenuItem changeMenuItemAvailability(@PathVariable Long restaurantId,
                                            @PathVariable Long menuitemsId) {
        return restaurantService.changeMenuItemAvailability(restaurantId, menuitemsId);
    }
}
