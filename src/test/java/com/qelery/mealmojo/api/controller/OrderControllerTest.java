package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Test
    @DisplayName("Should return all restaurants from service")
    void getRestaurants() {
        List<RestaurantThinDtoOut> expectedDtoList = List.of(new RestaurantThinDtoOut());
        when(orderService.getAllRestaurants())
                .thenReturn(expectedDtoList);

        List<RestaurantThinDtoOut> actualDtoList = orderController.getAllRestaurants();

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return restaurants from service within specified distance")
    void getRestaurantsWithinDistance() {
        List<RestaurantThinDtoOut> expectedDtoList = List.of(new RestaurantThinDtoOut());
        when(orderService.getRestaurantsWithinDistance(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(expectedDtoList);

        List<RestaurantThinDtoOut> actualDtoList = orderController.getRestaurantsWithinDistance(1, 1, 1);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return a restaurant from service")
    void getRestaurant() {
        RestaurantDtoOut expectedDto = new RestaurantDtoOut();
        when(orderService.getRestaurant(anyLong()))
                .thenReturn(expectedDto);

        RestaurantDtoOut actualDto = orderController.getRestaurant(1L);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return all menu items for a given restaurant from service")
    void getAllMenuItemsByRestaurantId() {
        List<MenuItemDto> expectedDtoList = List.of(new MenuItemDto());
        when(orderService.getAllMenuItemsByRestaurant(anyLong()))
                .thenReturn(expectedDtoList);

        List<MenuItemDto> actualDtoList = orderController.getAllMenuItemsByRestaurant(1L);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return a menu item from service")
    void getMenuItemByRestaurant() {
        MenuItemDto expectedDto = new MenuItemDto();
        when(orderService.getMenuItemByRestaurant(anyLong(), anyLong()))
                .thenReturn(expectedDto);

        MenuItemDto actualDto = orderController.getMenuItemByRestaurant(1L, 1L);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return the submitted order from service")
    void submitOrder() {
        OrderDtoOut expectedDto = new OrderDtoOut();
        when(orderService.submitOrder(any(OrderDtoIn.class)))
                .thenReturn(expectedDto);

        OrderDtoOut actualDto = orderController.submitOrder(new OrderDtoIn());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return orders from service")
    void getOrder() {
        List<OrderDtoOut> expectedDtoList = List.of(new OrderDtoOut());
        when(orderService.getPlacedOrders(anyLong()))
                .thenReturn(expectedDtoList);

        List<OrderDtoOut> actualDtoList = orderController.getOrders(1L);

        assertEquals(expectedDtoList, actualDtoList);
    }
}

