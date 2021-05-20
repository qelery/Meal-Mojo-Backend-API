package com.qelery.mealmojo.api.model.response;

import com.qelery.mealmojo.api.model.Address;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponse {

    private String JWT;
    private Address address;

    public LoginResponse(String JWT, Address address) {
        this.JWT = JWT;
        this.address = address;
    }

}
