package com.qelery.mealmojo.api.service;


import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.ProfileNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MerchantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final AddressRepository addressRepository;
    private final MapperUtils mapperUtils;

    @Autowired
    public MerchantService(RestaurantRepository restaurantRepository,
                           MenuItemRepository menuItemRepository,
                           OrderRepository orderRepository,
                           OperatingHoursRepository operatingHoursRepository,
                           AddressRepository addressRepository,
                           MapperUtils mapperUtils) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.addressRepository = addressRepository;
        this.mapperUtils = mapperUtils;
    }


    public List<RestaurantThinDtoOut> getAllRestaurantsOwned() {
        MerchantProfile merchantProfile = getLoggedInUserMerchantProfile();
        List<Restaurant> restaurantsOwned = restaurantRepository.findAllByMerchantProfileId(merchantProfile.getId());
        return restaurantsOwned.stream()
                .map(restaurant -> mapperUtils.map(restaurant, RestaurantThinDtoOut.class))
                .collect(Collectors.toList());
    }

    public RestaurantDtoOut getSingleRestaurantOwned(Long restaurantId) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        return mapperUtils.map(restaurant, RestaurantDtoOut.class);
    }

    public RestaurantDtoOut createRestaurant(RestaurantDtoIn restaurantDtoIn) {
        Restaurant restaurant = mapperUtils.map(restaurantDtoIn, Restaurant.class);
        restaurant.setMerchantProfile(getLoggedInUserMerchantProfile());
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

        for (OperatingHoursDto newHoursDto: newHoursListDto) {
            OperatingHours newHours = mapperUtils.map(newHoursDto, OperatingHours.class);
            newHours.setRestaurant(restaurant);
            Optional<OperatingHours> oldHoursForThatDay = operatingHoursRepository.findByRestaurantIdAndDayOfWeek(restaurant.getId(), newHours.getDayOfWeek());
            if (oldHoursForThatDay.isPresent()) {
                OperatingHours oldHours = oldHoursForThatDay.get();
                mapperUtils.map(newHours, oldHours);
                operatingHoursRepository.save(oldHours);
            } else {
                newHours.setRestaurant(restaurant);
                operatingHoursRepository.save(newHours);
            }
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
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        MenuItem oldMenuItem = optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        mapperUtils.map(newMenuItem, oldMenuItem);
        menuItemRepository.save(oldMenuItem);
        return mapperUtils.map(oldMenuItem, MenuItemDto.class);
    }

    public List<OrderDtoOut> getAllOrdersForOwnedRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        List<Order> orders = orderRepository.findAllByRestaurantId(restaurant.getId());
        return orders.stream().map(order -> mapperUtils.map(order, OrderDtoOut.class)).collect(Collectors.toList());
    }

    public OrderDtoOut getSingleOrderForOwnedRestaurant(Long restaurantId, Long orderId) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        Optional<Order> optionalOrder = orderRepository.findByIdAndRestaurantId(orderId, restaurant.getId());
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        return mapperUtils.map(order, OrderDtoOut.class);
    }

    public OrderDtoOut markOrderComplete(Long restaurantId, Long orderId) {
        Restaurant restaurant = getRestaurantByMerchantProfile(restaurantId);
        Optional<Order> optionalOrder = orderRepository.findByIdAndRestaurantId(orderId, restaurant.getId());
        Order order = optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setIsCompleted(true);
        orderRepository.save(order);
        return mapperUtils.map(order, OrderDtoOut.class);
    }

    private Restaurant getRestaurantByMerchantProfile(Long restaurantId) {
        MerchantProfile merchantProfile = getLoggedInUserMerchantProfile();
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByIdAndMerchantProfileId(restaurantId, merchantProfile.getId());
        return optionalRestaurant.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private MerchantProfile getLoggedInUserMerchantProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        MerchantProfile merchantProfile = userDetails.getUser().getMerchantProfile();
        if (merchantProfile == null) {
            throw new ProfileNotFoundException();
        }
        return merchantProfile;
    }
}
