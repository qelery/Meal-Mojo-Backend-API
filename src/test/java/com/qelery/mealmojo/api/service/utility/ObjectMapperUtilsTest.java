package com.qelery.mealmojo.api.service.utility;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.enums.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMapperUtilsTest {

    ObjectMapperUtils mapperUtils = new ObjectMapperUtils();

    Address addressEntity;
    CustomerProfile customerProfileEntity;
    MerchantProfile merchantProfileEntity;
    User userEntityWithCustomerProfile;
    User userEntityWithMerchantProfile;
    OperatingHours operatingHoursMondayEntity;
    OperatingHours operatingHoursTuesdayEntity;
    MenuItem menuItemEntity;
    Restaurant restaurantEntity;
    OrderLine orderLineEntity;
    Order orderEntity;

    @BeforeEach
    void setup() {
        initializeEntities();
    }

    @Test
    @DisplayName("Should map non-null properties of source obj to destination obj when both of same class")
    void mapToSameClass() {
        Address existingAddressInfo = addressEntity;

        Address incomingAddressInfo = new Address();
        incomingAddressInfo.setStreet1("Unit 3");
        incomingAddressInfo.setStreet2("Second Floor");
        incomingAddressInfo.setStreet3("123 Maple Lane");
        incomingAddressInfo.setCity("Springfield");
        incomingAddressInfo.setZipcode("62629");
        incomingAddressInfo.setLatitude(39.7817);
        incomingAddressInfo.setLongitude(-89.6501);
        incomingAddressInfo.setState(null);
        incomingAddressInfo.setCountry(null);

        this.mapperUtils.map(incomingAddressInfo, existingAddressInfo);

        assertEquals(incomingAddressInfo.getStreet1(), existingAddressInfo.getStreet1());
        assertEquals(incomingAddressInfo.getStreet2(), existingAddressInfo.getStreet2());
        assertEquals(incomingAddressInfo.getStreet3(), existingAddressInfo.getStreet3());
        assertEquals(incomingAddressInfo.getCity(), existingAddressInfo.getCity());
        assertEquals(incomingAddressInfo.getZipcode(), existingAddressInfo.getZipcode());
        assertEquals(incomingAddressInfo.getLatitude(), existingAddressInfo.getLatitude());
        assertEquals(incomingAddressInfo.getLongitude(), existingAddressInfo.getLongitude());
        assertNotNull(existingAddressInfo.getState());
        assertNotNull(existingAddressInfo.getCountry());
    }

    @Test
    @DisplayName("Should map Address entity to AddressDto")
    void mapAddressToAddressDto() {
        AddressDto addressDto = this.mapperUtils.map(addressEntity, AddressDto.class);

        assertEquals(addressEntity.getStreet1(), addressDto.getStreet1());
        assertEquals(addressEntity.getStreet2(), addressDto.getStreet2());
        assertEquals(addressEntity.getStreet3(), addressDto.getStreet3());
        assertEquals(addressEntity.getCity(), addressDto.getCity());
        assertEquals(addressEntity.getZipcode(), addressDto.getZipcode());
        assertEquals(addressEntity.getState(), addressDto.getState());
        assertEquals(addressEntity.getCountry(), addressDto.getCountry());
        assertEquals(addressEntity.getLatitude(), addressDto.getLatitude());
        assertEquals(addressEntity.getLongitude(), addressDto.getLongitude());
    }

    @Test
    @DisplayName("Should have null property mapping disabled for objects in general")
    void shouldDisableNullPropertyMapping() {
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setName("Cheeseburger");
        updatedMenuItem.setPrice(null);

        mapperUtils.map(updatedMenuItem, menuItemEntity);

        assertEquals(updatedMenuItem.getName(), menuItemEntity.getName());
        assertNotNull(menuItemEntity.getPrice());
    }

    @Test
    @DisplayName("Should have null property mapping enabled for street2 and street3 fields in the Address object")
    void mapAddressToAddressStreet2And3Null() {
        Address updatedAddress = new Address();
        updatedAddress.setStreet1("5700 S lake Shore Dr");
        updatedAddress.setStreet2(null);
        updatedAddress.setStreet3(null);

        mapperUtils.map(updatedAddress, addressEntity);

        assertEquals(updatedAddress.getStreet1(), addressEntity.getStreet1());
        assertNull(addressEntity.getStreet2());
        assertNull(addressEntity.getStreet3());
    }

    @Test
    @DisplayName("Should map User entity to UserDtoOut")
    void mapUserToUserOutDto() {
        UserDtoOut userDtoOut = mapperUtils.map(userEntityWithCustomerProfile, UserDtoOut.class);

        assertEquals(userEntityWithCustomerProfile.getEmail(), userDtoOut.getEmail());
        assertEquals(userEntityWithCustomerProfile.getRole(), userDtoOut.getRole());
        assertEquals(userEntityWithCustomerProfile.getCustomerProfile().getFirstName(), userDtoOut.getFirstName());
        assertEquals(userEntityWithCustomerProfile.getCustomerProfile().getLastName(), userDtoOut.getLastName());
    }

    @Test
    @DisplayName("Should pull first and last name from correct profile when mapping to UserDtoOut")
    void mapNameForUserDtoOut() {
        UserDtoOut userDtoOut = mapperUtils.map(userEntityWithCustomerProfile, UserDtoOut.class);

        assertEquals(userEntityWithCustomerProfile.getCustomerProfile().getFirstName(), userDtoOut.getFirstName());
        assertEquals(userEntityWithCustomerProfile.getCustomerProfile().getLastName(), userDtoOut.getLastName());


        userDtoOut = mapperUtils.map(userEntityWithMerchantProfile, UserDtoOut.class);

        assertEquals(userEntityWithMerchantProfile.getMerchantProfile().getFirstName(), userDtoOut.getFirstName());
        assertEquals(userEntityWithMerchantProfile.getMerchantProfile().getLastName(), userDtoOut.getLastName());


        User userEntityNoProfile = new User();
        userDtoOut = mapperUtils.map(userEntityNoProfile, UserDtoOut.class);

        assertNull(userDtoOut.getFirstName());
        assertNull(userDtoOut.getLastName());
    }

    @Test
    @DisplayName("Should map CustomerProfile and MerchantProfile entities to ProfileDto")
    void mapProfileToProfileDto() {
        ProfileDto profileDto = mapperUtils.map(customerProfileEntity, ProfileDto.class);

        assertEquals(customerProfileEntity.getFirstName(), profileDto.getFirstName());
        assertEquals(customerProfileEntity.getLastName(), profileDto.getLastName());
        assertEquals(customerProfileEntity.getAddress(), profileDto.getAddress());


        profileDto = mapperUtils.map(merchantProfileEntity, ProfileDto.class);

        assertEquals(merchantProfileEntity.getFirstName(), profileDto.getFirstName());
        assertEquals(merchantProfileEntity.getLastName(), profileDto.getLastName());
        assertEquals(merchantProfileEntity.getAddress(), profileDto.getAddress());
    }

    @Test
    @DisplayName("Should map OperatingHours entity to OperatingHoursDto")
    void mapOperatingHoursToOperatingHoursDto() {
        OperatingHoursDto operatingHoursDto = mapperUtils.map(operatingHoursMondayEntity, OperatingHoursDto.class);

        assertEquals(operatingHoursMondayEntity.getOpenTime(), operatingHoursDto.getOpenTime());
        assertEquals(operatingHoursMondayEntity.getCloseTime(), operatingHoursDto.getCloseTime());
        assertEquals(operatingHoursMondayEntity.getDayOfWeek(), operatingHoursDto.getDayOfWeek());
    }

    @Test
    @DisplayName("Should map MenuItem entity to MenuItemDto")
    void mapMenuItemToMenuItemDto() {
        MenuItemDto menuItemDto = mapperUtils.map(menuItemEntity, MenuItemDto.class);

        assertEquals(menuItemEntity.getName(), menuItemDto.getName());
        assertEquals(menuItemEntity.getDescription(), menuItemDto.getDescription());
        assertEquals(menuItemEntity.getPrice(), menuItemDto.getPrice());
        assertEquals(menuItemEntity.getImageUrl(), menuItemDto.getImageUrl());
        assertEquals(menuItemEntity.getIsAvailable(), menuItemDto.getIsAvailable());
    }

    @Test
    @DisplayName("Should map Restaurant entity to RestaurantDtoOut")
    void mapRestaurantToRestaurantDtoOut() {
        RestaurantDtoOut restaurantDtoOut = mapperUtils.map(restaurantEntity, RestaurantDtoOut.class);

        assertEquals(restaurantEntity.getName(), restaurantDtoOut.getName());
        assertEquals(restaurantEntity.getDescription(), restaurantDtoOut.getDescription());
        assertEquals(restaurantEntity.getPickupAvailable(), restaurantDtoOut.getPickUpAvailable());
        assertEquals(restaurantEntity.getPickupEtaMinutes(), restaurantDtoOut.getPickupEtaMinutes());
        assertEquals(restaurantEntity.getDeliveryAvailable(), restaurantDtoOut.getDeliveryAvailable());
        assertEquals(restaurantEntity.getDeliveryEtaMinutes(), restaurantDtoOut.getDeliveryEtaMinutes());
        assertEquals(restaurantEntity.getDeliveryFee(), restaurantDtoOut.getDeliveryFee());
        assertEquals(restaurantEntity.getLogoImageUrl(), restaurantDtoOut.getLogoImageUrl());
        assertEquals(restaurantEntity.getHeroImageUrl(), restaurantDtoOut.getHeroImageUrl());
        assertEquals(restaurantEntity.getAddress(), restaurantDtoOut.getAddress());
        assertEquals(restaurantEntity.getOperatingHoursList(), restaurantDtoOut.getOperatingHoursList());
        assertEquals(restaurantEntity.getMenuItems(), restaurantDtoOut.getMenuItems());
        assertEquals(restaurantEntity.getOrders(), restaurantDtoOut.getOrders());
    }

    @Test
    @DisplayName("Should map Restaurant entity to RestaurantThinDtoOut")
    void mapRestaurantToRestaurantThinDtoOut() {
        RestaurantThinDtoOut restaurantThinDtoOut = mapperUtils.map(restaurantEntity, RestaurantThinDtoOut.class);

        assertEquals(restaurantEntity.getName(), restaurantThinDtoOut.getName());
        assertEquals(restaurantEntity.getDescription(), restaurantThinDtoOut.getDescription());
        assertEquals(restaurantEntity.getPickupAvailable(), restaurantThinDtoOut.getPickUpAvailable());
        assertEquals(restaurantEntity.getPickupEtaMinutes(), restaurantThinDtoOut.getPickupEtaMinutes());
        assertEquals(restaurantEntity.getDeliveryAvailable(), restaurantThinDtoOut.getDeliveryAvailable());
        assertEquals(restaurantEntity.getDeliveryEtaMinutes(), restaurantThinDtoOut.getDeliveryEtaMinutes());
        assertEquals(restaurantEntity.getDeliveryFee(), restaurantThinDtoOut.getDeliveryFee());
        assertEquals(restaurantEntity.getLogoImageUrl(), restaurantThinDtoOut.getLogoImageUrl());
        assertEquals(restaurantEntity.getHeroImageUrl(), restaurantThinDtoOut.getHeroImageUrl());
        assertEquals(restaurantEntity.getAddress(), restaurantThinDtoOut.getAddress());
        assertEquals(restaurantEntity.getOperatingHoursList(), restaurantThinDtoOut.getOperatingHoursList());
    }

    @Test
    @DisplayName("Should map Order entity to OrderDtoOut")
    void mapOrderToOrderDtoOut() {
        OrderDtoOut orderDtoOut = mapperUtils.map(orderEntity, OrderDtoOut.class);

        assertEquals(orderEntity.getTip(), orderDtoOut.getTip());
        assertEquals(orderEntity.getIsCompleted(), orderDtoOut.getIsCompleted());
        assertEquals(orderEntity.getIsDelivery(), orderDtoOut.getIsDelivery());
        assertEquals(orderEntity.getPaymentMethod(), orderDtoOut.getPaymentMethod());
        assertEquals(orderEntity.getRestaurant().getName(), orderDtoOut.getRestaurantName());
        assertEquals(orderEntity.getRestaurant().getLogoImageUrl(), orderDtoOut.getRestaurantLogoImageUrl());
        assertEquals(orderEntity.getCustomerProfile().getFirstName(), orderDtoOut.getCustomerProfileFirstName());
        assertEquals(orderEntity.getCustomerProfile().getLastName(), orderDtoOut.getCustomerProfileLastName());
    }

    @Test
    @DisplayName("Should map a list of an entity to a list of a dto class")
    void mapEntityListToDtoList() {
        List<OperatingHours> operatingHoursEntityList = List.of(operatingHoursMondayEntity, operatingHoursTuesdayEntity);
        List<OperatingHoursDto> operatingHoursDtoList = this.mapperUtils.mapAll(
                operatingHoursEntityList,
                OperatingHoursDto.class
        );

        for (int i = 0; i < operatingHoursDtoList.size(); i++) {
            OperatingHours entity = operatingHoursEntityList.get(i);
            OperatingHoursDto dto = operatingHoursDtoList.get(i);
            assertEquals(entity.getDayOfWeek(), dto.getDayOfWeek());
            assertEquals(entity.getOpenTime(), dto.getOpenTime());
            assertEquals(entity.getCloseTime(), dto.getCloseTime());
        }
    }

    @Test
    @DisplayName("Should map a set of an entity to a set of a dto class")
    void mapEntitySetToDtoSet() {
        Set<OperatingHours> operatingHoursEntitySet = Set.of(operatingHoursMondayEntity, operatingHoursTuesdayEntity);
        Set<OperatingHoursDto> operatingHoursDtoSet = this.mapperUtils.mapAll(
                operatingHoursEntitySet,
                OperatingHoursDto.class
        );

        for (OperatingHoursDto dto: operatingHoursDtoSet) {
            Optional<OperatingHours> optionalEntity = operatingHoursEntitySet.stream()
                    .filter(e -> e.getDayOfWeek().equals(dto.getDayOfWeek()))
                    .findFirst();

            assertTrue(optionalEntity.isPresent());
            OperatingHours entity = optionalEntity.get();
            assertEquals(entity.getDayOfWeek(), dto.getDayOfWeek());
            assertEquals(entity.getOpenTime(), dto.getOpenTime());
            assertEquals(entity.getCloseTime(), dto.getCloseTime());
        }
    }

    private void initializeEntities() {
        addressEntity = new Address();
        addressEntity.setStreet1("1 " +
                "400 S Lake Shore Dr");
        addressEntity.setStreet2("Field Museum");
        addressEntity.setStreet3("Office 3B");
        addressEntity.setCity("Chicago");
        addressEntity.setState(State.IL);
        addressEntity.setZipcode("60605");
        addressEntity.setCountry(Country.US);
        addressEntity.setLatitude(41.866265);
        addressEntity.setLongitude(-87.6191692);

        customerProfileEntity = new CustomerProfile();
        customerProfileEntity.setFirstName("Chris");
        customerProfileEntity.setLastName("Customer");
        customerProfileEntity.setAddress(addressEntity);

        merchantProfileEntity = new MerchantProfile();
        merchantProfileEntity.setFirstName("Melissa");
        merchantProfileEntity.setLastName("Merchant");
        merchantProfileEntity.setAddress(addressEntity);

        userEntityWithCustomerProfile = new User();
        userEntityWithCustomerProfile.setRole(Role.CUSTOMER);
        userEntityWithCustomerProfile.setEmail("chris@example.org");
        userEntityWithCustomerProfile.setCustomerProfile(customerProfileEntity);

        userEntityWithMerchantProfile = new User();
        userEntityWithMerchantProfile.setRole(Role.MERCHANT);
        userEntityWithMerchantProfile.setEmail("melissa@example.org");
        userEntityWithMerchantProfile.setMerchantProfile(merchantProfileEntity);

        operatingHoursMondayEntity = new OperatingHours();
        operatingHoursMondayEntity.setDayOfWeek(DayOfWeek.MONDAY);
        operatingHoursMondayEntity.setOpenTime(LocalTime.of(8, 0));
        operatingHoursMondayEntity.setCloseTime(LocalTime.of(20, 0));

        operatingHoursTuesdayEntity = new OperatingHours();
        operatingHoursTuesdayEntity.setDayOfWeek(DayOfWeek.TUESDAY);
        operatingHoursTuesdayEntity.setOpenTime(LocalTime.of(9, 30));
        operatingHoursTuesdayEntity.setCloseTime(LocalTime.of(21, 30));

        menuItemEntity = new MenuItem();
        menuItemEntity.setId(1L);
        menuItemEntity.setName("Burger");
        menuItemEntity.setPrice(5.99);

        restaurantEntity = new Restaurant();
        restaurantEntity.setName("Burger Hut");
        restaurantEntity.setDescription("Description for Burger Hut");
        restaurantEntity.setPickupAvailable(true);
        restaurantEntity.setPickupEtaMinutes(20);
        restaurantEntity.setDeliveryAvailable(true);
        restaurantEntity.setDeliveryEtaMinutes(40);
        restaurantEntity.setLogoImageUrl("logo.jpg");
        restaurantEntity.setHeroImageUrl("hero.jpg");
        restaurantEntity.setAddress(addressEntity);
        restaurantEntity.setMerchantProfile(merchantProfileEntity);
        restaurantEntity.setOperatingHoursList(List.of(operatingHoursMondayEntity));
        restaurantEntity.setMenuItems(List.of(menuItemEntity));

        orderLineEntity = new OrderLine();
        orderLineEntity.setMenuItem(menuItemEntity);
        orderLineEntity.setQuantity(3);
        orderLineEntity.setPriceEach(7.99);

        orderEntity = new Order();
        orderEntity.setTip(4.00);
        orderEntity.setIsDelivery(true);
        orderEntity.setPaymentMethod(PaymentMethod.CARD);
        orderEntity.setOrderLines(List.of(orderLineEntity));
        orderEntity.setRestaurant(restaurantEntity);
        orderEntity.setCustomerProfile(customerProfileEntity);

        restaurantEntity.setOrders(List.of(orderEntity));
    }
}
