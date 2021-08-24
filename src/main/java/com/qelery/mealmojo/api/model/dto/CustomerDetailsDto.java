package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

@Data
public class CustomerDetailsDto {

    private String email;
    private String firstName;
    private String lastName;
    private AddressDto address;
}
