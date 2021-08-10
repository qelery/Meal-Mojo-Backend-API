package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.MenuItem;
import com.qelery.mealmojo.api.model.entity.OperatingHours;
import com.qelery.mealmojo.api.model.entity.Order;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantDtoOut {

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
    private Address address;
    private List<OperatingHours> operatingHoursList;
    private List<MenuItem> menuItems;
    private List<Order> orders;
}
