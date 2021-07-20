package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

@Data
public class RestaurantThinDtoIn {

    private String businessName;
    private String description;
    private Boolean pickUpAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Double deliveryFree;
    private String logoImageUrl;
    private String heroImageUrl;
}
