package com.qelery.mealmojo.api.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.CustomerProfile;
import com.qelery.mealmojo.api.model.entity.OrderLine;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts = {"/seed/load-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/seed/clear-test-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureMockMvc
class CustomerCenteredIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    // Expected values are based on the test data loaded via the sql scripts in /test/resources/seed
    @Nested
    @DisplayName("[Integration Tests - Through All Layers] With customer logged in")
    class throughAllLayers {

        @Test
        @DisplayName("Should get all active restaurants")
        @WithUserDetails(value = "john_customer@example.com")
        void getAllActiveRestaurants_customer() throws Exception {
            List<String> expectedRestaurantNames = List.of("Joy Yee", "Portillo's Hot Dogs", "Pizano's Pizza & Pasta");
            String deactivatedRestaurantName = "Daley's Restaurant";

            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/order/restaurants"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse();
            String json = response.getContentAsString();
            List<RestaurantThinDtoOut> actualRestaurants = objectMapper.readValue(json, new TypeReference<>() {
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

            MockHttpServletResponse response = mockMvc.perform(
                            get(url))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse();
            String json = response.getContentAsString();
            List<RestaurantThinDtoOut> actualRestaurants = objectMapper.readValue(json, new TypeReference<>() {
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

            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/order/restaurants/" + expectedRestaurantId))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse();

            String json = response.getContentAsString();
            RestaurantDtoOut actualRestaurant = objectMapper.readValue(json, RestaurantDtoOut.class);

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

            MockHttpServletResponse response = mockMvc.perform(
                            get(url))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse();
            String json = response.getContentAsString();
            List<MenuItemDto> actualMenuItems = objectMapper.readValue(json, new TypeReference<>() {
            });
            List<String> actualMenuItemNames = actualMenuItems.stream().map(MenuItemDto::getName).collect(Collectors.toList());

            assertEquals(2, actualMenuItems.size());
            assertTrue(actualMenuItemNames.containsAll(expectedMenuItemNames));
        }
    }

    @Test
    @DisplayName("Should get a specific menu item for a restaurant")
    @WithUserDetails("john_customer@example.com")
    void getMenuItemByRestaurant() throws Exception {
        long restaurantId = 1L;
        long expectedMenuItemId = 1L;
        String expectedMenuItemName = "Chicago-Style Hot Dog";
        String url = "/api/order/restaurants/" + restaurantId + "/menuitems/" + expectedMenuItemId;

        MockHttpServletResponse response = mockMvc.perform(
                        get(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        String json = response.getContentAsString();

        MenuItemDto actualMenuItem = objectMapper.readValue(json, MenuItemDto.class);

        assertEquals(expectedMenuItemId, actualMenuItem.getId());
        assertEquals(expectedMenuItemName, actualMenuItem.getName());
    }

    @Test
    @DisplayName("Get all orders for logged in customer")
    @WithUserDetails("alice_customer@example.com")
    void getOrders() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/order/past")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        String json = response.getContentAsString();
        List<OrderDtoOut> actualOrders = objectMapper.readValue(json, new TypeReference<>() {
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
        System.out.println(actualOrderId3);
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
    @DisplayName("Should submit an order for logged in user")
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

        MockHttpServletResponse response = mockMvc.perform(
                        post("/api/order/submit")
                                .content(asJsonString(orderDtoIn))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        String json = response.getContentAsString();
        OrderDtoOut actualOrder = objectMapper.readValue(json, OrderDtoOut.class);

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

    String asJsonString(final Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    double calculateTotalPrice(OrderDtoOut order) {
        double total = 0;
        total += order.getTip();
        for (OrderLine orderLine : order.getOrderLines()) {
            total += orderLine.getQuantity() * orderLine.getPriceEach();
        }
        return total;
    }
}
