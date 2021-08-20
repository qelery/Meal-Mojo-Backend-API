package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantThinDtoOut {

    private Long id;
    private String name;
    private String description;
    private Boolean pickUpAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Double deliveryFee;
    private String logoImageUrl;
    private String heroImageUrl;
    private AddressDto address;
    private List<OperatingHoursDto> operatingHoursList;
}
