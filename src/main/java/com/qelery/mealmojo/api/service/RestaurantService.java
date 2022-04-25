package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.repository.AddressRepository;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OperatingHoursRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.service.utility.DistanceUtils;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final AddressRepository addressRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final UserService userService;
    private final DistanceUtils distanceUtils;
    private final MapperUtils mapperUtils;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository,
                             MenuItemRepository menuItemRepository,
                             AddressRepository addressRepository,
                             OperatingHoursRepository operatingHoursRepository,
                             UserService userService,
                             DistanceUtils distanceUtils,
                             MapperUtils mapperUtils) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.addressRepository = addressRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.userService = userService;
        this.distanceUtils = distanceUtils;
        this.mapperUtils = mapperUtils;
    }

    public List<RestaurantThinDtoOut> getAllRestaurants() {
        List<Restaurant> activeRestaurants = restaurantRepository.findAllByIsActive(true);
        return mapperUtils.mapAll(activeRestaurants, RestaurantThinDtoOut.class);
    }

    public List<RestaurantThinDtoOut> getRestaurantsWithinDistance(double latitude,
                                                                   double longitude,
                                                                   int maxDistanceMiles) {
        List<Restaurant> activeRestaurants = restaurantRepository.findAllByIsActive(true);
        List<Restaurant> restaurants = distanceUtils.filterWithinDistance(activeRestaurants, latitude, longitude, maxDistanceMiles);
        return mapperUtils.mapAll(restaurants, RestaurantThinDtoOut.class);
    }

    public RestaurantDtoOut getRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurantEntity(restaurantId);
        return mapperUtils.map(restaurant, RestaurantDtoOut.class);
    }

    public Restaurant getRestaurantEntity(Long restaurantId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        return optionalRestaurant.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    // TODO: Tests for locked down routes.
    // If I have locked down routes, is that enough. For example, in the below test
    // should I also be checking the user's role and throwing an error if it's incorrect?
    public List<RestaurantThinDtoOut> getAllRestaurantsOwnedByLoggedInMerchant() {
        MerchantProfile merchantProfile = userService.getLoggedInUserMerchantProfile();
        List<Restaurant> restaurantsOwned = restaurantRepository.findAllByMerchantProfileId(merchantProfile.getId());
        return restaurantsOwned.stream()
                .map(restaurant -> mapperUtils.map(restaurant, RestaurantThinDtoOut.class))
                .collect(Collectors.toList());
    }

    public RestaurantDtoOut getSingleRestaurantOwnedByLoggedInMerchant(Long restaurantId) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        return mapperUtils.map(restaurant, RestaurantDtoOut.class);
    }

    public RestaurantDtoOut createRestaurant(RestaurantDtoIn restaurantDtoIn) {
        MerchantProfile merchantProfile = userService.getLoggedInUserMerchantProfile();
        Restaurant restaurant = mapperUtils.map(restaurantDtoIn, Restaurant.class);
        restaurant.setMerchantProfile(merchantProfile);
        restaurantRepository.save(restaurant);
        return mapperUtils.map(restaurant, RestaurantDtoOut.class);
    }

    public RestaurantThinDtoOut updateRestaurantBasicInformation(Long restaurantId, RestaurantDtoIn newRestaurantInfoDto) {
        Restaurant oldRestaurantInfo = getRestaurantByMerchantProfile(restaurantId);
        Restaurant newRestaurantInfo = mapperUtils.map(newRestaurantInfoDto, Restaurant.class);
        mapperUtils.map(newRestaurantInfo, oldRestaurantInfo);
        restaurantRepository.save(oldRestaurantInfo);
        return mapperUtils.map(oldRestaurantInfo, RestaurantThinDtoOut.class);
    }


    public RestaurantThinDtoOut updateRestaurantHours(Long restaurantId, List<OperatingHoursDto> newHoursListDto) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);

        for (OperatingHoursDto newHoursDto : newHoursListDto) {
            OperatingHours newHours = mapperUtils.map(newHoursDto, OperatingHours.class);
            Optional<OperatingHours> oldHoursForThatDay = restaurant.getOperatingHoursList()
                    .stream()
                    .filter(hours -> hours.getDayOfWeek().equals(newHours.getDayOfWeek()))
                    .findFirst();
            if (oldHoursForThatDay.isPresent()) {
                OperatingHours oldHours = oldHoursForThatDay.get();
                mapperUtils.map(newHours, oldHours);
            } else {
                operatingHoursRepository.save(newHours);
                restaurant.getOperatingHoursList().add(newHours);
            }
            restaurantRepository.save(restaurant);
        }
        return mapperUtils.map(restaurant, RestaurantThinDtoOut.class);
    }

    public RestaurantThinDtoOut updateRestaurantAddress(Long restaurantId, AddressDto newAddressDto) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        Address newAddress = mapperUtils.map(newAddressDto, Address.class);
        Address oldAddress = restaurant.getAddress();
        mapperUtils.map(newAddress, oldAddress);
        addressRepository.save(oldAddress);
        return mapperUtils.map(restaurant, RestaurantThinDtoOut.class);
    }

    public List<MenuItemDto> getAllMenuItemsByRestaurant(Long restaurantId) {
        RestaurantDtoOut restaurant = getRestaurant(restaurantId);
        return mapperUtils.mapAll(restaurant.getMenuItems(), MenuItemDto.class);
    }

    public MenuItemDto getMenuItemByRestaurant(Long restaurantId, Long menuItemId) {
        RestaurantDtoOut restaurant = getRestaurant(restaurantId);
        Optional<MenuItemDto> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getMenuItemId().equals(menuItemId))
                .findFirst();
        MenuItemDto menuItem = optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        return mapperUtils.map(menuItem, MenuItemDto.class);
    }

    public MenuItemDto createMenuItem(Long restaurantId, MenuItemDto menuItemDto) {
        MenuItem menuItem = mapperUtils.map(menuItemDto, MenuItem.class);
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        menuItem.setRestaurant(restaurant);
        menuItemRepository.save(menuItem);
        return mapperUtils.map(menuItem, MenuItemDto.class);
    }

    public MenuItemDto updateMenuItem(Long restaurantId, Long menuItemId, MenuItemDto newMenuItemDto) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        MenuItem newMenuItem = mapperUtils.map(newMenuItemDto, MenuItem.class);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getMenuItemId().equals(menuItemId))
                .findFirst();
        MenuItem oldMenuItem = optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        mapperUtils.map(newMenuItem, oldMenuItem);
        menuItemRepository.save(oldMenuItem);
        return mapperUtils.map(oldMenuItem, MenuItemDto.class);
    }

    private Restaurant getRestaurantByMerchantProfile(Long restaurantId) {
        MerchantProfile merchantProfile = userService.getLoggedInUserMerchantProfile();
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByRestaurantIdAndMerchantProfileId(restaurantId, merchantProfile.getId());
        return optionalRestaurant.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }
}
