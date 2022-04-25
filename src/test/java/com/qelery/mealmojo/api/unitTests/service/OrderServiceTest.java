package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.EmptyOrderException;
import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.OrderCreationDto;
import com.qelery.mealmojo.api.model.dto.OrderDto;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.service.OrderService;
import com.qelery.mealmojo.api.service.RestaurantService;
import com.qelery.mealmojo.api.service.UserService;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuItemRepository menuItemRepository;
    @Mock
    RestaurantService restaurantService;
    @Mock
    UserService userService;
    @Mock
    Authentication authentication;
    @Spy
    MapperUtils mapperUtils;

    Restaurant restaurant1;
    Restaurant restaurant2;
    MenuItem menuItem1;
    MenuItem menuItem2;
    Order order1;
    Order order2;

    @BeforeEach
    void setup() {
        restaurant1 = new Restaurant();
        restaurant1.setRestaurantId(1L);
        restaurant1.setName("Restaurant1");
        restaurant2 = new Restaurant();
        restaurant2.setRestaurantId(2L);
        restaurant2.setName("Restaurant2");
        restaurant2.setIsActive(false);

        menuItem1 = new MenuItem();
        menuItem1.setMenuItemId(1L);
        menuItem1.setName("Salad 1");
        menuItem1.setPrice(799L);
        menuItem2 = new MenuItem();
        menuItem2.setMenuItemId(2L);
        menuItem2.setName("Salad 2");
        menuItem2.setPrice(860L);

        order1 = new Order();
        order1.setOrderId(1L);
        order1.setTip(100L);
        order1.setRestaurant(restaurant1);
        order2 = new Order();
        order2.setOrderId(2L);
        order2.setTip(200L);
        order2.setRestaurant(restaurant2);

        restaurant1.setMenuItems(List.of(menuItem1, menuItem2));
        restaurant1.setOrders(List.of(order1));
        restaurant2.setOrders(List.of(order2));

        menuItem1.setRestaurant(restaurant1);
        menuItem2.setRestaurant(restaurant1);
    }

    @Nested
    @DisplayName("With merchant logged in,")
    class WithMerchantLoggedIn {

        MerchantProfile merchantProfile;

        @BeforeEach
        void addMockMerchantProfile() {
            merchantProfile = new MerchantProfile();
            merchantProfile.setId(1L);
            merchantProfile.setRestaurantsOwned(List.of(restaurant1, restaurant2));
        }

        @Test
        @DisplayName("Should get all orders for owned restaurant by id")
        void getAllOrdersForSingleOwnedRestaurant() {
            Long restaurantId = 1L;
            when(userService.getLoggedInUserMerchantProfile())
                    .thenReturn(merchantProfile);
            when(userService.getLoggedInUserRole()).thenReturn(Role.MERCHANT);

            List<OrderDto> actualOrdersDto = orderService.getOrders(restaurantId);

            assertTrue(actualOrdersDto.stream().anyMatch(order -> order.getTip() == 100L));
        }

        @Test
        @DisplayName("Should throw exception when attempting to get all orders for restaurant not owned by merchant")
        void getAllOrdersForNonOwnedRestaurant_throwException() {
            MerchantProfile someOtherMerchantsProfile = new MerchantProfile();
            Long restaurantId = 3L;
            Restaurant restaurant3 = new Restaurant();
            restaurant3.setRestaurantId(restaurantId);
            someOtherMerchantsProfile.setRestaurantsOwned(List.of(restaurant3));
            when(userService.getLoggedInUserMerchantProfile())
                    .thenReturn(merchantProfile);
            when(userService.getLoggedInUserRole()).thenReturn(Role.MERCHANT);

            assertThrows(RestaurantNotFoundException.class, () -> orderService.getOrders(restaurantId));
        }

        @Test
        @DisplayName("Should get all orders for all restaurants owned if restaurant id not supplied")
        void getAllOrdersForAllOwnedRestaurants() {
            Long restaurantId = null;
            when(userService.getLoggedInUserMerchantProfile())
                    .thenReturn(merchantProfile);
            when(userService.getLoggedInUserRole()).thenReturn(Role.MERCHANT);

            List<OrderDto> actualOrdersDto = orderService.getOrders(restaurantId);

            assertTrue(actualOrdersDto.stream().allMatch(order -> order.getRestaurantId().equals(restaurant1.getRestaurantId()) ||
                    order.getRestaurantId().equals(restaurant2.getRestaurantId())));
        }

        @Test
        @DisplayName("Should get a single order if it belongs to a restaurant the merchant owns")
        void getSingleOrderForOwnedRestaurant() {
            Long orderId = 1L;
            when(userService.getLoggedInUserMerchantProfile())
                    .thenReturn(merchantProfile);
            when(userService.getLoggedInUserRole()).thenReturn(Role.MERCHANT);

            OrderDto actualOrderDto = orderService.getSingleOrder(orderId);

            assertEquals(order1.getTip(), actualOrderDto.getTip());
        }

        @Test
        @DisplayName("Should throw exception when attempting to get an order that belongs to another merchant's restaurant")
        void getSingleOrderForNonOwnedRestaurant_throwException() {
            MerchantProfile someOtherMerchantsProfile = new MerchantProfile();
            Restaurant restaurant3 = new Restaurant();
            restaurant3.setRestaurantId(3L);
            Long orderId = 3L;
            Order order3 = new Order();
            order3.setOrderId(orderId);
            someOtherMerchantsProfile.setRestaurantsOwned(List.of(restaurant3));
            when(userService.getLoggedInUserMerchantProfile())
                    .thenReturn(merchantProfile);
            when(userService.getLoggedInUserRole()).thenReturn(Role.MERCHANT);

            assertThrows(OrderNotFoundException.class, () -> orderService.getSingleOrder(orderId));
        }
    }

    @Nested
    @DisplayName("With customer logged in,")
    class WithCustomerLoggedIn {

        CustomerProfile customerProfile;

        @BeforeEach
        void addMockCustomerProfile() {
            customerProfile = new CustomerProfile();
            customerProfile.setId(1L);
            customerProfile.setPlacedOrders(List.of(order1, order2));
        }

        @Test
        @DisplayName("Should get all orders placed by customer for specified restaurant id")
        void getAllOrdersPlaceByCustomerByRestaurantId() {
            Long restaurantId = 1L;
            when(userService.getLoggedInUserRole()).thenReturn(Role.CUSTOMER);
            when(userService.getLoggedInCustomerProfile()).thenReturn(customerProfile);
            when(restaurantService.getRestaurantEntity(anyLong())).thenReturn(restaurant1);


            List<OrderDto> actualOrdersDto = orderService.getOrders(restaurantId);

            assertTrue(actualOrdersDto.stream().anyMatch(order -> order.getRestaurantId().equals(restaurantId)));
        }

        @Test
        @DisplayName("Should get all orders placed by customer if optional restaurant id not supplied")
        void getAllOrdersPlaceByCustomer() {
            Long restaurantId = null;
            when(userService.getLoggedInUserRole()).thenReturn(Role.CUSTOMER);
            when(userService.getLoggedInCustomerProfile()).thenReturn(customerProfile);
            customerProfile.setPlacedOrders(List.of(order1, order2));


            List<OrderDto> actualOrdersDto = orderService.getOrders(restaurantId);

            List<Long> expectedOrderIds = List.of(order1.getOrderId(), order2.getOrderId());
            List<Long> actualOrderIds = actualOrdersDto.stream().map(OrderDto::getOrderId).collect(Collectors.toList());
            assertEquals(expectedOrderIds.size(), actualOrderIds.size());
            assertTrue(expectedOrderIds.containsAll(actualOrderIds));
        }

        @Test
        @DisplayName("Should get single order placed by customer by order id")
        void getSinglePlacedOrder() {
            Long orderId = 1L;
            customerProfile.setPlacedOrders(List.of(order1, order2));
            when(userService.getLoggedInUserRole()).thenReturn(Role.CUSTOMER);
            when(userService.getLoggedInCustomerProfile()).thenReturn(customerProfile);

            OrderDto actualOrderDto = orderService.getSingleOrder(orderId);

            assertEquals(order1.getTip(), actualOrderDto.getTip());
        }

        @Test
        @DisplayName("Should throw exception when attempting to order placed by customer by order id that doesn't exist")
        void getSinglePlacedOrderNonExistentId_throwException() {
            Long orderIdThatDoesNotExist = 2849L;
            customerProfile.setPlacedOrders(List.of(order1, order2));
            when(userService.getLoggedInUserRole()).thenReturn(Role.CUSTOMER);
            when(userService.getLoggedInCustomerProfile()).thenReturn(customerProfile);

            assertThrows(OrderNotFoundException.class, () -> orderService.getSingleOrder(orderIdThatDoesNotExist));
        }


        @Test
        @DisplayName("Should save new order to the database")
        void submitOrder() {
            Map<Long, Integer> quantityMap = new HashMap<>();
            quantityMap.put(menuItem1.getMenuItemId(), 3);
            quantityMap.put(menuItem2.getMenuItemId(), 5);

            OrderCreationDto orderCreationDto = new OrderCreationDto();
            orderCreationDto.setTip(600L);
            orderCreationDto.setMenuItemQuantitiesMap(quantityMap);

            when(userService.getLoggedInCustomerProfile()).thenReturn(customerProfile);
            when(menuItemRepository.findById(menuItem1.getMenuItemId())).thenReturn(Optional.ofNullable(menuItem1));
            when(menuItemRepository.findById(menuItem2.getMenuItemId())).thenReturn(Optional.ofNullable(menuItem2));
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

            orderService.submitOrder(orderCreationDto);

            verify(orderRepository).save(orderCaptor.capture());
            Order savedOrder = orderCaptor.getValue();
            // Assert saved Order looks correct
            assertEquals(orderCreationDto.getTip(), savedOrder.getTip());
            assertEquals(orderCreationDto.getIsDelivery(), savedOrder.getIsDelivery());
            assertEquals(orderCreationDto.getPaymentMethod(), savedOrder.getPaymentMethod());
            assertEquals(menuItem1.getRestaurant().getDeliveryFee(), savedOrder.getDeliveryFee());
            assertSame(customerProfile, savedOrder.getCustomerProfile());
            // Assert saved OrderLines have correct menu item associated with correct quantity ordered
            List<OrderLine> savedOrderLines = savedOrder.getOrderLines();
            assertEquals(2, savedOrderLines.size());
            assertTrue(savedOrderLines.stream().anyMatch(line -> line.getQuantity().equals(3) && line.getMenuItem().getName().equals(menuItem1.getName())));
            assertTrue(savedOrderLines.stream().anyMatch(line -> line.getQuantity().equals(5) && line.getMenuItem().getName().equals(menuItem2.getName())));
        }

        @Test
        @DisplayName("Should throw exception when attempting to save order that has no menu items listed for purchase")
        void shouldNotSubmitEmptyOrder() {
            Map<Long, Integer> emptyMap = new HashMap<>();

            OrderCreationDto orderCreationDto = new OrderCreationDto();
            orderCreationDto.setMenuItemQuantitiesMap(emptyMap);

            assertThrows(EmptyOrderException.class, () -> orderService.submitOrder(orderCreationDto));
        }
    }

    @Nested
    @DisplayName("With admin logged in,")
    class WithAdminLoggedIn {

        @Test
        @DisplayName("Should get all orders by restaurant id")
        void getAllOrdersByRestaurant() {
            Long restaurantId = 1L;
            when(userService.getLoggedInUserRole()).thenReturn(Role.ADMIN);
            when(restaurantService.getRestaurantEntity(anyLong())).thenReturn(restaurant1);

            List<OrderDto> actualOrdersDto = orderService.getOrders(restaurantId);

            List<Long> expectedOrderIds = restaurant1.getOrders().stream().map(Order::getOrderId).collect(Collectors.toList());
            List<Long> actualOrderIds = actualOrdersDto.stream().map(OrderDto::getOrderId).collect(Collectors.toList());
            assertEquals(expectedOrderIds.size(), actualOrderIds.size());
            assertTrue(expectedOrderIds.containsAll(actualOrderIds));
        }

        @Test
        @DisplayName("Should get all orders in database if restaurant id not supplied")
        void getAllOrders() {
            Long restaurantId = null;
            when(userService.getLoggedInUserRole()).thenReturn(Role.ADMIN);
            when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

            List<OrderDto> actualOrdersDto = orderService.getOrders(restaurantId);


            List<Long> expectedOrderIds = List.of(order1.getOrderId(), order2.getOrderId());
            List<Long> actualOrderIds = actualOrdersDto.stream().map(OrderDto::getOrderId).collect(Collectors.toList());
            assertEquals(expectedOrderIds.size(), actualOrdersDto.size());
            assertTrue(expectedOrderIds.containsAll(actualOrderIds));
        }

        @Test
        @DisplayName("Should get single by order id")
        void getSingleOrderById() {
            when(userService.getLoggedInUserRole()).thenReturn(Role.ADMIN);
            when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(order1));
            Long orderId = 1L;

            OrderDto actualOrderDto = orderService.getSingleOrder(orderId);

            assertEquals(order1.getTip(), actualOrderDto.getTip());
        }

        @Test
        @DisplayName("Should throw exception when attempting to get by order id that doesn't exist")
        void getSingleOrderByNonExistentId_throwException() {
            when(userService.getLoggedInUserRole()).thenReturn(Role.ADMIN);
            when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
            Long orderIdThatDoesNotExist = 3098L;

            assertThrows(OrderNotFoundException.class, () -> orderService.getSingleOrder(orderIdThatDoesNotExist));
        }
    }

    @Test
    @DisplayName("Should mark an order complete if it belong to one of merchant's restaurants")
    void markOrderComplete() {
        MerchantProfile merchantProfile = new MerchantProfile();
        merchantProfile.setId(1L);
        merchantProfile.setRestaurantsOwned(List.of(restaurant1, restaurant2));
        when(userService.getLoggedInUserMerchantProfile())
                .thenReturn(merchantProfile);
        order1.setIsCompleted(false);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);


        orderService.markOrderComplete(order1.getOrderId());

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertTrue(savedOrder.getIsCompleted());
    }
}
