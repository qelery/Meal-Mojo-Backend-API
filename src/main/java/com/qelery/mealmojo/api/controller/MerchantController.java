package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/merchant/restaurants")
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantThinDtoOut> getAllRestaurantOwned() {
        return merchantService.getAllRestaurantsByOwner();
    }

    @GetMapping("/merchant/restaurants/{restaurantId}")
    public RestaurantDto getSingleRestaurantByOwner(@PathVariable Long restaurantId) {
        return merchantService.getSingleRestaurantByOwner(restaurantId);
    }

    @PostMapping("/merchant/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantDto createRestaurant(@RequestBody RestaurantDto restaurantDto) {
        return merchantService.createRestaurant(restaurantDto);
    }

    @PutMapping("/merchant/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantBasicInformation(@PathVariable Long restaurantId,
                                                                   @RequestBody RestaurantThinDtoIn restaurantInfoDto) {
        return merchantService.updateRestaurantBasicInformation(restaurantId, restaurantInfoDto);
    }

    @PatchMapping("/merchant/restaurants/{restaurantId}/hours")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantHours(@PathVariable Long restaurantId,
                                                      @RequestBody List<OperatingHoursDto> hoursList) {
        return merchantService.updateRestaurantHours(restaurantId, hoursList);
    }

    @PatchMapping("/merchant/restaurants/{restaurantId}/address")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantAddress(@PathVariable Long restaurantId,
                                                        @RequestBody AddressDto addressDto) {
        return merchantService.updateRestaurantAddress(restaurantId, addressDto);
    }

    @PostMapping("/merchant/restaurants/{restaurantId}/menuitems")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemDto createMenuItem(@PathVariable Long restaurantId,
                                      @RequestBody MenuItemDto menuItemDto) {
        return merchantService.createMenuItem(restaurantId, menuItemDto);
    }

    @PutMapping("/merchant/restaurants/{restaurantId}/menuitems/{menuItemId}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemDto updateMenuItem(@PathVariable Long restaurantId,
                                      @PathVariable Long menuItemId,
                                      @RequestBody MenuItemDto menuItemDto) {
        return merchantService.updateMenuItem(restaurantId, menuItemId, menuItemDto);
    }


    @GetMapping("/merchant/restaurants/{restaurantId}/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDtoOut> getOwnedRestaurantOrders(@PathVariable Long restaurantId) {
        return merchantService.getAllOrdersForOwnedRestaurant(restaurantId);
    }

    @GetMapping("/merchant/restaurants/{restaurantId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDtoOut getOwnedRestaurantOrder(@PathVariable Long restaurantId,
                                               @PathVariable Long orderId) {
        return merchantService.getSingleOrderForOwnedRestaurant(restaurantId, orderId);
    }

    @PatchMapping("/merchant/restaurants/{restaurantId}/orders/{orderId}/complete")
    @ResponseStatus(HttpStatus.OK)
    public OrderDtoOut markOrderComplete(@PathVariable Long restaurantId,
                                         @PathVariable Long orderId) {
        return merchantService.markOrderComplete(restaurantId, orderId);
    }
}
