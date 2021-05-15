package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.Address;
import com.qelery.mealmojo.api.model.MenuItem;
import com.qelery.mealmojo.api.model.OperatingHours;
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

//    @GetMapping("/restaurants")
//    public List<RestaurantProfile> getRestaurants(@RequestParam double latitude,
//                                                  @RequestParam double longitude,
//                                                  @RequestParam(defaultValue="15") int maxDistance) {
//        return restaurantService.getRestaurants(latitude, longitude, maxDistance);
//    }

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

    @PatchMapping("/restaurants/{restaurantId}/basicinfo")
    public RestaurantProfile updateRestaurantBasicInfo(@PathVariable Long restaurantId,
                                                       @RequestBody RestaurantProfile restaurantProfile) {
        return restaurantService.updateRestaurantBasicInfo(restaurantId, restaurantProfile);
    }

    @PatchMapping("/restaurants/{restaurantId}/hours")
    public RestaurantProfile updateRestaurantHours(@PathVariable Long restaurantId,
                                                   @RequestBody List<OperatingHours> hoursList) {
        return restaurantService.updateRestaurantHours(restaurantId, hoursList);
    }

    @PatchMapping("/restaurants/{restaurantId}/address")
    public RestaurantProfile updateRestaurantAddress(@PathVariable Long restaurantId,
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

    @PostMapping("/restaurants/{restaurantId}/menuitems/{menuitemId}")
    public MenuItem createMenuItem(@PathVariable Long restaurantId,
                                   @PathVariable Long menuitemId,
                                   @RequestBody MenuItem menuItem) {
        return restaurantService.createMenuItem(restaurantId, menuitemId, menuItem);
    }

    @PutMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}")
    public MenuItem updateMenuItem(@PathVariable Long restaurantId,
                                   @PathVariable Long menuItemId,
                                   @RequestBody MenuItem menuItem) {
        return restaurantService.updateMenuItem(restaurantId, menuItemId, menuItem);
    }

    @PatchMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}")
    public MenuItem changeMenuItemAvailability(@PathVariable Long restaurantId,
                                               @PathVariable Long menuItemId,
                                               @RequestParam Boolean available) {
        return restaurantService.changeMenuItemAvailability(restaurantId, menuItemId, available);
    }
}
