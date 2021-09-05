package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

@Data
public class UserInfoDto {

    private String email;
    private String firstName;
    private String lastName;
    private AddressDto address;
}
