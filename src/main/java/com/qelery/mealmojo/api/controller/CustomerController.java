package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final OrderService orderService;

    @Autowired
    public CustomerController(OrderService restaurantService) {
        this.orderService = restaurantService;
    }

    @GetMapping("/restaurants")
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantThinDtoOut> getAllRestaurants() {
        return orderService.getAllRestaurants();
    }

    @GetMapping(value="/restaurants/nearby", params={"latitude", "longitude", "maxDistanceMiles"})
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantThinDtoOut> getRestaurantsWithinDistance(@RequestParam double latitude,
                                                                   @RequestParam double longitude,
                                                                   @RequestParam(defaultValue="15") int maxDistanceMiles) {
        return orderService.getRestaurantsWithinDistance(latitude, longitude, maxDistanceMiles);
    }

    @GetMapping("/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDtoOut getRestaurant(@PathVariable Long restaurantId) {
        return orderService.getRestaurant(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/menuitems")
    @ResponseStatus(HttpStatus.OK)
    public List<MenuItemDto> getAllMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return orderService.getAllMenuItemsByRestaurant(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/menuitems/{menuitemId}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemDto getMenuItemByRestaurant(@PathVariable Long restaurantId,
                                            @PathVariable Long menuitemId) {
        return orderService.getMenuItemByRestaurant(restaurantId, menuitemId);
    }

    @GetMapping("/order/past")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDtoOut> getOrders(@RequestParam(required=false) Long restaurantId) {
        return this.orderService.getPlacedOrders(restaurantId);
    }

    @PostMapping("/order/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDtoOut submitOrder(@RequestBody OrderDtoIn orderDtoIn) {
        return this.orderService.submitOrder(orderDtoIn);
    }
}
