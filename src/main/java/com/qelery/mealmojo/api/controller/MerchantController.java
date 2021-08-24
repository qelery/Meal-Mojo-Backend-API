package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    @Autowired
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/restaurants")
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantThinDtoOut> getAllRestaurantsOwned() {
        return merchantService.getAllRestaurantsOwned();
    }

    @GetMapping("/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDtoOut getSingleRestaurantOwned(@PathVariable Long restaurantId) {
        return merchantService.getSingleRestaurantOwned(restaurantId);
    }

    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantDtoOut createRestaurant(@RequestBody RestaurantDtoIn restaurantDtoIn) {
        return merchantService.createRestaurant(restaurantDtoIn);
    }

    @PatchMapping("/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantBasicInformation(@PathVariable Long restaurantId,
                                                                   @RequestBody RestaurantDtoIn restaurantInfoDto) {
        return merchantService.updateRestaurantBasicInformation(restaurantId, restaurantInfoDto);
    }

    @PatchMapping("/restaurants/{restaurantId}/hours")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantHours(@PathVariable Long restaurantId,
                                                      @RequestBody List<OperatingHoursDto> hoursList) {
        return merchantService.updateRestaurantHours(restaurantId, hoursList);
    }

    @PatchMapping("/restaurants/{restaurantId}/address")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantThinDtoOut updateRestaurantAddress(@PathVariable Long restaurantId,
                                                        @RequestBody AddressDto addressDto) {
        return merchantService.updateRestaurantAddress(restaurantId, addressDto);
    }

    @PostMapping("/restaurants/{restaurantId}/menuitems")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemDto createMenuItem(@PathVariable Long restaurantId,
                                      @RequestBody MenuItemDto menuItemDto) {
        return merchantService.createMenuItem(restaurantId, menuItemDto);
    }

    @PutMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemDto updateMenuItem(@PathVariable Long restaurantId,
                                      @PathVariable Long menuItemId,
                                      @RequestBody MenuItemDto menuItemDto) {
        return merchantService.updateMenuItem(restaurantId, menuItemId, menuItemDto);
    }

    @GetMapping("/restaurants/{restaurantId}/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDtoOut> getAllOrdersForOwnedRestaurant(@PathVariable Long restaurantId) {
        return merchantService.getAllOrdersForOwnedRestaurant(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDtoOut getSingleOrderForOwnedRestaurant(@PathVariable Long restaurantId,
                                               @PathVariable Long orderId) {
        return merchantService.getSingleOrderForOwnedRestaurant(restaurantId, orderId);
    }

    @PatchMapping("/restaurants/{restaurantId}/orders/{orderId}/complete")
    @ResponseStatus(HttpStatus.OK)
    public OrderDtoOut markOrderComplete(@PathVariable Long restaurantId,
                                         @PathVariable Long orderId) {
        return merchantService.markOrderComplete(restaurantId, orderId);
    }
}
