package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.OperatingHours;
import lombok.Data;

@Data
public class RestaurantThinDtoOut {

    private String businessName;
    private String description;
    private Boolean pickUpAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Double deliveryFree;
    private String logoImageUrl;
    private String heroImageUrl;
    private Address address;
    private OperatingHours operatingHours;
}
