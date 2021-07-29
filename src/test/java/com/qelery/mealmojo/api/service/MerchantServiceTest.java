package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.dto.RestaurantThinDtoOut;
import com.qelery.mealmojo.api.model.entity.MerchantProfile;
import com.qelery.mealmojo.api.model.entity.Restaurant;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.ObjectMapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @InjectMocks
    MerchantService merchantService;

    @Mock
    RestaurantRepository restaurantRepository;
    @Mock
    MenuItemRepository menuItemRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OperatingHoursRepository operatingHoursRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    Authentication authentication;
    @Spy
    ObjectMapperUtils mapperUtils;

    User user;
    MerchantProfile merchantProfile;
    Restaurant restaurant1;
    Restaurant restaurant2;

    @BeforeEach
    void setUp() {
        this.user = new User();
        this.merchantProfile = new MerchantProfile();
        merchantProfile.setId(1L);
        this.user.setMerchantProfile(merchantProfile);
        this.restaurant1 = new Restaurant();
        restaurant1.setName("Restaurant1");
        this.restaurant2 = new Restaurant();
        restaurant2.setName("Restaurant2");

        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(user));
        SecurityContextHolder.getContext().setAuthentication(authentication);
   }



    @Test
    @DisplayName("Should return all restaurants owned by the logged in merchant user")
    void getAllRestaurantsOwned() {
        merchantProfile.setRestaurantsOwned(List.of(restaurant1, restaurant2));
        List<Restaurant> restaurants = List.of(restaurant1, restaurant2);
        List<RestaurantThinDtoOut> expectedRestaurantsDto = mapperUtils.mapAll(restaurants, RestaurantThinDtoOut.class);
        when(restaurantRepository.findAllByMerchantProfileId(anyLong())).thenReturn(restaurants);

        List<RestaurantThinDtoOut> actualRestaurantsDto = merchantService.getAllRestaurantsOwned();

        assertEquals(expectedRestaurantsDto, actualRestaurantsDto);
    }

    @Test
    void getSingleRestaurantOwned() {
    }

    @Test
    void createRestaurant() {
    }

    @Test
    void updateRestaurantBasicInformation() {
    }

    @Test
    void updateRestaurantHours() {
    }

    @Test
    void updateRestaurantAddress() {
    }

    @Test
    void createMenuItem() {
    }

    @Test
    void updateMenuItem() {
    }

    @Test
    void getAllOrdersForOwnedRestaurant() {
    }

    @Test
    void getSingleOrderForOwnedRestaurant() {
    }

    @Test
    void markOrderComplete() {
    }
}
