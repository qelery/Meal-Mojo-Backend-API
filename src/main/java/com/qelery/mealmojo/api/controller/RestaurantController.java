package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDtoOut getRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.getRestaurant(restaurantId);
    }

    @GetMapping("/restaurants")
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantThinDtoOut> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping(value="/restaurants/nearby", params={"latitude", "longitude", "maxDistanceMiles"})
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantThinDtoOut> getRestaurantsWithinDistance(@RequestParam double latitude,
                                                                   @RequestParam double longitude,
                                                                   @RequestParam(defaultValue="15") int maxDistanceMiles) {
        return restaurantService.getRestaurantsWithinDistance(latitude, longitude, maxDistanceMiles);
    }

    @GetMapping("/restaurants/by-logged-in-merchant")
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantThinDtoOut> getAllRestaurantsOwnedByLoggedInMerchant() {
        return restaurantService.getAllRestaurantsOwnedByLoggedInMerchant();
    }

    @GetMapping("/restaurants/{restaurantId}/by-logged-in-merchant")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDtoOut getSingleRestaurantOwnedByLoggedInMerchant(@PathVariable Long restaurantId) {
        return restaurantService.getSingleRestaurantOwnedByLoggedInMerchant(restaurantId);
    }

    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantDtoOut createRestaurant(@RequestBody RestaurantDtoIn restaurantDtoIn) {
        return restaurantService.createRestaurant(restaurantDtoIn);
    }

    @PatchMapping("/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantBasicInformation(@PathVariable Long restaurantId,
                                                                 @RequestBody RestaurantDtoIn restaurantInfoDto) {
        return restaurantService.updateRestaurantBasicInformation(restaurantId, restaurantInfoDto);
    }

    @PatchMapping("/restaurants/{restaurantId}/hours")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantHours(@PathVariable Long restaurantId,
                                                      @RequestBody List<OperatingHoursDto> hoursList) {
        return restaurantService.updateRestaurantHours(restaurantId, hoursList);
    }

    @PatchMapping("/restaurants/{restaurantId}/address")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantAddress(@PathVariable Long restaurantId,
                                                        @RequestBody AddressDto addressDto) {
        return restaurantService.updateRestaurantAddress(restaurantId, addressDto);
    }

    @GetMapping("/restaurants/{restaurantId}/menuitems/{menuitemId}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemDto getMenuItemByRestaurant(@PathVariable Long restaurantId,
                                               @PathVariable Long menuitemId) {
        return restaurantService.getMenuItemByRestaurant(restaurantId, menuitemId);
    }

    @GetMapping("/restaurants/{restaurantId}/menuitems")
    @ResponseStatus(HttpStatus.OK)
    public List<MenuItemDto> getAllMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.getAllMenuItemsByRestaurant(restaurantId);
    }

    @PostMapping("/restaurants/{restaurantId}/menuitems")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemDto createMenuItem(@PathVariable Long restaurantId,
                                      @RequestBody MenuItemDto menuItemDto) {
        return restaurantService.createMenuItem(restaurantId, menuItemDto);
    }

    @PutMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemDto updateMenuItem(@PathVariable Long restaurantId,
                                      @PathVariable Long menuItemId,
                                      @RequestBody MenuItemDto menuItemDto) {
        return restaurantService.updateMenuItem(restaurantId, menuItemId, menuItemDto);
    }
}
