package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.Restaurant;
import com.qelery.mealmojo.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService restaurantService) {
        this.orderService = restaurantService;
    }

    @GetMapping("/order/restaurants")
    @ResponseStatus(HttpStatus.OK)
    public List<Restaurant> getRestaurants() {
        return orderService.getRestaurants();
    }

    @GetMapping(value="/order/restaurants", params={"latitude", "longitude", "maxDistance"})
    public List<RestaurantThinDtoOut> getRestaurantsWithinDistance(@RequestParam double latitude,
                                                                   @RequestParam double longitude,
                                                                   @RequestParam(defaultValue="15") int maxDistance) {
        return orderService.getRestaurantsWithinDistance(latitude, longitude, maxDistance);
    }

    @GetMapping("/order/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDto getRestaurant(@PathVariable Long restaurantId) {
        return orderService.getRestaurant(restaurantId);
    }

    @GetMapping("/order/restaurants/{restaurantId}/menuitems")
    @ResponseStatus(HttpStatus.OK)
    public List<MenuItemDto> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return orderService.getMenuItemsByRestaurant(restaurantId);
    }

    @GetMapping("/order/restaurants/{restaurantId}/menuitems/{menuitemId}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemDto getMenuItemByRestaurant(@PathVariable Long restaurantId,
                                            @PathVariable Long menuitemId) {
        return orderService.getMenuItemByRestaurant(restaurantId, menuitemId);
    }

    @PostMapping("/order/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDtoOut submitOrder(@RequestBody OrderDtoIn orderDtoIn) {
        return this.orderService.submitOrder(orderDtoIn);
    }

    @GetMapping("/order/past")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDtoOut> getOrders(@RequestParam(required=false) Long restaurantId) {
        return this.orderService.getPlacedOrders(restaurantId);
    }
}
