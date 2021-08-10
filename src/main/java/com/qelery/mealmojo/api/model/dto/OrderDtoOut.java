package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.OrderLine;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class OrderDtoOut {

    private Long id;
    private Double tip;
    private Boolean isCompleted;
    private Boolean isDelivery;
    private PaymentMethod paymentMethod;
    private List<OrderLine> orderLines;
    private String restaurantName;
    private String restaurantLogoImageUrl;
    private String customerProfileFirstName;
    private String customerProfileLastName;
    private Address customerProfileAddress;
}
