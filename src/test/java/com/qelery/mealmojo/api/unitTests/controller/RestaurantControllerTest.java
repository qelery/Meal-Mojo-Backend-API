package com.qelery.mealmojo.api.unitTests.controller;

import com.qelery.mealmojo.api.controller.RestaurantController;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.RestaurantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTest {

    @InjectMocks
    private RestaurantController restaurantController;

    @Mock
    private RestaurantService restaurantService;

    @Test
    @DisplayName("Should return all restaurants from service")
    void getRestaurants() {
        List<RestaurantThinDtoOut> expectedDtoList = List.of(new RestaurantThinDtoOut());
        when(restaurantService.getAllRestaurants())
                .thenReturn(expectedDtoList);

        List<RestaurantThinDtoOut> actualDtoList = restaurantController.getAllRestaurants();

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return restaurants from service within specified distance")
    void getRestaurantsWithinDistance() {
        List<RestaurantThinDtoOut> expectedDtoList = List.of(new RestaurantThinDtoOut());
        when(restaurantService.getRestaurantsWithinDistance(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(expectedDtoList);

        List<RestaurantThinDtoOut> actualDtoList = restaurantController.getRestaurantsWithinDistance(1, 1, 1);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return a restaurant from service")
    void getRestaurant() {
        RestaurantDtoOut expectedDto = new RestaurantDtoOut();
        when(restaurantService.getRestaurant(anyLong()))
                .thenReturn(expectedDto);

        RestaurantDtoOut actualDto = restaurantController.getRestaurant(1L);

        assertEquals(expectedDto, actualDto);
    }


    @Test
    @DisplayName("Should return all restaurants from service owned by logged in merchant")
    void getAllRestaurantOwnedByLoggedInMerchant() {
        List<RestaurantThinDtoOut> expectedDtoList = List.of(new RestaurantThinDtoOut());
        when(restaurantService.getAllRestaurantsOwnedByLoggedInMerchant())
                .thenReturn(expectedDtoList);

        List<RestaurantThinDtoOut> actualDtoList = restaurantController.getAllRestaurantsOwnedByLoggedInMerchant();

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return the restaurant from service if owned by user")
    void getSingleRestaurantByOwnerByMerchant() {
        RestaurantDtoOut expectedDto = new RestaurantDtoOut();
        when(restaurantService.getSingleRestaurantOwnedByLoggedInMerchant(anyLong()))
                .thenReturn(expectedDto);

        RestaurantDtoOut actualDto = restaurantController.getSingleRestaurantOwnedByLoggedInMerchant(1L);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return the created restaurant from service")
    void createRestaurant() {
        RestaurantDtoOut expectedDto = new RestaurantDtoOut();
        when(restaurantService.createRestaurant(any(RestaurantDtoIn.class)))
                .thenReturn(expectedDto);

        RestaurantDtoOut actualDto = restaurantService.createRestaurant(new RestaurantDtoIn());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return restaurant with updated info from service")
    void updateRestaurantBasicInformation() {
        RestaurantThinDtoOut expectedDto = new RestaurantThinDtoOut();
        when(restaurantService.updateRestaurantBasicInformation(anyLong(), any(RestaurantDtoIn.class)))
                .thenReturn(expectedDto);

        RestaurantThinDtoOut actualDto = restaurantController.updateRestaurantBasicInformation(1L, new RestaurantDtoIn());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return restaurant with updated hours from service")
    void updateRestaurantHours() {
        RestaurantThinDtoOut expectedDto = new RestaurantThinDtoOut();
        when(restaurantService.updateRestaurantHours(anyLong(), anyList()))
                .thenReturn(expectedDto);

        RestaurantThinDtoOut actualDto = restaurantController.updateRestaurantHours(1L, new ArrayList<>());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return restaurant with updated address from service")
    void updateRestaurantAddress() {
        RestaurantThinDtoOut expectedDto = new RestaurantThinDtoOut();
        when(restaurantService.updateRestaurantAddress(anyLong(), any(AddressDto.class)))
                .thenReturn(expectedDto);

        RestaurantThinDtoOut actualDto = restaurantController.updateRestaurantAddress(1L, new AddressDto());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return all menu items for a given restaurant from service")
    void getAllMenuItemsByRestaurantId() {
        List<MenuItemDto> expectedDtoList = List.of(new MenuItemDto());
        when(restaurantService.getAllMenuItemsByRestaurant(anyLong()))
                .thenReturn(expectedDtoList);

        List<MenuItemDto> actualDtoList = restaurantController.getAllMenuItemsByRestaurant(1L);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return a menu item from service")
    void getMenuItemByRestaurant() {
        MenuItemDto expectedDto = new MenuItemDto();
        when(restaurantService.getMenuItemByRestaurant(anyLong(), anyLong()))
                .thenReturn(expectedDto);

        MenuItemDto actualDto = restaurantController.getMenuItemByRestaurant(1L, 1L);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return created menu item from service")
    void createMenuItem() {
        MenuItemDto expectedDto = new MenuItemDto();
        when(restaurantService.createMenuItem(anyLong(), any(MenuItemDto.class)))
                .thenReturn(expectedDto);

        MenuItemDto actualDto = restaurantController.createMenuItem(1L, new MenuItemDto());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return updated menu item from service")
    void updateMenuItem() {
        MenuItemDto expectedDto = new MenuItemDto();
        when(restaurantService.updateMenuItem(anyLong(), anyLong(), any(MenuItemDto.class)))
                .thenReturn(expectedDto);

        MenuItemDto actualDto = restaurantController.updateMenuItem(1L, 1L, new MenuItemDto());

        assertEquals(expectedDto, actualDto);
    }
}
