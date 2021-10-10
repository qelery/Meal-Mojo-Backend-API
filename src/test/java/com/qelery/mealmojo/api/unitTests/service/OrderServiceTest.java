package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.EmptyOrderException;
import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.ProfileNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.OrderDtoIn;
import com.qelery.mealmojo.api.model.dto.OrderDtoOut;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.repository.MenuItemRepository;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.service.OrderService;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
        restaurant1.setId(1L);
        restaurant1.setName("Restaurant1");
        restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Restaurant2");
        restaurant2.setIsActive(false);

        menuItem1 = new MenuItem();
        menuItem1.setId(1L);
        menuItem1.setName("Salad 1");
        menuItem1.setPrice(799L);
        menuItem2 = new MenuItem();
        menuItem2.setId(2L);
        menuItem2.setName("Salad 2");
        menuItem2.setPrice(860L);

        order1 = new Order();
        order1.setId(1L);
        order1.setTip(100L);
        order1.setRestaurant(restaurant1);
        order2 = new Order();
        order2.setId(2L);
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

        @Test
        @DisplayName("Should get all orders for owned restaurant by id")
        void getAllOrdersForSingleOwnedRestaurant() {
            User loggedInUser = addMerchantToSecurityContext();
            loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
            Long restaurantId = 1L;

            List<OrderDtoOut> actualOrdersDto = orderService.getOrders(restaurantId);

            assertTrue(actualOrdersDto.stream().anyMatch(order -> order.getTip() == 100L));
        }

        @Test
        @DisplayName("Should throw exception when attempting to get all orders for restaurant not owned by merchant")
        void getAllOrdersForNonOwnedRestaurant_throwException() {
            User loggedInUser = addMerchantToSecurityContext();
            loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));

            MerchantProfile someOtherMerchantsProfile = new MerchantProfile();
            Long restaurantId = 3L;
            Restaurant restaurant3 = new Restaurant();
            restaurant3.setId(restaurantId);
            someOtherMerchantsProfile.setRestaurantsOwned(List.of(restaurant3));

            assertThrows(RestaurantNotFoundException.class, () -> orderService.getOrders(restaurantId));
        }

        @Test
        @DisplayName("Should get all orders for all restaurants owned if restaurant id not supplied")
        void getAllOrdersForAllOwnedRestaurants() {
            User loggedInUser = addMerchantToSecurityContext();
            loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
            Long restaurantId = null;

            List<OrderDtoOut> actualOrdersDto = orderService.getOrders(restaurantId);

            assertTrue(actualOrdersDto.stream().allMatch(order -> order.getRestaurantId().equals(restaurant1.getId()) ||
                    order.getRestaurantId().equals(restaurant2.getId())));
        }

        @Test
        @DisplayName("Should get a single order if it belongs to a restaurant the merchant owns")
        void getSingleOrderForOwnedRestaurant() {
            User loggedInUser = addMerchantToSecurityContext();
            loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
            Long orderId = 1L;

            OrderDtoOut actualOrderDto = orderService.getSingleOrder(orderId);

            assertEquals(order1.getTip(), actualOrderDto.getTip());
        }

        @Test
        @DisplayName("Should throw exception when attempting to get an order that belongs to another merchant's restaurant")
        void getSingleOrderForNonOwnedRestaurant_throwException() {
            User loggedInUser = addMerchantToSecurityContext();
            loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));

            MerchantProfile someOtherMerchantsProfile = new MerchantProfile();
            Restaurant restaurant3 = new Restaurant();
            restaurant3.setId(3L);
            Long orderId = 3L;
            Order order3 = new Order();
            order3.setId(orderId);
            someOtherMerchantsProfile.setRestaurantsOwned(List.of(restaurant3));

            assertThrows(OrderNotFoundException.class, () -> orderService.getSingleOrder(orderId));
        }
    }

    @Nested
    @DisplayName("With customer logged in,")
    class WithCustomerLoggedIn {

        User loggedInUser;

        @BeforeEach
        void addCustomerToSecurityContext() {
            CustomerProfile customerProfile = new CustomerProfile();
            customerProfile.setId(1L);
            loggedInUser = new User();
            loggedInUser.setRole(Role.CUSTOMER);
            loggedInUser.setCustomerProfile(customerProfile);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(loggedInUser);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        @Test
        @DisplayName("Should get all orders placed by customer for specified restaurant id")
        void getAllOrdersPlaceByCustomerByRestaurantId() {
            loggedInUser.getCustomerProfile().setPlacedOrders(List.of(order1, order2));
            when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));
            Long restaurantId = 1L;

            List<OrderDtoOut> actualOrdersDto = orderService.getOrders(restaurantId);

            assertTrue(actualOrdersDto.stream().anyMatch(order -> order.getRestaurantId().equals(restaurantId)));
        }

        @Test
        @DisplayName("Should throw exception when attempting to get all orders placed by customer for restaurant id that doesn't exist")
        void getAllOrdersPlaceByCustomerForNonExistentRestaurant_throwException() {
            loggedInUser.getCustomerProfile().setPlacedOrders(List.of(order1, order2));
            Long restaurantIdThatDoesNotExist = 875L;

            assertThrows(RestaurantNotFoundException.class, () -> orderService.getOrders(restaurantIdThatDoesNotExist));
        }

        @Test
        @DisplayName("Should get all orders placed by customer if optional restaurant id not supplied")
        void getAllOrdersPlaceByCustomer() {
            loggedInUser.getCustomerProfile().setPlacedOrders(List.of(order1, order2));
            Long restaurantId = null;

            List<OrderDtoOut> actualOrdersDto = orderService.getOrders(restaurantId);

            List<Long> expectedOrderIds = List.of(order1.getId(), order2.getId());
            List<Long> actualOrderIds = actualOrdersDto.stream().map(OrderDtoOut::getId).collect(Collectors.toList());
            assertEquals(expectedOrderIds.size(), actualOrderIds.size());
            assertTrue(expectedOrderIds.containsAll(actualOrderIds));
        }

        @Test
        @DisplayName("Should get single order placed by customer by order id")
        void getSinglePlacedOrder() {
            loggedInUser.getCustomerProfile().setPlacedOrders(List.of(order1, order2));
            Long orderId = 1L;

            OrderDtoOut actualOrderDto = orderService.getSingleOrder(orderId);

            assertEquals(order1.getTip(), actualOrderDto.getTip());
        }

        @Test
        @DisplayName("Should throw exception when attempting to order placed by customer by order id that doesn't exist")
        void getSinglePlacedOrderNonExistentId_throwException() {
            loggedInUser.getCustomerProfile().setPlacedOrders(List.of(order1, order2));
            Long orderIdThatDoesNotExist = 2849L;

            assertThrows(OrderNotFoundException.class, () -> orderService.getSingleOrder(orderIdThatDoesNotExist));
        }


        @Test
        @DisplayName("Should save new order to the database")
        void submitOrder() {
            Map<Long, Integer> quantityMap = new HashMap<>();
            quantityMap.put(menuItem1.getId(), 3);
            quantityMap.put(menuItem2.getId(), 5);

            OrderDtoIn orderDtoIn = new OrderDtoIn();
            orderDtoIn.setTip(600L);
            orderDtoIn.setMenuItemQuantitiesMap(quantityMap);

            when(menuItemRepository.findById(menuItem1.getId())).thenReturn(Optional.ofNullable(menuItem1));
            when(menuItemRepository.findById(menuItem2.getId())).thenReturn(Optional.ofNullable(menuItem2));
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

            orderService.submitOrder(orderDtoIn);

            verify(orderRepository).save(orderCaptor.capture());
            Order savedOrder = orderCaptor.getValue();
            // Assert saved Order looks correct
            assertEquals(orderDtoIn.getTip(), savedOrder.getTip());
            assertEquals(orderDtoIn.getIsDelivery(), savedOrder.getIsDelivery());
            assertEquals(orderDtoIn.getPaymentMethod(), savedOrder.getPaymentMethod());
            assertEquals(menuItem1.getRestaurant().getDeliveryFee(), savedOrder.getDeliveryFee());
            assertSame(loggedInUser.getCustomerProfile(), savedOrder.getCustomerProfile());
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

            OrderDtoIn orderDtoIn = new OrderDtoIn();
            orderDtoIn.setMenuItemQuantitiesMap(emptyMap);

            assertThrows(EmptyOrderException.class, () -> orderService.submitOrder(orderDtoIn));
        }
    }

    @Nested
    @DisplayName("With admin logged in,")
    class WithAdminLoggedIn {

        @Test
        @DisplayName("Should get all orders by restaurant id")
        void getAllOrdersByRestaurant() {
            addAdminToSecurityContext();
            Long restaurantId = 1L;
            when(restaurantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(restaurant1));

            List<OrderDtoOut> actualOrdersDto = orderService.getOrders(restaurantId);

            List<Long> expectedOrderIds = restaurant1.getOrders().stream().map(Order::getId).collect(Collectors.toList());
            List<Long> actualOrderIds = actualOrdersDto.stream().map(OrderDtoOut::getId).collect(Collectors.toList());
            assertEquals(expectedOrderIds.size(), actualOrderIds.size());
            assertTrue(expectedOrderIds.containsAll(actualOrderIds));
        }

        @Test
        @DisplayName("Should throw exception when attempting to get orders by restaurant id that doesn't exist")
        void getAllOrdersByNonExistentRestaurant() {
            addAdminToSecurityContext();
            Long restaurantIdThatDoesNotExit = 433L;
            when(restaurantRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(RestaurantNotFoundException.class, () -> orderService.getOrders(restaurantIdThatDoesNotExit));
        }

        @Test
        @DisplayName("Should get all orders in database if restaurant id not supplied")
        void getAllOrders() {
            addAdminToSecurityContext();
            Long restaurantId = null;
            when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

            List<OrderDtoOut> actualOrdersDto = orderService.getOrders(restaurantId);


            List<Long> expectedOrderIds = List.of(order1.getId(), order2.getId());
            List<Long> actualOrderIds = actualOrdersDto.stream().map(OrderDtoOut::getId).collect(Collectors.toList());
            assertEquals(expectedOrderIds.size(), actualOrdersDto.size());
            assertTrue(expectedOrderIds.containsAll(actualOrderIds));
        }

        @Test
        @DisplayName("Should get single by order id")
        void getSingleOrderById() {
            addAdminToSecurityContext();
            when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(order1));
            Long orderId = 1L;

            OrderDtoOut actualOrderDto = orderService.getSingleOrder(orderId);

            assertEquals(order1.getTip(), actualOrderDto.getTip());
        }

        @Test
        @DisplayName("Should throw exception when attempting to get by order id that doesn't exist")
        void getSingleOrderByNonExistentId_throwException() {
            addAdminToSecurityContext();
            when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
            Long orderIdThatDoesNotExist = 3098L;

            assertThrows(OrderNotFoundException.class, () -> orderService.getSingleOrder(orderIdThatDoesNotExist));
        }
    }

    @Test
    @DisplayName("Should throw exception if try to save order without customer logged in")
    void shouldOnlySubmitOrderIfCustomerLoggedIn() {
        addMerchantToSecurityContext();
        Map<Long, Integer> quantityMap = new HashMap<>();
        quantityMap.put(menuItem1.getId(), 3);
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setMenuItemQuantitiesMap(quantityMap);

        assertThrows(ProfileNotFoundException.class, () -> orderService.submitOrder(orderDtoIn));
    }

    @Test
    @DisplayName("Should mark an order complete if it belong to one of merchant's restaurants")
    void markOrderComplete() {
        User loggedInUser = addMerchantToSecurityContext();
        loggedInUser.getMerchantProfile().setRestaurantsOwned(List.of(restaurant1, restaurant2));
        order1.setIsCompleted(false);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        orderService.markOrderComplete(order1.getId());

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertTrue(savedOrder.getIsCompleted());
    }

    private User addMerchantToSecurityContext() {
        MerchantProfile merchantProfile = new MerchantProfile();
        merchantProfile.setId(1L);
        User user = new User();
        user.setRole(Role.MERCHANT);
        user.setMerchantProfile(merchantProfile);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return user;
    }

    private void addAdminToSecurityContext() {
        User user = new User();
        user.setRole(Role.ADMIN);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
