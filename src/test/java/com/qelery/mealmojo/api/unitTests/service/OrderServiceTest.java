package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.EmptyOrderException;
import com.qelery.mealmojo.api.exception.ProfileNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.service.OrderService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    RestaurantRepository restaurantRepository;
    @Mock
    MenuItemRepository menuItemRepository;
    @Mock
    DistanceUtils distanceUtils;
    @Mock
    Authentication authentication;
    @Spy
    MapperUtils mapperUtils;

    User loggedInUser;
    CustomerProfile customerProfile;
    Restaurant restaurant1;
    Restaurant restaurant2;
    MenuItem menuItem1;
    MenuItem menuItem2;

    @BeforeEach
    void setup() {
        this.restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("Restaurant1");
        this.restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Restaurant2");
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

        this.customerProfile = new CustomerProfile();
        customerProfile.setId(1L);
        this.loggedInUser = new User();
        loggedInUser.setCustomerProfile(customerProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Should get all active restaurants")
    void getAllRestaurants() {
        Restaurant activeRestaurant = restaurant1;
        when(restaurantRepository.findAllByIsActive(true)).thenReturn(List.of(activeRestaurant));

        List<RestaurantThinDtoOut> actualRestaurantDtos = orderService.getAllRestaurants();

        assertEquals(1, actualRestaurantDtos.size());
        assertEquals(activeRestaurant.getName(), actualRestaurantDtos.get(0).getName());
    }

    @Test
    @DisplayName("Should get all active restaurants within specified distance")
    void getRestaurantsWithinDistance() {
        when(distanceUtils.filterWithinDistance(anyList(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of(restaurant1));

        List<RestaurantThinDtoOut> actualRestaurantDtoOut = orderService.getRestaurantsWithinDistance(41.9, -87.6, 10);

        assertEquals(1, actualRestaurantDtoOut.size());
        assertEquals(restaurant1.getName(), actualRestaurantDtoOut.get(0).getName());
    }

    @Test
    @DisplayName("Should get a restaurant by its id")
    void getRestaurant() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));

        RestaurantDtoOut actualRestaurantDto = orderService.getRestaurant(restaurant1.getId());

        assertEquals(restaurant1.getName(), actualRestaurantDto.getName());
    }

    @Test
    @DisplayName("Should throw exception if try to get restaurant by id that doesn't exist")
    void getRestaurantByNonExistentId() {
        Long nonExistentId = 90210L;
        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> orderService.getRestaurant(nonExistentId));
    }

    @Test
    @DisplayName("Should get all menu items for a restaurant")
    void getAllMenuItemsByRestaurant() {

        List<MenuItem> menuItems = List.of(menuItem1, menuItem2);
        restaurant1.setMenuItems(menuItems);
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));

        List<MenuItemDto> actualMenuItemDtos = orderService.getAllMenuItemsByRestaurant(restaurant1.getId());

        for (MenuItem expectedMenuItem: menuItems) {
            assertTrue(actualMenuItemDtos.stream().anyMatch(actualDto -> actualDto.getName().equals(expectedMenuItem.getName())));
        }
    }

    @Test
    @DisplayName("Should get a menu item by its id")
    void getMenuItemByRestaurant() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));

        MenuItemDto actualMenuItemDto = orderService.getMenuItemByRestaurant(restaurant1.getId(), menuItem1.getId());

        assertEquals(menuItem1.getName(), actualMenuItemDto.getName());
    }

    @Test
    @DisplayName("Should get customer's orders for all restaurants if optional restaurant id not supplied")
    void getPlacedOrdersAll() {
        Order order1 = new Order();
        order1.setTip(4.00);
        order1.setRestaurant(restaurant1);
        Order order2 = new Order();
        order2.setTip(7.00);
        order2.setRestaurant(restaurant2);
        List<Order> orders = List.of(order1, order2);
        loggedInUser.getCustomerProfile().setPlacedOrders(orders);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);

        List<OrderDtoOut> orderDtoOutList = orderService.getPlacedOrders(null);

        for (Order expectedOrder: orders) {
            assertTrue(orderDtoOutList.stream().anyMatch(actualDto -> actualDto.getTip().equals(expectedOrder.getTip())));
        }
    }

    @Test
    @DisplayName("Should get customer's orders for specific restaurant if optional restaurant id is supplied")
    void getPlacedOrdersSpecificRestaurant() {
        Order order1 = new Order();
        order1.setTip(4.00);
        order1.setRestaurant(restaurant1);
        Order order2 = new Order();
        order2.setTip(7.00);
        order2.setRestaurant(restaurant2);
        List<Order> orders = List.of(order1, order2);
        loggedInUser.getCustomerProfile().setPlacedOrders(orders);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);

        List<OrderDtoOut> actualOrderDtoOutList = orderService.getPlacedOrders(restaurant1.getId());

        assertEquals(1, actualOrderDtoOutList.size());
        assertEquals(order1.getTip(), actualOrderDtoOutList.get(0).getTip());
    }

    @Test
    @DisplayName("Should save the new order to the database")
    void submitOrder() {
        Map<Long, Integer> quantityMap = new HashMap<>();
        quantityMap.put(menuItem1.getId(), 3);
        quantityMap.put(menuItem2.getId(), 5);

        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setTip(6.00);
        orderDtoIn.setMenuItemQuantitiesMap(quantityMap);

        when(authentication.getPrincipal()).thenReturn(loggedInUser);
        when(menuItemRepository.findById(menuItem1.getId())).thenReturn(Optional.ofNullable(menuItem1));
        when(menuItemRepository.findById(menuItem2.getId())).thenReturn(Optional.ofNullable(menuItem2));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        orderService.submitOrder(orderDtoIn);

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        // Assert Order itself looks correct
        assertEquals(orderDtoIn.getTip(), savedOrder.getTip());
        assertEquals(orderDtoIn.getIsDelivery(), savedOrder.getIsDelivery());
        assertEquals(orderDtoIn.getPaymentMethod(), savedOrder.getPaymentMethod());
        assertSame(loggedInUser.getCustomerProfile(), savedOrder.getCustomerProfile());
        // Assert OrderLines have correct menu item associated with correct quantity ordered
        List<OrderLine> savedOrderLines = savedOrder.getOrderLines();
        assertEquals(2, savedOrderLines.size());
        assertTrue(savedOrderLines.stream().anyMatch(line -> line.getQuantity().equals(3) && line.getMenuItem().getName().equals(menuItem1.getName())));
        assertTrue(savedOrderLines.stream().anyMatch(line -> line.getQuantity().equals(5) && line.getMenuItem().getName().equals(menuItem2.getName())));
    }

    @Test
    @DisplayName("Should throw exception if try to save order that has no menu items listed for purchase")
    void shouldNotSubmitEmptyOrder() {
        Map<Long, Integer> emptyMap = new HashMap<>();

        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setMenuItemQuantitiesMap(emptyMap);

        assertThrows(EmptyOrderException.class, () -> orderService.submitOrder(orderDtoIn));
    }

    @Test
    @DisplayName("Should throw exception if try to save order without customer logged in")
    void shouldOnlySubmitOrderIfCustomerLoggedIn() {
        loggedInUser.setCustomerProfile(null);
        Map<Long, Integer> quantityMap = new HashMap<>();
        quantityMap.put(menuItem1.getId(), 3);
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setMenuItemQuantitiesMap(quantityMap);

        when(authentication.getPrincipal()).thenReturn(loggedInUser);
        when(menuItemRepository.findById(menuItem1.getId())).thenReturn(Optional.ofNullable(menuItem1));

        assertThrows(ProfileNotFoundException.class, () -> orderService.submitOrder(orderDtoIn));
    }
}
