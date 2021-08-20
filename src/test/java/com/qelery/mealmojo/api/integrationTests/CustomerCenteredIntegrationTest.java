package com.qelery.mealmojo.api.integrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.exception.global.ErrorResponseBody;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.CustomerProfile;
import com.qelery.mealmojo.api.model.entity.OrderLine;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import com.qelery.mealmojo.api.testUtils.HttpRequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts = {"/seed/load-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/seed/clear-test-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureMockMvc
class CustomerCenteredIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HttpRequestDispatcher httpRequestDispatcher;

    @Autowired
    private UserDetailsService userDetailsService;


    // Expected values are based on the test data loaded into the test H2
    // database using the sql scripts in the directory /test/resources/seed
    @Nested
    @DisplayName("[Integration Tests - Through All Layers] With customer logged in,")
    class throughAllLayers_customer {

        @Test
        @DisplayName("Should get all active restaurants")
        @WithUserDetails(value = "john_customer@example.com")
        void getAllActiveRestaurants_customer() throws Exception {
            List<String> expectedRestaurantNames = List.of("Joy Yee", "Portillo's Hot Dogs", "Pizano's Pizza & Pasta");
            String deactivatedRestaurantName = "Daley's Restaurant";

            String url = "/api/order/restaurants";
            String jsonResponse = httpRequestDispatcher.performGET(url);
            List<RestaurantThinDtoOut> actualRestaurants = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            List<String> actualRestaurantNames = actualRestaurants.stream()
                    .map(RestaurantThinDtoOut::getName)
                    .collect(Collectors.toList());

            assertEquals(3, actualRestaurants.size());
            assertFalse(actualRestaurantNames.contains(deactivatedRestaurantName));
            assertTrue(actualRestaurantNames.containsAll(expectedRestaurantNames));
        }

        @Test
        @DisplayName("Should get all active restaurants within specified distance of customer")
        @WithUserDetails(value = "alice_customer@example.com")
        void getRestaurantsWithinDistance_customer() throws Exception {
            String expectedRestaurantName = "Joy Yee";
            User alice = (User) userDetailsService.loadUserByUsername("alice_customer@example.com");
            Address address = alice.getCustomerProfile().getAddress();
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            int maxDistance = 5;

            String url = String.format("/api/order/restaurants/nearby?latitude=%f&longitude=%f&maxDistanceMiles=%d",
                    latitude, longitude, maxDistance);
            String jsonResponse = httpRequestDispatcher.performGET(url);
            List<RestaurantThinDtoOut> actualRestaurants = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            List<String> actualRestaurantNames = actualRestaurants.stream()
                    .map(RestaurantThinDtoOut::getName)
                    .collect(Collectors.toList());

            assertEquals(1, actualRestaurantNames.size());
            assertTrue(actualRestaurantNames.contains(expectedRestaurantName));
        }

        @Test
        @DisplayName("Should get a restaurant by its id")
        @WithUserDetails("john_customer@example.com")
        void getRestaurant_customer() throws Exception {
            long expectedRestaurantId = 1L;
            String expectedRestaurantName = "Portillo's Hot Dogs";

            String url = "/api/order/restaurants/" + expectedRestaurantId;
            String jsonResponse = httpRequestDispatcher.performGET(url);
            RestaurantDtoOut actualRestaurant = objectMapper.readValue(jsonResponse, RestaurantDtoOut.class);

            assertEquals(expectedRestaurantId, actualRestaurant.getId());
            assertEquals(expectedRestaurantName, actualRestaurant.getName());
        }

        @Test
        @DisplayName("Should get all menu items for a restaurant")
        @WithUserDetails("alice_customer@example.com")
        void getAllMenuItemsByRestaurant_customer() throws Exception {
            List<String> expectedMenuItemNames = List.of("Cheese Pizza", "Sausage Pizza");
            long restaurantId = 2L;

            String url = "/api/order/restaurants/" + restaurantId + "/menuitems";
            String jsonResponse = httpRequestDispatcher.performGET(url);
            List<MenuItemDto> actualMenuItems = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            List<String> actualMenuItemNames = actualMenuItems.stream().map(MenuItemDto::getName).collect(Collectors.toList());

            assertEquals(2, actualMenuItems.size());
            assertTrue(actualMenuItemNames.containsAll(expectedMenuItemNames));
        }

        @Test
        @DisplayName("Should get a specific menu item for a restaurant")
        @WithUserDetails("john_customer@example.com")
        void getMenuItemByRestaurant() throws Exception {
            long restaurantId = 1L;
            long expectedMenuItemId = 1L;
            String expectedMenuItemName = "Chicago-Style Hot Dog";

            String url = "/api/order/restaurants/" + restaurantId + "/menuitems/" + expectedMenuItemId;
            String jsonResponse = httpRequestDispatcher.performGET(url);
            MenuItemDto actualMenuItem = objectMapper.readValue(jsonResponse, MenuItemDto.class);

            assertEquals(expectedMenuItemId, actualMenuItem.getId());
            assertEquals(expectedMenuItemName, actualMenuItem.getName());
        }

        @Test
        @DisplayName("Get all orders for logged in customer")
        @WithUserDetails("alice_customer@example.com")
        void getOrders() throws Exception {
            String url = "/api/order/past";
            String jsonResponse = httpRequestDispatcher.performGET(url);
            List<OrderDtoOut> actualOrders = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });

            assertEquals(3, actualOrders.size());

            Optional<OrderDtoOut> optionalOrderId1 = actualOrders.stream().filter(order -> order.getId() == 1L).findFirst();
            Optional<OrderDtoOut> optionalOrderId3 = actualOrders.stream().filter(order -> order.getId() == 3L).findFirst();
            Optional<OrderDtoOut> optionalOrderId4 = actualOrders.stream().filter(order -> order.getId() == 4L).findFirst();
            assertTrue(optionalOrderId1.isPresent());
            assertTrue(optionalOrderId3.isPresent());
            assertTrue(optionalOrderId4.isPresent());
            OrderDtoOut actualOrderId1 = optionalOrderId1.get();
            OrderDtoOut actualOrderId3 = optionalOrderId3.get();
            OrderDtoOut actualOrderId4 = optionalOrderId4.get();

            assertEquals("Portillo's Hot Dogs", actualOrderId1.getRestaurantName());
            assertEquals(2, actualOrderId1.getOrderLines().size());
            assertEquals(14.50, calculateTotalPrice(actualOrderId1));
            assertEquals("Daley's Restaurant", actualOrderId3.getRestaurantName());
            assertEquals(1, actualOrderId3.getOrderLines().size());
            assertEquals(18.50, calculateTotalPrice(actualOrderId3));
            assertEquals("Joy Yee", actualOrderId4.getRestaurantName());
            assertEquals(1, actualOrderId4.getOrderLines().size());
            assertEquals(33.40, calculateTotalPrice(actualOrderId4));
        }

        @Test
        @DisplayName("Should submit an order for logged in customer")
        @WithUserDetails("alice_customer@example.com")
        void submitOrder() throws Exception {
            long hotdogMenuItemId = 1L;
            long italianBeefMenuItemId = 2L;
            Map<Long, Integer> menuItemsQuantitiesMap = new HashMap<>();
            menuItemsQuantitiesMap.put(hotdogMenuItemId, 5);
            menuItemsQuantitiesMap.put(italianBeefMenuItemId, 3);

            OrderDtoIn orderDtoIn = new OrderDtoIn();
            orderDtoIn.setTip(5.00);
            orderDtoIn.setIsDelivery(true);
            orderDtoIn.setPaymentMethod(PaymentMethod.CARD);
            orderDtoIn.setMenuItemQuantitiesMap(menuItemsQuantitiesMap);

            String url = "/api/order/submit";
            String jsonResponse = httpRequestDispatcher.performPOST(url, orderDtoIn);
            OrderDtoOut actualOrder = objectMapper.readValue(jsonResponse, OrderDtoOut.class);

            assertEquals(orderDtoIn.getTip(), actualOrder.getTip());
            assertFalse(actualOrder.getIsCompleted());
            assertTrue(actualOrder.getIsDelivery());
            assertEquals(orderDtoIn.getPaymentMethod(), actualOrder.getPaymentMethod());
            assertEquals(2, actualOrder.getOrderLines().size());
            assertEquals("Portillo's Hot Dogs", actualOrder.getRestaurantName());
            CustomerProfile loggedInCustomer = ((User) userDetailsService.loadUserByUsername("alice_customer@example.com")).getCustomerProfile();
            assertEquals(loggedInCustomer.getFirstName(), actualOrder.getCustomerProfileFirstName());
            assertEquals(loggedInCustomer.getLastName(), actualOrder.getCustomerProfileLastName());
            assertEquals(loggedInCustomer.getAddress(), actualOrder.getCustomerProfileAddress());
        }

        @Nested
        @DisplayName("And customer has supplied non-existent entity id,")
        class throughAllLayers_customer_nonExistentEntity {

            @Test
            @WithUserDetails("alice_customer@example.com")
            @DisplayName("Should get 404 response when trying to get restaurant that doesn't exist")
            void should404OnNonExistentRestaurant() throws Exception {
                long restaurantIdThatDoesNotExist = 3001L;
                String expectedErrorMessage = new RestaurantNotFoundException(3001L).getMessage();

                String url;
                String jsonResponse;

                url = "/api/order/restaurants/" + restaurantIdThatDoesNotExist;
                jsonResponse = httpRequestDispatcher.performGET(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                url = "/api/order/restaurants/" + restaurantIdThatDoesNotExist + "/menuitems";
                jsonResponse = httpRequestDispatcher.performGET(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
            }

            @Test
            @WithUserDetails("john_customer@example.com")
            @DisplayName("Should get 404 response when trying to get menu item that doesn't exist")
            void should404OnNonExistentMenuItem() throws Exception {
                long restaurantId = 2L;
                long menuItemIdThatDoesNotExist = 99099L;
                String expectedErrorMessage = new MenuItemNotFoundException(99099L).getMessage();

                String url = "/api/order/restaurants/" + restaurantId + "/menuitems/" + menuItemIdThatDoesNotExist;
                String jsonResponse = httpRequestDispatcher.performGET(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
            }
        }
    }

    private double calculateTotalPrice(OrderDtoOut order) {
        double total = 0;
        total += order.getTip();
        for (OrderLine orderLine : order.getOrderLines()) {
            total += orderLine.getQuantity() * orderLine.getPriceEach();
        }
        return total;
    }

    private void assertContainsErrorMessage(String jsonResponse, String expectedErrorMessage) throws Exception {
        ErrorResponseBody responseBody = objectMapper.readValue(jsonResponse, ErrorResponseBody.class);
        assertEquals(expectedErrorMessage, responseBody.getMessage());
    }
}