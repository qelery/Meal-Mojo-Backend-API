package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

@Data
public class RestaurantDtoIn {

    private String name;
    private String description;
    private Boolean pickUpAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Double deliveryFee;
    private String logoImageUrl;
    private String heroImageUrl;
}
