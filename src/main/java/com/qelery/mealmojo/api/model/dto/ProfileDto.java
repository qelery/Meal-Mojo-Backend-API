package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.entity.Address;
import lombok.Data;

@Data
public class ProfileDto {

    private String firstName;
    private String lastName;
    private Address address;
}
