package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RestaurantDtoOut {

    private Long restaurantId;
    private String name;
    private String description;
    private Boolean pickUpAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Long deliveryFee; // cents
    private String logoImageUrl;
    private String heroImageUrl;
    private AddressDto address;
    private List<OperatingHoursDto> operatingHoursList;
    private List<MenuItemDto> menuItems;
    private List<OrderDto> orders;
    private Set<CuisineDto> cuisines;
}
