package com.qelery.mealmojo.api.unitTests.service.utility;

import com.qelery.mealmojo.api.model.dto.*;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.enums.State;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MapperUtilsTest {

    MapperUtils mapperUtils = new MapperUtils();

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

        assertThat(incomingAddressInfo).usingRecursiveComparison()
                .ignoringFields("state", "country").isEqualTo(existingAddressInfo);
        assertThat(existingAddressInfo.getState()).isNotNull();
        assertThat(existingAddressInfo.getCountry()).isNotNull();
    }

    @Test
    @DisplayName("Should map Address entity to AddressDto")
    void mapAddressToAddressDto() {
        AddressDto addressDto = this.mapperUtils.map(addressEntity, AddressDto.class);

        assertThat(addressDto).usingRecursiveComparison().isEqualTo(addressEntity);
    }

    @Test
    @DisplayName("Should have null property mapping disabled for objects in general")
    void shouldDisableNullPropertyMapping() {
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setName("Cheeseburger");
        updatedMenuItem.setPrice(null);

        mapperUtils.map(updatedMenuItem, menuItemEntity);

        assertThat(menuItemEntity.getName()).isEqualTo(updatedMenuItem.getName());
        assertThat(menuItemEntity.getPrice()).isNotNull();
    }

    @Test
    @DisplayName("Should have null property mapping enabled for street2 and street3 fields in the Address object")
    void mapAddressToAddressStreet2And3Null() {
        Address updatedAddress = new Address();
        updatedAddress.setStreet1("5700 S lake Shore Dr");
        updatedAddress.setStreet2(null);
        updatedAddress.setStreet3(null);

        mapperUtils.map(updatedAddress, addressEntity);

        assertThat(addressEntity.getStreet1()).isEqualTo(updatedAddress.getStreet1());
        assertThat(addressEntity.getStreet2()).isNull();
        assertThat(addressEntity.getStreet3()).isNull();
    }

    @Test
    @DisplayName("Should map User entity to UserInfoDto")
    void mapUserToUserInfoDto() {
        UserInfoDto userInfoDto = mapperUtils.map(userEntityWithCustomerProfile, UserInfoDto.class);

        assertThat(userInfoDto.getEmail()).isEqualTo(userEntityWithCustomerProfile.getEmail());
        assertThat(userInfoDto.getFirstName()).isEqualTo(userEntityWithCustomerProfile.getCustomerProfile().getFirstName());
        assertThat(userInfoDto.getLastName()).isEqualTo(userEntityWithCustomerProfile.getCustomerProfile().getLastName());
        Address addressEntity = userEntityWithCustomerProfile.getCustomerProfile().getAddress();
        AddressDto addressDto = userInfoDto.getAddress();
        assertThat(addressDto).usingRecursiveComparison().isEqualTo(addressEntity);
    }

    @Test
    @DisplayName("Should map CustomerProfile and MerchantProfile entities to ProfileDto")
    void mapProfileToProfileDto() {
        ProfileDto profileDto = mapperUtils.map(customerProfileEntity, ProfileDto.class);
        assertThat(profileDto).usingRecursiveComparison().isEqualTo(customerProfileEntity);


        profileDto = mapperUtils.map(merchantProfileEntity, ProfileDto.class);
        assertThat(profileDto).usingRecursiveComparison().isEqualTo(merchantProfileEntity);
    }

    @Test
    @DisplayName("Should map OperatingHours entity to OperatingHoursDto")
    void mapOperatingHoursToOperatingHoursDto() {
        OperatingHoursDto operatingHoursDto = mapperUtils.map(operatingHoursMondayEntity, OperatingHoursDto.class);
        assertThat(operatingHoursDto).usingRecursiveComparison().isEqualTo(operatingHoursMondayEntity);
    }

    @Test
    @DisplayName("Should map MenuItem entity to MenuItemDto")
    void mapMenuItemToMenuItemDto() {
        MenuItemDto menuItemDto = mapperUtils.map(menuItemEntity, MenuItemDto.class);

        assertThat(menuItemDto.getName()).isEqualTo(menuItemEntity.getName());
        assertThat(menuItemDto.getDescription()).isEqualTo(menuItemEntity.getDescription());
        assertThat(menuItemDto.getPrice()).isEqualTo(menuItemEntity.getPrice());
        assertThat(menuItemDto.getImageUrl()).isEqualTo(menuItemEntity.getImageUrl());
        assertThat(menuItemDto.getIsAvailable()).isEqualTo(menuItemEntity.getIsAvailable());
    }

    @Test
    @DisplayName("Should map Restaurant entity to RestaurantDtoOut")
    void mapRestaurantToRestaurantDtoOut() {
        RestaurantDtoOut restaurantDtoOut = mapperUtils.map(restaurantEntity, RestaurantDtoOut.class);

        assertThat(restaurantDtoOut.getName()).isEqualTo(restaurantEntity.getName());
        assertThat(restaurantDtoOut.getDescription()).isEqualTo(restaurantEntity.getDescription());
        assertThat(restaurantDtoOut.getPickUpAvailable()).isEqualTo(restaurantEntity.getPickupAvailable());
        assertThat(restaurantDtoOut.getPickupEtaMinutes()).isEqualTo(restaurantEntity.getPickupEtaMinutes());
        assertThat(restaurantDtoOut.getDeliveryAvailable()).isEqualTo(restaurantEntity.getDeliveryAvailable());
        assertThat(restaurantDtoOut.getDeliveryEtaMinutes()).isEqualTo(restaurantEntity.getDeliveryEtaMinutes());
        assertThat(restaurantDtoOut.getDeliveryFee()).isEqualTo(restaurantEntity.getDeliveryFee());
        assertThat(restaurantDtoOut.getLogoImageUrl()).isEqualTo(restaurantEntity.getLogoImageUrl());
        assertThat(restaurantDtoOut.getHeroImageUrl()).isEqualTo(restaurantEntity.getHeroImageUrl());

        Address entityAddress = restaurantEntity.getAddress();
        AddressDto dtoAddress = restaurantDtoOut.getAddress();
        assertThat(dtoAddress).usingRecursiveComparison().isEqualTo(entityAddress);
    }

    @Test
    @DisplayName("Should map Restaurant entity to RestaurantThinDtoOut")
    void mapRestaurantToRestaurantThinDtoOut() {
        RestaurantThinDtoOut restaurantThinDtoOut = mapperUtils.map(restaurantEntity, RestaurantThinDtoOut.class);
        assertThat(restaurantThinDtoOut.getName()).isEqualTo(restaurantEntity.getName());
        assertThat(restaurantThinDtoOut.getDescription()).isEqualTo(restaurantEntity.getDescription());
        assertThat(restaurantThinDtoOut.getPickUpAvailable()).isEqualTo(restaurantEntity.getPickupAvailable());
        assertThat(restaurantThinDtoOut.getPickupEtaMinutes()).isEqualTo(restaurantEntity.getPickupEtaMinutes());
        assertThat(restaurantThinDtoOut.getDeliveryAvailable()).isEqualTo(restaurantEntity.getDeliveryAvailable());
        assertThat(restaurantThinDtoOut.getDeliveryEtaMinutes()).isEqualTo(restaurantEntity.getDeliveryEtaMinutes());
        assertThat(restaurantThinDtoOut.getDeliveryFee()).isEqualTo(restaurantEntity.getDeliveryFee());
        assertThat(restaurantThinDtoOut.getLogoImageUrl()).isEqualTo(restaurantEntity.getLogoImageUrl());
        assertThat(restaurantThinDtoOut.getHeroImageUrl()).isEqualTo(restaurantEntity.getHeroImageUrl());

        Address entityAddress = restaurantEntity.getAddress();
        AddressDto dtoAddress = restaurantThinDtoOut.getAddress();
        assertThat(dtoAddress).usingRecursiveComparison().isEqualTo(entityAddress);
    }

    @Test
    @DisplayName("Should map Order entity to OrderDtoOut")
    void mapOrderToOrderDtoOut() {
        OrderDto orderDto = mapperUtils.map(orderEntity, OrderDto.class);

        assertThat(orderDto.getTip()).isEqualTo(orderEntity.getTip());
        assertThat(orderDto.getIsCompleted()).isEqualTo(orderEntity.getIsCompleted());
        assertThat(orderDto.getDateTime()).isEqualTo(orderEntity.getDateTime());
        assertThat(orderDto.getIsDelivery()).isEqualTo(orderEntity.getIsDelivery());
        assertThat(orderDto.getDeliveryFee()).isEqualTo(orderEntity.getDeliveryFee());
        assertThat(orderDto.getPaymentMethod()).isEqualTo(orderEntity.getPaymentMethod());
        assertThat(orderDto.getRestaurantId()).isEqualTo(orderEntity.getRestaurant().getRestaurantId());
        assertThat(orderDto.getRestaurantName()).isEqualTo(orderEntity.getRestaurant().getName());
        assertThat(orderDto.getRestaurantLogoImageUrl()).isEqualTo(orderEntity.getRestaurant().getLogoImageUrl());
        assertThat(orderDto.getCustomerProfileFirstName()).isEqualTo(orderEntity.getCustomerProfile().getFirstName());
        assertThat(orderDto.getCustomerProfileLastName()).isEqualTo(orderEntity.getCustomerProfile().getLastName());
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
            assertThat(dto.getDayOfWeek()).isEqualTo(entity.getDayOfWeek());
            assertThat(dto.getOpenTime()).isEqualTo(entity.getOpenTime());
            assertThat(dto.getCloseTime()).isEqualTo(entity.getCloseTime());
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

            assertThat(optionalEntity).isPresent();
            OperatingHours entity = optionalEntity.get();
            assertThat(dto.getDayOfWeek()).isEqualTo(entity.getDayOfWeek());
            assertThat(dto.getOpenTime()).isEqualTo(entity.getOpenTime());
            assertThat(dto.getCloseTime()).isEqualTo(entity.getCloseTime());
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
        userEntityWithCustomerProfile.setEmail("chris@example.com");
        userEntityWithCustomerProfile.setCustomerProfile(customerProfileEntity);

        userEntityWithMerchantProfile = new User();
        userEntityWithMerchantProfile.setRole(Role.MERCHANT);
        userEntityWithMerchantProfile.setEmail("melissa@example.com");
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
        menuItemEntity.setMenuItemId(1L);
        menuItemEntity.setName("Burger");
        menuItemEntity.setPrice(599L);

        restaurantEntity = new Restaurant();
        restaurantEntity.setName("Burger Hut");
        restaurantEntity.setDescription("Description for Burger Hut");
        restaurantEntity.setDeliveryFee(400L);
        restaurantEntity.setDeliveryEtaMinutes(40);
        restaurantEntity.setDeliveryAvailable(true);
        restaurantEntity.setPickupAvailable(true);
        restaurantEntity.setPickupEtaMinutes(20);
        restaurantEntity.setLogoImageUrl("logo.jpg");
        restaurantEntity.setHeroImageUrl("hero.jpg");
        restaurantEntity.setAddress(addressEntity);
        restaurantEntity.setMenuItems(List.of(menuItemEntity));
        restaurantEntity.setMerchantProfile(merchantProfileEntity);
        restaurantEntity.setOperatingHoursList(List.of(operatingHoursMondayEntity));

        orderLineEntity = new OrderLine();
        orderLineEntity.setMenuItem(menuItemEntity);
        orderLineEntity.setQuantity(3);
        orderLineEntity.setPriceEach(799L);

        orderEntity = new Order();
        orderEntity.setTip(400L);
        orderEntity.setIsDelivery(true);

        orderEntity.setRestaurant(restaurantEntity);
        orderEntity.setPaymentMethod(PaymentMethod.CARD);
        orderEntity.setOrderLines(List.of(orderLineEntity));
        orderEntity.setCustomerProfile(customerProfileEntity);
        orderEntity.setDeliveryFee(restaurantEntity.getDeliveryFee());

        restaurantEntity.setOrders(List.of(orderEntity));
    }
}
