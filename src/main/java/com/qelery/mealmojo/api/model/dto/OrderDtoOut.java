package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.OrderLine;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class OrderDtoOut {

    private Long id;
    private Long tip; //cents
    private OffsetDateTime dateTime;
    private Boolean isCompleted;
    private Boolean isDelivery;
    private Long deliveryFee; // cents
    private PaymentMethod paymentMethod;
    private List<OrderLine> orderLines;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantLogoImageUrl;
    private String customerProfileFirstName;
    private String customerProfileLastName;
    private Address customerProfileAddress;
}
