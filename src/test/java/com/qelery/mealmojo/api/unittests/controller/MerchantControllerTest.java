package com.qelery.mealmojo.api.unittests.controller;

import com.qelery.mealmojo.api.controller.MerchantController;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.service.MerchantService;
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
class MerchantControllerTest {

    @InjectMocks
    private MerchantController merchantController;

    @Mock
    private MerchantService mockMerchantService;

    @Test
    @DisplayName("Should return all restaurants from service owned by logged in merchant")
    void getAllRestaurantOwned() {
        List<RestaurantThinDtoOut> expectedDtoList = List.of(new RestaurantThinDtoOut());
        when(mockMerchantService.getAllRestaurantsOwned())
                .thenReturn(expectedDtoList);

        List<RestaurantThinDtoOut> actualDtoList = merchantController.getAllRestaurantsOwned();

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return the restaurant from service if owned by user")
    void getSingleRestaurantByOwner() {
        RestaurantDtoOut expectedDto = new RestaurantDtoOut();
        when(mockMerchantService.getSingleRestaurantOwned(anyLong()))
                .thenReturn(expectedDto);

        RestaurantDtoOut actualDto = merchantController.getSingleRestaurantOwned(1L);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return the created restaurant from service")
    void createRestaurant() {
        RestaurantDtoOut expectedDto = new RestaurantDtoOut();
        when(mockMerchantService.createRestaurant(any(RestaurantDtoIn.class)))
                .thenReturn(expectedDto);

        RestaurantDtoOut actualDto = merchantController.createRestaurant(new RestaurantDtoIn());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return restaurant with updated info from service")
    void updateRestaurantBasicInformation() {
        RestaurantThinDtoOut expectedDto = new RestaurantThinDtoOut();
        when(mockMerchantService.updateRestaurantBasicInformation(anyLong(), any(RestaurantDtoIn.class)))
                .thenReturn(expectedDto);

        RestaurantThinDtoOut actualDto = merchantController.updateRestaurantBasicInformation(1L, new RestaurantDtoIn());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return restaurant with updated hours from service")
    void updateRestaurantHours() {
        RestaurantThinDtoOut expectedDto = new RestaurantThinDtoOut();
        when(mockMerchantService.updateRestaurantHours(anyLong(), anyList()))
                .thenReturn(expectedDto);

        RestaurantThinDtoOut actualDto = merchantController.updateRestaurantHours(1L, new ArrayList<>());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return restaurant with updated address from service")
    void updateRestaurantAddress() {
        RestaurantThinDtoOut expectedDto = new RestaurantThinDtoOut();
        when(mockMerchantService.updateRestaurantAddress(anyLong(), any(AddressDto.class)))
                .thenReturn(expectedDto);

        RestaurantThinDtoOut actualDto = merchantController.updateRestaurantAddress(1L, new AddressDto());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return created menu item from service")
    void createMenuItem() {
        MenuItemDto expectedDto = new MenuItemDto();
        when(mockMerchantService.createMenuItem(anyLong(), any(MenuItemDto.class)))
                .thenReturn(expectedDto);

        MenuItemDto actualDto = merchantController.createMenuItem(1L, new MenuItemDto());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return updated menu item from service")
    void updateMenuItem() {
        MenuItemDto expectedDto = new MenuItemDto();
        when(mockMerchantService.updateMenuItem(anyLong(), anyLong(), any(MenuItemDto.class)))
                .thenReturn(expectedDto);

        MenuItemDto actualDto = merchantController.updateMenuItem(1L, 1L, new MenuItemDto());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return orders from given restaurant for logged in merchant")
    void getAllOrdersForOwnedRestaurant() {
        List<OrderDtoOut> expectedDtoList = List.of(new OrderDtoOut());
        when(mockMerchantService.getAllOrdersForOwnedRestaurant(anyLong()))
                .thenReturn(expectedDtoList);

        List<OrderDtoOut> actualDtoList = merchantController.getAllOrdersForOwnedRestaurant(1L);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return the order for given restaurant from service")
    void getSingleOrderForOwnedRestaurant() {
        OrderDtoOut expectedDto = new OrderDtoOut();
        when(mockMerchantService.getSingleOrderForOwnedRestaurant(anyLong(), anyLong()))
                .thenReturn(expectedDto);

        OrderDtoOut actualDto = merchantController.getSingleOrderForOwnedRestaurant(1L, 1L);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return order that has been marked as complete from service")
    void markOrderComplete() {
        OrderDtoOut expectedDto = new OrderDtoOut();
        when(mockMerchantService.markOrderComplete(anyLong(), anyLong()))
                .thenReturn(expectedDto);

        OrderDtoOut actualDto = merchantController.markOrderComplete(1L, 1L);

        assertEquals(expectedDto, actualDto);
    }
}
