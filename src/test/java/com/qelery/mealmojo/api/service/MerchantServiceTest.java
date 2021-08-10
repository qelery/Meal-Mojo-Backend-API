package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.ProfileNotFoundException;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.State;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
    AddressRepository addressRepository;
    @Mock
    Authentication authentication;
    @Spy
    MapperUtils mapperUtils;

    User loggedInUser;
    MerchantProfile merchantProfile;
    Restaurant restaurant1;
    Restaurant restaurant2;

    @BeforeEach
    void setUp() {
        this.restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("Restaurant1");
        restaurant1.setDescription("Description for restaurant1");
        restaurant1.setDeliveryFee(3.00);
        this.restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Restaurant2");

        this.merchantProfile = new MerchantProfile();
        merchantProfile.setId(1L);
        this.loggedInUser = new User();
        loggedInUser.setMerchantProfile(merchantProfile);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
   }

    @Test
    @DisplayName("Should throw exception if user trying to retrieve information does not have a merchant profile")
    void shouldThrowProfileNotFoundException() {
        loggedInUser.setMerchantProfile(null);

        assertThrows(ProfileNotFoundException.class, () -> merchantService.getAllRestaurantsOwned());
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
    @DisplayName("Should return a restaurant by id that is owned by the logged in merchant user")
    void getSingleRestaurantOwned() {
        merchantProfile.setRestaurantsOwned(List.of(restaurant1));
        RestaurantDtoOut expectedRestaurantDto = mapperUtils.map(restaurant1, RestaurantDtoOut.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.of(restaurant1));

        RestaurantDtoOut actualRestaurantDto = merchantService.getSingleRestaurantOwned(merchantProfile.getId());

        assertEquals(expectedRestaurantDto, actualRestaurantDto);
    }

    @Test
    @DisplayName("Should save created restaurant to database")
    void createRestaurantAndSaveToDatabase() {
        RestaurantDtoIn restaurantDtoIn = new RestaurantDtoIn();
        restaurantDtoIn.setName("My Restaurant");
        restaurantDtoIn.setDescription("Description for my restaurant");
        restaurantDtoIn.setHeroImageUrl("my-restaurant-image.jpg");
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);

        merchantService.createRestaurant(restaurantDtoIn);

        verify(restaurantRepository).save(restaurantCaptor.capture());
        Restaurant savedRestaurant = restaurantCaptor.getValue();
        assertEquals(restaurantDtoIn.getName(), savedRestaurant.getName());
        assertEquals(restaurantDtoIn.getDescription(), savedRestaurant.getDescription());
        assertEquals(restaurantDtoIn.getHeroImageUrl(), savedRestaurant.getHeroImageUrl());
    }

    @Test
    @DisplayName("Should save created restaurant with logged in merchant as its owner")
    void createRestaurantAndSaveToMerchantProfile() {
        RestaurantDtoIn restaurantDtoIn = new RestaurantDtoIn();
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);

        merchantService.createRestaurant(restaurantDtoIn);

        verify(restaurantRepository).save(restaurantCaptor.capture());
        assertEquals(merchantProfile, restaurantCaptor.getValue().getMerchantProfile());
    }

    @Test
    @DisplayName("Should update restaurant info and save changes to database")
    void updateRestaurantBasicInformation() {
        RestaurantDtoIn updatedInfoDto = new RestaurantDtoIn();
        updatedInfoDto.setName("Updated Name");
        updatedInfoDto.setDescription("Updated Restaurant Description");
        updatedInfoDto.setDeliveryFee(5.50);
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));

        merchantService.updateRestaurantBasicInformation(restaurant1.getId(), updatedInfoDto);

        verify(restaurantRepository).save(restaurantCaptor.capture());
        Restaurant savedRestaurant = restaurantCaptor.getValue();
        assertEquals(updatedInfoDto.getName(), savedRestaurant.getName());
        assertEquals(updatedInfoDto.getDescription(), savedRestaurant.getDescription());
        assertEquals(updatedInfoDto.getDeliveryFee(), savedRestaurant.getDeliveryFee());
    }

    @Test
    @DisplayName("Should update restaurant with new hours and save to database")
    void updateRestaurantHoursAddNewHours() {
        OperatingHoursDto tuesdayHoursDto = new OperatingHoursDto();
        tuesdayHoursDto.setDayOfWeek(DayOfWeek.TUESDAY);
        tuesdayHoursDto.setOpenTime(LocalTime.of(8,0));
        tuesdayHoursDto.setCloseTime(LocalTime.of(20, 0));
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);


        merchantService.updateRestaurantHours(restaurant1.getId(), List.of(tuesdayHoursDto));


        verify(restaurantRepository).save(restaurantCaptor.capture());
        Optional<OperatingHours> optionalHours = restaurantCaptor.getValue()
                .getOperatingHoursList()
                .stream()
                .filter(hours -> hours.getDayOfWeek() == DayOfWeek.TUESDAY)
                .findFirst();
        assertTrue(optionalHours.isPresent());
        OperatingHours savedTuesdayHours = optionalHours.get();
        assertEquals(tuesdayHoursDto.getDayOfWeek(), savedTuesdayHours.getDayOfWeek());
        assertEquals(tuesdayHoursDto.getOpenTime(), savedTuesdayHours.getOpenTime());
        assertEquals(tuesdayHoursDto.getCloseTime(), savedTuesdayHours.getCloseTime());
    }

    @Test
    @DisplayName("Should overwrite existing hours if updating restaurant hours and hours for that day already exists")
    void updateRestaurantHoursOverwriteExisting() {
        OperatingHours currentTuesdayHours = new OperatingHours();
        currentTuesdayHours.setDayOfWeek(DayOfWeek.TUESDAY);
        currentTuesdayHours.setOpenTime(LocalTime.of(5, 0));
        currentTuesdayHours.setCloseTime(LocalTime.of(23, 0));
        restaurant1.setOperatingHoursList(List.of(currentTuesdayHours));

        OperatingHoursDto updatedTuesdayHoursDto = new OperatingHoursDto();
        updatedTuesdayHoursDto.setDayOfWeek(DayOfWeek.TUESDAY);
        updatedTuesdayHoursDto.setOpenTime(LocalTime.of(8,0));
        updatedTuesdayHoursDto.setCloseTime(LocalTime.of(20, 0));

        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);


        merchantService.updateRestaurantHours(restaurant1.getId(), List.of(updatedTuesdayHoursDto));


        verify(restaurantRepository).save(restaurantCaptor.capture());
        Optional<OperatingHours> optionalHours = restaurantCaptor.getValue()
                .getOperatingHoursList()
                .stream()
                .filter(hours -> hours.getDayOfWeek() == DayOfWeek.TUESDAY)
                .findFirst();
        assertTrue(optionalHours.isPresent());
        OperatingHours savedTuesdayHours = optionalHours.get();
        assertEquals(updatedTuesdayHoursDto.getDayOfWeek(), savedTuesdayHours.getDayOfWeek());
        assertEquals(updatedTuesdayHoursDto.getOpenTime(), savedTuesdayHours.getOpenTime());
        assertEquals(updatedTuesdayHoursDto.getCloseTime(), savedTuesdayHours.getCloseTime());
    }

    @Test
    @DisplayName("Should update restaurant address and save changes to database")
    void updateRestaurantAddress() {
        Address currentAddress = new Address();
        currentAddress.setStreet1("1400 S Lake Shore Dr");
        currentAddress.setStreet2("2nd Floor");
        currentAddress.setStreet3("Office 3B");
        currentAddress.setCity("Chicago");
        currentAddress.setState(State.IL);
        currentAddress.setZipcode("60605");
        currentAddress.setCountry(Country.US);
        restaurant1.setAddress(currentAddress);

        AddressDto updatedAddressDto = new AddressDto();
        updatedAddressDto.setStreet1("5700 S Lake Shore Dr");
        updatedAddressDto.setZipcode("60637");

        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));


        RestaurantThinDtoOut restaurantDto = merchantService.updateRestaurantAddress(restaurant1.getId(), updatedAddressDto);
        Address savedAddress = restaurantDto.getAddress();


        verify(addressRepository).save(savedAddress);
        assertEquals(updatedAddressDto.getStreet1(), savedAddress.getStreet1());
        assertEquals(updatedAddressDto.getZipcode(), savedAddress.getZipcode());
        assertNotNull(savedAddress.getCity());
        assertNotNull(savedAddress.getState());
        // Street2 and Street3 are the only fields that should
        // be set to null if not supplied on update
        assertNull(savedAddress.getStreet2());
        assertNull(savedAddress.getStreet3());
    }

    @Test
    @DisplayName("Should add a new menu item to a restaurant and save to database")
    void createMenuItem() {
        MenuItemDto menuItemDto = new MenuItemDto();
        menuItemDto.setName("Pizza");
        menuItemDto.setPrice(18.0);
        ArgumentCaptor<MenuItem> menuItemCaptor = ArgumentCaptor.forClass(MenuItem.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));

        merchantService.createMenuItem(restaurant1.getId(), menuItemDto);

        verify(menuItemRepository).save(menuItemCaptor.capture());
        MenuItem savedMenuItem = menuItemCaptor.getValue();
        assertEquals(menuItemDto.getName(), savedMenuItem.getName());
        assertEquals(menuItemDto.getPrice(), savedMenuItem.getPrice());
    }

    @Test
    @DisplayName("Should update a restaurant's menu item and save changes to database")
    void updateMenuItem() {
        MenuItem currentMenuItem = new MenuItem();
        currentMenuItem.setId(5L);
        currentMenuItem.setName("Pepperoni Pizza");
        currentMenuItem.setPrice(18.0);
        restaurant1.setMenuItems(List.of(currentMenuItem));

        MenuItemDto updatedMenuItemDto = new MenuItemDto();
        updatedMenuItemDto.setName("Sausage Pizza");
        updatedMenuItemDto.setPrice(20.0);

        ArgumentCaptor<MenuItem> menuItemCaptor = ArgumentCaptor.forClass(MenuItem.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));


        merchantService.updateMenuItem(restaurant1.getId(), currentMenuItem.getId(), updatedMenuItemDto);


        verify(menuItemRepository).save(menuItemCaptor.capture());
        MenuItem savedMenuItem = menuItemCaptor.getValue();
        assertEquals(updatedMenuItemDto.getName(), savedMenuItem.getName());
        assertEquals(updatedMenuItemDto.getPrice(), savedMenuItem.getPrice());
    }

    @Test
    @DisplayName("Should get all orders for a restaurant if it is owned by the logged in merchant")
    void getAllOrdersForOwnedRestaurant() {
        Order order1 = new Order();
        order1.setTip(1.0);
        Order order2 = new Order();
        order2.setTip(2.0);
        restaurant1.setOrders(List.of(order1, order2));
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));
        when(orderRepository.findAllByRestaurantId(anyLong()))
                .thenReturn(restaurant1.getOrders());

        List<OrderDtoOut> actualOrdersDto = merchantService.getAllOrdersForOwnedRestaurant(restaurant1.getId());

        assertTrue(actualOrdersDto.stream().anyMatch(order -> order.getTip() == 1.0));
        assertTrue(actualOrdersDto.stream().anyMatch(order -> order.getTip() == 2.0));
    }

    @Test
    @DisplayName("Should get an order if its restaurant is owned by the logged in merchant")
    void getSingleOrderForOwnedRestaurant() {
        Order order = new Order();
        order.setId(5L);
        order.setTip(1.0);
        restaurant1.setOrders(List.of(order));
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));
        when(orderRepository.findByIdAndRestaurantId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1.getOrders().get(0)));

        OrderDtoOut actualOrderDto = merchantService.getSingleOrderForOwnedRestaurant(restaurant1.getId(), order.getId()) ;

        assertEquals(order.getTip(), actualOrderDto.getTip());
    }

    @Test
    @DisplayName("Should mark an order complete if its restaurant is owned by the logged in merchant")
    void markOrderComplete() {
        Order order = new Order();
        order.setId(5L);
        order.setIsCompleted(false);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));
        when(orderRepository.findByIdAndRestaurantId(anyLong(), anyLong()))
                .thenReturn(Optional.of(order));

        merchantService.markOrderComplete(restaurant1.getId(), order.getId());

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertTrue(savedOrder.getIsCompleted());
    }
}
