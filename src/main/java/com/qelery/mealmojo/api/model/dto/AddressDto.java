package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.State;
import lombok.Data;

@Data
public class AddressDto {

    private String street1;
    private String street2;
    private String street3;
    private String city;
    private State state;
    private String zipcode;
    private Country country;
}
