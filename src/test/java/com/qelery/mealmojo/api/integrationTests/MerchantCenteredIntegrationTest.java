package com.qelery.mealmojo.api.integrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
import com.qelery.mealmojo.api.exception.OrderNotFoundException;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.OperatingHours;
import com.qelery.mealmojo.api.model.entity.Order;
import com.qelery.mealmojo.api.model.entity.Restaurant;
import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.State;
import com.qelery.mealmojo.api.repository.AddressRepository;
import com.qelery.mealmojo.api.repository.OrderRepository;
import com.qelery.mealmojo.api.repository.RestaurantRepository;
import com.qelery.mealmojo.api.testUtils.HttpRequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.qelery.mealmojo.api.testUtils.CustomAssertions.assertContainsErrorMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(scripts = {"/seed/load-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/seed/clear-test-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MerchantCenteredIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HttpRequestDispatcher httpRequestDispatcher;

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private OrderRepository orderRepository;

    // Expected values are based on the test data loaded into the test H2
    // database using the sql scripts in the directory /test/resources/seed
    @Nested
    @DisplayName("[Integration Tests - Through All Layers] With merchant logged in,")
    class throughAllLayers_merchant {

        @Test
        @DisplayName("Should get all restaurants he/she owns")
        @WithUserDetails(value = "rebecca_merchant@example.com")
        void getAllRestaurantsOwned_merchant() throws Exception {
            List<String> expectedRestaurantNames = List.of("Portillo's Hot Dogs", "Joy Yee", "Daley's Restaurant");

            String url = "/api/restaurants/by-logged-in-merchant";
            String jsonResponse = httpRequestDispatcher.performGET(url);

            List<RestaurantThinDtoOut> actualRestaurantDtos = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });
            List<String> actualRestaurantNames = actualRestaurantDtos.stream()
                    .map(RestaurantThinDtoOut::getName)
                    .collect(Collectors.toList());

            assertEquals(3, actualRestaurantDtos.size());
            assertTrue(expectedRestaurantNames.containsAll(actualRestaurantNames));
        }

        @Test
        @DisplayName("Should get a restaurant he/she owns")
        @WithUserDetails("sam_merchant@example.com")
        void getSingleRestaurantOwned_merchant() throws Exception {
            long expectedRestaurantId = 2;
            String expectedRestaurantName = "Pizano's Pizza & Pasta";

            String url = "/api/restaurants/" + expectedRestaurantId + "/by-logged-in-merchant";
            String jsonResponse = httpRequestDispatcher.performGET(url);
            RestaurantDtoOut actualRestaurant = objectMapper.readValue(jsonResponse, RestaurantDtoOut.class);

            assertEquals(expectedRestaurantId, actualRestaurant.getRestaurantId());
            assertEquals(expectedRestaurantName, actualRestaurant.getName());
        }

        @Test
        @DisplayName("Should create a restaurant under his/her profile")
        @WithUserDetails("rebecca_merchant@example.com")
        void createRestaurant_merchant() throws Exception {
            AddressDto addressDto = new AddressDto();
            addressDto.setStreet1("4300 N Lincoln Ave");
            addressDto.setCity("Chicago");
            addressDto.setState(State.IL);
            addressDto.setZipcode("60618");
            addressDto.setCountry(Country.US);

            RestaurantDtoIn restaurantDtoIn = new RestaurantDtoIn();
            restaurantDtoIn.setName("The Bad Apple");
            restaurantDtoIn.setDescription("Laid-back bar & restaurant with creative burgers and an extensive craft beer selection.");
            restaurantDtoIn.setPickUpAvailable(true);
            restaurantDtoIn.setPickupEtaMinutes(30);
            restaurantDtoIn.setDeliveryAvailable(true);
            restaurantDtoIn.setDeliveryEtaMinutes(40);
            restaurantDtoIn.setDeliveryFee(600L);
            restaurantDtoIn.setAddress(addressDto);

            String url = "/api/restaurants";
            String jsonResponse = httpRequestDispatcher.performPOST(url, restaurantDtoIn);
            RestaurantDtoOut actualRestaurantDtoOut = objectMapper.readValue(jsonResponse, RestaurantDtoOut.class);

            // Assert that the DTO response from the controller has the expected fields
            assertEquals(restaurantDtoIn.getName(), actualRestaurantDtoOut.getName());
            assertEquals(restaurantDtoIn.getDescription(), actualRestaurantDtoOut.getDescription());
            assertEquals(restaurantDtoIn.getPickUpAvailable(), actualRestaurantDtoOut.getPickUpAvailable());
            assertEquals(restaurantDtoIn.getPickupEtaMinutes(), actualRestaurantDtoOut.getPickupEtaMinutes());
            assertEquals(restaurantDtoIn.getDeliveryAvailable(), actualRestaurantDtoOut.getDeliveryAvailable());
            assertEquals(restaurantDtoIn.getDeliveryFee(), actualRestaurantDtoOut.getDeliveryFee());

            // Assert that the Restaurant saved to the database has the expected fields
            Optional<Restaurant> optionalRestaurant = restaurantRepository.findAll()
                    .stream()
                    .filter(restaurant -> restaurant.getName().equals(restaurantDtoIn.getName()))
                    .findFirst();
            assert (optionalRestaurant.isPresent());
            Restaurant restaurantSavedToDatabase = optionalRestaurant.get();
            assertTrue(restaurantSavedToDatabase.getIsActive());
            assertEquals(restaurantDtoIn.getName(), restaurantSavedToDatabase.getName());

            // Assert that the Restaurant's address was saved to the database
            Optional<Address> addressSavedToDatabase = addressRepository.findAll()
                    .stream()
                    .filter(address -> address.getStreet1().equals(addressDto.getStreet1()))
                    .findFirst();
            assertTrue(addressSavedToDatabase.isPresent());
            assertEquals(addressSavedToDatabase.get(), restaurantSavedToDatabase.getAddress());
        }

        @Test
        @DisplayName("Should update the basic info for a restaurant he/she owns")
        @WithUserDetails("rebecca_merchant@example.com")
        void updateRestaurantBasicInfo_merchant() throws Exception {
            long restaurantId = 3L;
            RestaurantDtoIn restaurantDtoIn = new RestaurantDtoIn();
            restaurantDtoIn.setName("Joy Yee of Tinley Park");
            restaurantDtoIn.setLogoImageUrl("new_joy_yee_logo.jpg");

            String url = "/api/restaurants/" + restaurantId;
            String jsonResponse = httpRequestDispatcher.performPATCH(url, restaurantDtoIn);

            RestaurantThinDtoOut actualRestaurantDtoOut = objectMapper.readValue(jsonResponse, RestaurantThinDtoOut.class);

            // Assert that the DTO response from the controller has the expected fields
            assertEquals(restaurantDtoIn.getName(), actualRestaurantDtoOut.getName());
            assertEquals(restaurantDtoIn.getName(), actualRestaurantDtoOut.getName());

            // Assert that the Restaurant in the database has the updated fields
            Optional<Restaurant> optionalRestaurant = restaurantRepository.findAll()
                    .stream()
                    .filter(restaurant -> restaurant.getName().equals(restaurantDtoIn.getName()))
                    .findFirst();
            assertTrue(optionalRestaurant.isPresent());
            Restaurant restaurantFromDatabase = optionalRestaurant.get();
            assertEquals(restaurantDtoIn.getName(), restaurantFromDatabase.getName());
        }

        @Test
        @DisplayName("Should update hours for a restaurant he/she owns")
        @WithUserDetails("sam_merchant@example.com")
        void updateRestaurantHours_merchant() throws Exception {
            long restaurantId = 2L;
            OperatingHoursDto expectedNewTuesdayHoursDto = new OperatingHoursDto();
            expectedNewTuesdayHoursDto.setDayOfWeek(DayOfWeek.TUESDAY);
            expectedNewTuesdayHoursDto.setOpenTime(LocalTime.of(7, 0));
            expectedNewTuesdayHoursDto.setCloseTime(LocalTime.of(22, 0));
            List<OperatingHoursDto> updatedHoursDto = List.of(expectedNewTuesdayHoursDto);

            String url = "/api/restaurants/" + restaurantId + "/hours";
            String jsonResponse = httpRequestDispatcher.performPATCH(url, updatedHoursDto);


            RestaurantThinDtoOut actualRestaurantDtoOut = objectMapper.readValue(jsonResponse, RestaurantThinDtoOut.class);
            List<OperatingHoursDto> actualDtoResponseHours = actualRestaurantDtoOut.getOperatingHoursList();

            // Assert that the DTO response from the controller has the expected fields
            assertTrue(actualDtoResponseHours.containsAll(updatedHoursDto));

            // Assert that the Restaurant in the database has the updated hours
            Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(2L);
            assertTrue(optionalRestaurant.isPresent());
            List<OperatingHours> restaurantFromDatabasesHours = optionalRestaurant.get().getOperatingHoursList();
            Optional<OperatingHours> optionalTuesdayHours = restaurantFromDatabasesHours.stream()
                    .filter(hours -> hours.getDayOfWeek() == DayOfWeek.TUESDAY)
                    .findFirst();
            assertTrue(optionalTuesdayHours.isPresent());
            OperatingHours savedTuesdayHours = optionalTuesdayHours.get();
            assertEquals(expectedNewTuesdayHoursDto.getOpenTime(), savedTuesdayHours.getOpenTime());
            assertEquals(expectedNewTuesdayHoursDto.getCloseTime(), savedTuesdayHours.getCloseTime());
        }

        @Test
        @DisplayName("Should update address for a restaurant he/she owns")
        @WithUserDetails("rebecca_merchant@example.com")
        void updateRestaurantAddress_merchant() throws Exception {
            long restaurantId = 3L;

            Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
            assertTrue(restaurantOptional.isPresent());
            Address addressBeforeUpdate = restaurantOptional.get().getAddress();

            AddressDto updatedAddressDtoIn = new AddressDto();
            updatedAddressDtoIn.setStreet1("4300 N Lincoln Ave");
            updatedAddressDtoIn.setCity("Chicago");
            updatedAddressDtoIn.setState(State.IL);
            updatedAddressDtoIn.setZipcode("60618");
            updatedAddressDtoIn.setCountry(Country.US);


            String url = "/api/restaurants/" + restaurantId + "/address";
            String jsonResponse = httpRequestDispatcher.performPATCH(url, updatedAddressDtoIn);
            RestaurantThinDtoOut actualRestaurantDtoOut = objectMapper.readValue(jsonResponse, RestaurantThinDtoOut.class);
            AddressDto actualAddressDtoOut = actualRestaurantDtoOut.getAddress();


            assertEquals(actualAddressDtoOut.getStreet1(), updatedAddressDtoIn.getStreet1());
            assertEquals(actualAddressDtoOut.getCity(), updatedAddressDtoIn.getCity());
            assertEquals(actualAddressDtoOut.getState(), updatedAddressDtoIn.getState());
            assertEquals(actualAddressDtoOut.getZipcode(), updatedAddressDtoIn.getZipcode());
            assertEquals(actualAddressDtoOut.getCountry(), updatedAddressDtoIn.getCountry());
            assertEquals(actualAddressDtoOut.getLatitude(), addressBeforeUpdate.getLatitude());
            assertEquals(actualAddressDtoOut.getLongitude(), addressBeforeUpdate.getLongitude());
        }

        @Test
        @DisplayName("Should get all orders for all restaurants owned")
        @WithUserDetails("rebecca_merchant@example.com")
        void getAllOrdersAllOwnedRestaurants_merchant() throws Exception {
            String url = "/api/orders";
            String jsonResponse = httpRequestDispatcher.performGET(url);

            List<OrderDto> actualOrderDtos = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

            assertEquals(3, actualOrderDtos.size());
            List<Long> actualOrderIds =  actualOrderDtos.stream()
                    .map(OrderDto::getOrderId)
                    .collect(Collectors.toList());
            assertTrue(actualOrderIds.containsAll(List.of(1L, 3L, 4L)));
        }

        @Test
        @DisplayName("Should get all orders for a particular restaurant owned by merchant")
        @WithUserDetails("rebecca_merchant@example.com")
        void getAllOrdersForAnOwnedRestaurant() throws Exception {
            long restaurantId = 4L;
            String url = "/api/orders?restaurant-id=" + restaurantId;
            String jsonResponse = httpRequestDispatcher.performGET(url);

            List<OrderDto> actualOrderDtos = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

            assertEquals(1, actualOrderDtos.size());
            OrderDto actualOder = actualOrderDtos.get(0);
            assertEquals(3L, actualOder.getOrderId());
        }

        @Test
        @DisplayName("Should get order by id if order belongs to restaurant merchant owns")
        @WithUserDetails("sam_merchant@example.com")
        void getSingleOrderForOwnedRestaurant_merchant() throws Exception {
            long orderId = 2L;
            String url = "/api/orders/" + orderId;
            String jsonResponse = httpRequestDispatcher.performGET(url);

            OrderDto actualOrderDto = objectMapper.readValue(jsonResponse, OrderDto.class);

            assertEquals(orderId, actualOrderDto.getOrderId());
        }

        @Test
        @DisplayName("Should mark an order complete")
        @WithUserDetails("sam_merchant@example.com")
        void markOrderComplete_merchant() throws Exception {
            long orderId = 2L;
            String url = "/api/orders/" + orderId + "/complete";
            String jsonResponse = httpRequestDispatcher.performPATCH(url, 200);

            OrderDto actualOrderDto = objectMapper.readValue(jsonResponse, OrderDto.class);

            assertTrue(actualOrderDto.getIsCompleted());
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            assertTrue(optionalOrder.isPresent());
            Order orderFromDatabase = optionalOrder.get();
            assertTrue(orderFromDatabase.getIsCompleted());
        }


        @Nested
        @DisplayName("And merchant has supplied non-existent entity id,")
        class throughAllLayers_merchant_nonExistentEntity {

            @Test
            @WithUserDetails("sam_merchant@example.com")
            @DisplayName("Should get 404 response when trying to get or update restaurant that doesn't exist")
            void should404OnNonExistentRestaurant_merchant() throws Exception {
                long restaurantIdThatDoesNotExist = 3001L;
                String expectedErrorMessage = new RestaurantNotFoundException(3001L).getMessage();

                String url;
                String jsonResponse;

                url = "/api/restaurants/" + restaurantIdThatDoesNotExist;
                jsonResponse = httpRequestDispatcher.performGET(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                url = "/api/restaurants/" + restaurantIdThatDoesNotExist;
                jsonResponse = httpRequestDispatcher.performGET(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                url = "/api/orders?restaurant-id=" + restaurantIdThatDoesNotExist;
                jsonResponse = httpRequestDispatcher.performGET(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                RestaurantDtoIn updatedRestaurantInfoDto = new RestaurantDtoIn();
                updatedRestaurantInfoDto.setName("New Restaurant Name");
                url = "/api/restaurants/" + restaurantIdThatDoesNotExist;
                jsonResponse = httpRequestDispatcher.performPATCH(url, updatedRestaurantInfoDto, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                OperatingHoursDto updatedTuesdayHours = new OperatingHoursDto();
                updatedTuesdayHours.setDayOfWeek(DayOfWeek.TUESDAY);
                updatedTuesdayHours.setOpenTime(LocalTime.of(8, 0));
                updatedTuesdayHours.setCloseTime(LocalTime.of(20, 0));
                List<OperatingHoursDto> updatedHoursDto = List.of(updatedTuesdayHours);
                url = "/api/restaurants/" + restaurantIdThatDoesNotExist + "/hours";
                jsonResponse = httpRequestDispatcher.performPATCH(url, updatedHoursDto, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                AddressDto updatedAddressDto = new AddressDto();
                updatedAddressDto.setStreet1("123 New Street");
                url = "/api/restaurants/" + restaurantIdThatDoesNotExist + "/address";
                jsonResponse = httpRequestDispatcher.performPATCH(url, updatedAddressDto, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                MenuItemDto menuItemDto = new MenuItemDto();
                menuItemDto.setName("Name");
                menuItemDto.setPrice(1000L);
                url = "/api/restaurants/" + restaurantIdThatDoesNotExist + "/menuitems";
                jsonResponse = httpRequestDispatcher.performPOST(url, menuItemDto, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
            }

            @Test
            @WithUserDetails("rebecca_merchant@example.com")
            @DisplayName("Should get 404 response when trying to update menu item that doesn't exist")
            void should404OnNonExistentMenuItem_merchant() throws Exception {
                long restaurantId = 1L;
                long menuItemIdThatDoesNotExist = 5001L;
                String expectedErrorMessage = new MenuItemNotFoundException(5001L).getMessage();

                MenuItemDto menuItemDto = new MenuItemDto();
                menuItemDto.setName("Updated Name");
                menuItemDto.setPrice(750L);
                String url = "/api/restaurants/" + restaurantId + "/menuitems/" + menuItemIdThatDoesNotExist;
                String jsonResponse = httpRequestDispatcher.performPUT(url, menuItemDto, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
            }

            @Test
            @WithUserDetails("rebecca_merchant@example.com")
            @DisplayName("Should get 404 response when trying to get or update order that doesn't exist")
            void should404OnNonExistentOrder() throws Exception {
                long orderIdThatDoesNotExist = 4815162342L;
                String expectedErrorMessage = new OrderNotFoundException(4815162342L).getMessage();

                String url;
                String jsonResponse;

                url = "/api/orders/" + orderIdThatDoesNotExist;
                jsonResponse = httpRequestDispatcher.performGET(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);

                url = "/api/orders/" + orderIdThatDoesNotExist + "/complete";
                jsonResponse = httpRequestDispatcher.performPATCH(url, 404);
                assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
            }
        }
    }

    @Nested
    @DisplayName("[Integration Tests - Through All Layers] When logged in as Merchant A,")
    class throughAllLayers_wrongMerchant {

        @Test
        @WithUserDetails("rebecca_merchant@example.com")
        @DisplayName("Should NOT be able to retrieve info about Merchant B's restaurants")
        void shouldNotRetrieveUnownedRestaurantInfo_wrongMerchant() throws Exception {
            long someoneElsesRestaurantId = 2L;
            String expectedErrorMessage = new RestaurantNotFoundException(2L).getMessage();

            String url;
            String jsonResponse;


            url = "/api/restaurants/" + someoneElsesRestaurantId + "/by-logged-in-merchant";
            jsonResponse = httpRequestDispatcher.performGET(url, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);


            url = "/api/orders?restaurant-id=" + someoneElsesRestaurantId;
            jsonResponse = httpRequestDispatcher.performGET(url, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);


            long orderIdFromRestaurantNotOwned = 2L;
            expectedErrorMessage = new OrderNotFoundException(2L).getMessage();
            url = "/api/orders/" + orderIdFromRestaurantNotOwned;
            jsonResponse = httpRequestDispatcher.performGET(url, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
        }

        @Test
        @WithUserDetails("rebecca_merchant@example.com")
        @DisplayName("Should NOT be able to create entities for Merchant B's restaurants")
        void shouldNotCreateEntitiesForUnownedRestaurants_wrongMerchant() throws Exception {
            long someoneElsesRestaurantId = 2L;
            MenuItemDto menuItemDto = new MenuItemDto();
            menuItemDto.setName("name");
            menuItemDto.setPrice(1000L);
            String expectedErrorMessage = new RestaurantNotFoundException(2L).getMessage();

            String url = "/api/restaurants/" + someoneElsesRestaurantId + "/menuitems";
            String jsonResponse = httpRequestDispatcher.performPOST(url, menuItemDto, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
        }

        @Test
        @WithUserDetails("rebecca_merchant@example.com")
        @DisplayName("Should NOT be able to update information for Merchant B's restaurants")
        void shouldNotUpdateUnownedRestaurantInfo_wrongMerchant() throws Exception {
            long someoneElsesRestaurantId = 2L;
            long menuItemIdFromRestaurantNotOwned = 4L;
            String expectedErrorMessage = new RestaurantNotFoundException(2L).getMessage();

            String url;
            String jsonResponse;


            RestaurantDtoIn updatedBasicRestaurantInfoDto = new RestaurantDtoIn();
            updatedBasicRestaurantInfoDto.setName("New Name");
            url = "/api/restaurants/" + someoneElsesRestaurantId;
            jsonResponse = httpRequestDispatcher.performPATCH(url, updatedBasicRestaurantInfoDto, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);


            OperatingHoursDto newTuesdayHours = new OperatingHoursDto();
            newTuesdayHours.setDayOfWeek(DayOfWeek.TUESDAY);
            newTuesdayHours.setOpenTime(LocalTime.of(8, 0));
            newTuesdayHours.setCloseTime(LocalTime.of(20, 0));
            List<OperatingHoursDto> updateHoursDto = List.of(newTuesdayHours);
            url = "/api/restaurants/" + someoneElsesRestaurantId + "/hours";
            jsonResponse = httpRequestDispatcher.performPATCH(url, updateHoursDto, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);


            AddressDto addressDto = new AddressDto();
            addressDto.setStreet1("New Street 1");
            url = "/api/restaurants/" + someoneElsesRestaurantId + "/address";
            jsonResponse = httpRequestDispatcher.performPATCH(url, addressDto, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);


            MenuItemDto menuItemDto = new MenuItemDto();
            menuItemDto.setName("name");
            menuItemDto.setPrice(1000L);
            url = "/api/restaurants/" + someoneElsesRestaurantId + "/menuitems/" + menuItemIdFromRestaurantNotOwned;
            jsonResponse = httpRequestDispatcher.performPUT(url, menuItemDto, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);


            long orderIdFromRestaurantNotOwned = 2L;
            expectedErrorMessage = new OrderNotFoundException(2L).getMessage();
            url = "/api/orders/" + orderIdFromRestaurantNotOwned + "/complete";
            jsonResponse = httpRequestDispatcher.performPATCH(url, addressDto, 404);
            assertContainsErrorMessage(jsonResponse, expectedErrorMessage);
        }
    }
}
