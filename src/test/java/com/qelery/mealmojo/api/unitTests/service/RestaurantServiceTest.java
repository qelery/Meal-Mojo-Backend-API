package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.State;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.service.RestaurantService;
import com.qelery.mealmojo.api.service.utility.DistanceUtils;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @InjectMocks
    RestaurantService restaurantService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    RestaurantRepository restaurantRepository;
    @Mock
    OperatingHoursRepository operatingHoursRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    MenuItemRepository menuItemRepository;
    @Mock
    DistanceUtils distanceUtils;
    @Spy
    MapperUtils mapperUtils;

    Restaurant restaurant1;
    Restaurant restaurant2;
    MenuItem menuItem1;
    MenuItem menuItem2;

    @BeforeEach
    void setup() {
        this.restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("Restaurant1");
        restaurant1.setDescription("Restaurant1 description.");
        restaurant1.setDeliveryFee(3.00);
        this.restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Restaurant2");
        restaurant2.setDescription("Restaurant2 description.");
        restaurant2.setIsActive(false);

        this.menuItem1 = new MenuItem();
        menuItem1.setId(1L);
        menuItem1.setName("Salad 1");
        menuItem1.setPrice(7.99);
        this.menuItem2 = new MenuItem();
        menuItem2.setId(2L);
        menuItem2.setName("Salad 2");
        menuItem2.setPrice(8.60);

        restaurant1.setMenuItems(List.of(menuItem1, menuItem2));
    }

    @Test
    @DisplayName("Should get all active restaurants")
    void getAllRestaurants() {
        Restaurant activeRestaurant = restaurant1;
        when(restaurantRepository.findAllByIsActive(true)).thenReturn(List.of(activeRestaurant));

        List<RestaurantThinDtoOut> actualRestaurantDtos = restaurantService.getAllRestaurants();

        assertEquals(1, actualRestaurantDtos.size());
        assertEquals(activeRestaurant.getName(), actualRestaurantDtos.get(0).getName());
    }

    @Test
    @DisplayName("Should get all active restaurants within specified distance")
    void getRestaurantsWithinDistance() {
        when(distanceUtils.filterWithinDistance(anyList(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of(restaurant1));

        List<RestaurantThinDtoOut> actualRestaurantDtoOut = restaurantService.getRestaurantsWithinDistance(41.9, -87.6, 10);

        assertEquals(1, actualRestaurantDtoOut.size());
        assertEquals(restaurant1.getName(), actualRestaurantDtoOut.get(0).getName());
    }

    @Test
    @DisplayName("Should get a restaurant by its id")
    void getRestaurant() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));

        RestaurantDtoOut actualRestaurantDto = restaurantService.getRestaurant(restaurant1.getId());

        assertEquals(restaurant1.getName(), actualRestaurantDto.getName());
    }

    @Test
    @DisplayName("Should throw exception attempting to get restaurant by id that doesn't exist")
    void getRestaurantByNonExistentId() {
        Long nonExistentId = 90210L;
        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.getRestaurant(nonExistentId));
    }

    @Test
    @DisplayName("Should return restaurant by id owned by the logged in merchant user")
    void getSingleRestaurantOwnedByLoggedInMerchant() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
        RestaurantDtoOut expectedRestaurantDto = mapperUtils.map(restaurant1, RestaurantDtoOut.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.of(restaurant1));

        RestaurantDtoOut actualRestaurantDto = restaurantService.getSingleRestaurantOwnedByLoggedInMerchant(restaurant2.getId());

        assertEquals(expectedRestaurantDto, actualRestaurantDto);
    }

    @Test
    @DisplayName("Should throw exception when logged in merchant attempting to get restaurant by id that isn't owned by them")
    void getSingleRestaurantUnOwnedByLoggedInMerchant_throwError() {
        Long unownedRestaurantId = restaurant2.getId();
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1));
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());


        assertThrows(RestaurantNotFoundException.class, () ->
                restaurantService.getSingleRestaurantOwnedByLoggedInMerchant(unownedRestaurantId));
    }

    @Test
    @DisplayName("Should return all restaurants owned by the logged in merchant user")
    void getAllRestaurantsOwnedByLoggedInMerchant() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
        List<Restaurant> restaurants = List.of(restaurant1, restaurant2);
        List<RestaurantThinDtoOut> expectedRestaurantsDto = mapperUtils.mapAll(restaurants, RestaurantThinDtoOut.class);
        when(restaurantRepository.findAllByMerchantProfileId(anyLong())).thenReturn(restaurants);

        List<RestaurantThinDtoOut> actualRestaurantsDto = restaurantService.getAllRestaurantsOwnedByLoggedInMerchant();

        assertEquals(expectedRestaurantsDto, actualRestaurantsDto);
    }

    @Test
    @DisplayName("Should save created restaurant to database")
    void createRestaurantAndSaveToDatabase() {
        RestaurantDtoIn restaurantDtoIn = new RestaurantDtoIn();
        restaurantDtoIn.setName("My Restaurant");
        restaurantDtoIn.setDescription("Description for my restaurant");
        restaurantDtoIn.setHeroImageUrl("my-restaurant-image.jpg");
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);

        restaurantService.createRestaurant(restaurantDtoIn);

        verify(restaurantRepository).save(restaurantCaptor.capture());
        Restaurant savedRestaurant = restaurantCaptor.getValue();
        assertEquals(restaurantDtoIn.getName(), savedRestaurant.getName());
        assertEquals(restaurantDtoIn.getDescription(), savedRestaurant.getDescription());
        assertEquals(restaurantDtoIn.getHeroImageUrl(), savedRestaurant.getHeroImageUrl());
    }

    @Test
    @DisplayName("Should save created restaurant with logged in merchant as its owner")
    void createRestaurantAndSaveToMerchantProfile() {
        User loggedInUser = addMerchantToSecurityContext();
        MerchantProfile merchantProfile = loggedInUser.getMerchantProfile();
        RestaurantDtoIn restaurantDtoIn = new RestaurantDtoIn();
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);

        restaurantService.createRestaurant(restaurantDtoIn);

        verify(restaurantRepository).save(restaurantCaptor.capture());
        assertEquals(merchantProfile, restaurantCaptor.getValue().getMerchantProfile());
    }

    @Test
    @DisplayName("Should update restaurant info and save changes to database")
    void updateRestaurantBasicInformation() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
        RestaurantDtoIn updatedInfoDto = new RestaurantDtoIn();
        updatedInfoDto.setName("Updated Name");
        updatedInfoDto.setDescription("Updated Restaurant Description");
        updatedInfoDto.setDeliveryFee(5.50);
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));

        restaurantService.updateRestaurantBasicInformation(restaurant1.getId(), updatedInfoDto);

        verify(restaurantRepository).save(restaurantCaptor.capture());
        Restaurant savedRestaurant = restaurantCaptor.getValue();
        assertEquals(updatedInfoDto.getName(), savedRestaurant.getName());
        assertEquals(updatedInfoDto.getDescription(), savedRestaurant.getDescription());
        assertEquals(updatedInfoDto.getDeliveryFee(), savedRestaurant.getDeliveryFee());
    }

    @Test
    @DisplayName("Should update restaurant with new hours and save to database")
    void updateRestaurantHoursAddNewHours() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
        OperatingHoursDto tuesdayHoursDto = new OperatingHoursDto();
        tuesdayHoursDto.setDayOfWeek(DayOfWeek.TUESDAY);
        tuesdayHoursDto.setOpenTime(LocalTime.of(8,0));
        tuesdayHoursDto.setCloseTime(LocalTime.of(20, 0));
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));
        when(operatingHoursRepository.save(any(OperatingHours.class)))
                .thenReturn(new OperatingHours());
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);


        restaurantService.updateRestaurantHours(restaurant1.getId(), List.of(tuesdayHoursDto));


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
    @DisplayName("Should overwrite restaurant's existing if hours for that day already exists")
    void updateRestaurantHoursOverwriteExisting() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
        OperatingHours existingTuesdayHours = new OperatingHours();
        existingTuesdayHours.setDayOfWeek(DayOfWeek.TUESDAY);
        existingTuesdayHours.setOpenTime(LocalTime.of(5, 0));
        existingTuesdayHours.setCloseTime(LocalTime.of(23, 0));
        restaurant1.setOperatingHoursList(List.of(existingTuesdayHours));

        OperatingHoursDto updatedTuesdayHoursDto = new OperatingHoursDto();
        updatedTuesdayHoursDto.setDayOfWeek(DayOfWeek.TUESDAY);
        updatedTuesdayHoursDto.setOpenTime(LocalTime.of(8,0));
        updatedTuesdayHoursDto.setCloseTime(LocalTime.of(20, 0));

        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));
        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);


        restaurantService.updateRestaurantHours(restaurant1.getId(), List.of(updatedTuesdayHoursDto));


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


        RestaurantThinDtoOut restaurantDto = restaurantService.updateRestaurantAddress(restaurant1.getId(), updatedAddressDto);
        AddressDto savedAddress = restaurantDto.getAddress();


        verify(addressRepository).save(any(Address.class));
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
    @DisplayName("Should get all menu items for a restaurant")
    void getAllMenuItemsByRestaurant() {
        List<MenuItem> menuItems = List.of(menuItem1, menuItem2);
        restaurant1.setMenuItems(menuItems);
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));

        List<MenuItemDto> actualMenuItemDtos = restaurantService.getAllMenuItemsByRestaurant(restaurant1.getId());

        for (MenuItem expectedMenuItem: menuItems) {
            assertTrue(actualMenuItemDtos.stream().anyMatch(actualDto -> actualDto.getName().equals(expectedMenuItem.getName())));
        }
    }

    @Test
    @DisplayName("Should get a menu item by its id")
    void getMenuItemByRestaurant() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));

        MenuItemDto actualMenuItemDto = restaurantService.getMenuItemByRestaurant(restaurant1.getId(), menuItem1.getId());

        assertEquals(menuItem1.getName(), actualMenuItemDto.getName());
    }

    @Test
    @DisplayName("Should add a new menu item to a restaurant and save to database")
    void createMenuItem() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
        MenuItemDto menuItemDto = new MenuItemDto();
        menuItemDto.setName("Pizza");
        menuItemDto.setPrice(18.0);
        ArgumentCaptor<MenuItem> menuItemCaptor = ArgumentCaptor.forClass(MenuItem.class);
        when(restaurantRepository.findByIdAndMerchantProfileId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(restaurant1));

        restaurantService.createMenuItem(restaurant1.getId(), menuItemDto);

        verify(menuItemRepository).save(menuItemCaptor.capture());
        MenuItem savedMenuItem = menuItemCaptor.getValue();
        assertEquals(menuItemDto.getName(), savedMenuItem.getName());
        assertEquals(menuItemDto.getPrice(), savedMenuItem.getPrice());
    }

    @Test
    @DisplayName("Should update a restaurant's menu item and save changes to database")
    void updateMenuItem() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
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


        restaurantService.updateMenuItem(restaurant1.getId(), currentMenuItem.getId(), updatedMenuItemDto);


        verify(menuItemRepository).save(menuItemCaptor.capture());
        MenuItem savedMenuItem = menuItemCaptor.getValue();
        assertEquals(updatedMenuItemDto.getName(), savedMenuItem.getName());
        assertEquals(updatedMenuItemDto.getPrice(), savedMenuItem.getPrice());
    }


    private User addMerchantToSecurityContext() {
        MerchantProfile merchantProfile = new MerchantProfile();
        merchantProfile.setId(1L);
        User user = new User();
        user.setMerchantProfile(merchantProfile);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return user;
    }
}
