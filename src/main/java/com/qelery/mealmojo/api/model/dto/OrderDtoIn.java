package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Map;

@Data
public class OrderDtoIn {

    private Double tip;
    private Boolean isDelivery;
    private PaymentMethod paymentMethod;
    private Map<Long, Integer> menuItemQuantitiesMap;
}
