package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.OperatingHours;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantThinDtoOut {

    private String name;
    private String description;
    private Boolean pickUpAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Double deliveryFee;
    private String logoImageUrl;
    private String heroImageUrl;
    private Address address;
    private List<OperatingHours> operatingHoursList;
}
